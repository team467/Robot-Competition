package frc.robot.sensors.imu;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.Timer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class ImuBase extends GyroBase implements Imu {

  private static final double MEASURES_PER_DEGREE = 1.0;
  protected static final double timeout = 0.1;
  private static final double kCalibrationSampleTime = 5.0;

  protected NetworkTable table;
  protected NetworkTableEntry valueTableEntry;
  protected NetworkTableEntry pitchTableEntry;
  protected NetworkTableEntry rollTableEntry;
  protected NetworkTableEntry yawTableEntry;
  protected NetworkTableEntry accelXTableEntry;
  protected NetworkTableEntry accelYTableEntry;
  protected NetworkTableEntry accelZTableEntry;
  protected NetworkTableEntry angleXTableEntry;
  protected NetworkTableEntry angleYTableEntry;
  protected NetworkTableEntry angleZTableEntry;
  protected NetworkTableEntry magXTableEntry;
  protected NetworkTableEntry magYTableEntry;
  protected NetworkTableEntry magZTableEntry;
  protected NetworkTableEntry tempTableEntry;

  public enum AhrsAlgorithm {
    Complementary, 
    Madgwick
  }

  public enum Axis {
    kX, kY, kZ
  }

  // AHRS algorithm
  private AhrsAlgorithm algorithm;

  // AHRS yaw axis
  private Axis yawAxis;

  // gyro offset
  protected double gyroOffsetX = 0.0;
  protected double gyroOffsetY = 0.0;
  protected double gyroOffsetZ = 0.0;

  // last read values (post-scaling)
  protected double gyroX = 0.0;
  protected double gyroY = 0.0;
  protected double gyroZ = 0.0;
  protected double accelX = 0.0;
  protected double accelY = 0.0;
  protected double accelZ = 0.0;
  protected double magX = 0.0;
  protected double magY = 0.0;
  protected double magZ = 0.0;
  protected double temp = 0.0;
  protected double barometricPressure = 0.0;

  // accumulated gyro values (for offset calculation)
  protected int accumulatedCount = 0;
  protected double accumulatedGyroX = 0.0;
  protected double accumulatedGyroY = 0.0;
  protected double accumulatedGyroZ = 0.0;

  // integrated gyro values
  protected double integratedGyroX = 0.0;
  protected double integratedGyroY = 0.0;
  protected double integratedGyroZ = 0.0;

  // last sample time
  protected double lastSampleTime = 0.0;

  // Kalman (AHRS)
  private static final double gyroScale = 0.0174533; // rad/sec
  private static final double accelScale = 9.80665; // mg/sec/sec
  private static final double magScale = 0.1; // uTesla
  private double ahrsQuaternion1 = 1;
  private double ahrsQuaternion2 = 0;
  private double ahrsQuaternion3 = 0;
  private double ahrsQuaternion4 = 0;

  // Complementary AHRS
  private boolean first = true;
  private double previousGyroX;
  private double previousGyroY;
  private double previousGyroZ;
  private double previousMagAngle = 0.0;
  private boolean tiltCompYaw = true;

  // AHRS outputs
  private double yaw = 0.0;
  private double roll = 0.0;
  private double pitch = 0.0;

  protected AtomicBoolean freed = new AtomicBoolean(false);

  protected DigitalInput interrupt;

  // Sample from the IMU
  protected static class Sample {
    public double gyroX;
    public double gyroY;
    public double gyroZ;
    public double accelX;
    public double accelY;
    public double accelZ;
    public double magX;
    public double magY;
    public double magZ;
    public double dt;

    // Swap axis as appropriate for yaw axis selection
    public void adjustYawAxis(Axis yawAxis) {
      switch (yawAxis) {

        case kX: {
          // swap X and Z
          double tmp;
          tmp = accelX;
          accelX = accelZ;
          accelZ = tmp;
          tmp = magX;
          magX = magZ;
          magZ = tmp;
          tmp = gyroX;
          gyroX = gyroZ;
          gyroZ = tmp;
          break;
        }

        case kY: {
          // swap Y and Z
          double tmp;
          tmp = accelY;
          accelY = accelZ;
          accelZ = tmp;
          tmp = magY;
          magY = magZ;
          magZ = tmp;
          tmp = gyroY;
          gyroY = gyroZ;
          gyroZ = tmp;
          break;
        }

        case kZ:
        default:
          // no swap required
          break;
      }
    }
  }

  // Sample FIFO
  protected static final int samplesDepth = 10;
  protected final Sample[] samples;
  protected final Lock samplesMutex;
  protected final Condition samplesNotEmpty;
  protected int samplesCount = 0;
  protected int samplesTakeIndex = 0;
  protected int samplesPutIndex = 0;
  protected boolean calculateStarted = false;

  private static class AcquireTask implements Runnable {
    private ImuBase imu;

    public AcquireTask(ImuBase imu) {
      this.imu = imu;
    }

    @Override
    public void run() {
      imu.acquire();
    }
  }

  private static class CalculateTask implements Runnable {
    private ImuBase imu;

    public CalculateTask(ImuBase imu) {
      this.imu = imu;
    }

    @Override
    public void run() {
      imu.calculate();
    }
  }

  private Thread acquireTask;
  private Thread calculateTask;

  /**
   * Constructor.
   */
  protected ImuBase(Axis yawAxis, AhrsAlgorithm algorithm) {
    this.yawAxis = yawAxis;
    this.algorithm = algorithm;

    table = NetworkTableInstance.getDefault().getTable("Sensors on Pi");

    // Create data acq FIFO. We make the FIFO 2 longer than it needs
    // to be so the input and output never overlap (we hold a reference
    // to the output while the lock is released).
    samples = new Sample[samplesDepth + 2];
    for (int i = 0; i < samplesDepth + 2; i++) {
      samples[i] = new Sample();
    }
    samplesMutex = new ReentrantLock();
    samplesNotEmpty = samplesMutex.newCondition();

    // Configure interrupt on MXP DIO0 and start acquire task
    interrupt = new DigitalInput(10); // MXP DIO0
    acquireTask = new Thread(new AcquireTask(this));
    interrupt.requestInterrupts();
    interrupt.setUpSourceEdge(false, true);
    acquireTask.setDaemon(true);
    acquireTask.start();

    calibrate();

    // Start AHRS processing
    calculateTask = new Thread(new CalculateTask(this));
    calculateTask.setDaemon(true);
    calculateTask.start();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calibrate() {
    Timer.delay(0.1);

    synchronized (this) {
      accumulatedCount = 0;
      accumulatedGyroX = 0.0;
      accumulatedGyroY = 0.0;
      accumulatedGyroZ = 0.0;
    }

    Timer.delay(kCalibrationSampleTime);

    synchronized (this) {
      gyroOffsetX = accumulatedGyroX / accumulatedCount;
      gyroOffsetY = accumulatedGyroY / accumulatedCount;
      gyroOffsetZ = accumulatedGyroZ / accumulatedCount;
    }
  }

  /**
   * {@inheritDoc}
   */
  public void reset() {
    synchronized (this) {
      integratedGyroX = 0.0;
      integratedGyroY = 0.0;
      integratedGyroZ = 0.0;
    }
  }

  /**
   * Delete (free) the spi port used for the IMU.
   */
  @Override
  public void free() {
    freed.set(true);
    if (samplesMutex != null) {
      samplesMutex.lock();
      try {
        samplesNotEmpty.signal();
      } finally {
        samplesMutex.unlock();
      }
    }
    try {
      if (acquireTask != null) {
        acquireTask.join();
      }
      if (calculateTask != null) {
        calculateTask.join();
      }
    } catch (InterruptedException e) {
      System.err.println(e);
    }
    if (interrupt != null) {
      interrupt = null;
    }
  }

  protected abstract void acquire();

  private void calculate() {
    while (!freed.get()) {
      // Wait for next sample and get it
      Sample sample;
      samplesMutex.lock();
      try {
        calculateStarted = true;
        while (samplesCount == 0) {
          samplesNotEmpty.await();
          if (freed.get()) {
            return;
          }
        }
        sample = samples[samplesTakeIndex];
        samplesTakeIndex += 1;
        if (samplesTakeIndex == samples.length) {
          samplesTakeIndex = 0;
        }
        samplesCount -= 1;
      } catch (InterruptedException e) {
        break;
      } finally {
        samplesMutex.unlock();
      }

      switch (algorithm) {

        case Madgwick:
          calculateMadgwick(sample, 0.4);
          break;

        case Complementary:
        default:
          calculateComplementary(sample);
          break;

      }
    }
  }

  private void calculateMadgwick(Sample sample, double beta) {
    // Make local copy of quaternion and angle global state
    double q1;
    double q2;
    double q3;
    double q4;
    synchronized (this) {
      q1 = ahrsQuaternion1;
      q2 = ahrsQuaternion2;
      q3 = ahrsQuaternion3;
      q4 = ahrsQuaternion4;
    }

    // Swap axis as appropriate for yaw axis selection
    sample.adjustYawAxis(yawAxis);

    // Kalman calculation
    // Code originated from: https://decibel.ni.com/content/docs/DOC-18964
    do {
      // If true, only use gyros and magnetos for updating the filter.
      boolean excludeAccel = false;

      // Convert accelerometer units to m/sec/sec
      double ax = sample.accelX * accelScale;
      double ay = sample.accelY * accelScale;
      double az = sample.accelZ * accelScale;
      // Normalize accelerometer measurement
      double norm = Math.sqrt(ax * ax + ay * ay + az * az);
      if (norm > 0.3 && !excludeAccel) {
        // normal larger than the sensor noise floor during freefall
        norm = 1.0 / norm;
        ax *= norm;
        ay *= norm;
        az *= norm;
      } else {
        ax = 0;
        ay = 0;
        az = 0;
      }

      // Convert magnetometer units to uTesla
      double mx = sample.magX * magScale;
      double my = sample.magY * magScale;
      double mz = sample.magZ * magScale;
      // Normalize magnetometer measurement
      norm = Math.sqrt(mx * mx + my * my + mz * mz);
      if (norm > 0.0) {
        norm = 1.0 / norm;
        mx *= norm;
        my *= norm;
        mz *= norm;
      } else {
        break; // something is wrong with the magneto readouts
      }

      double q1Times2 = 2.0 * q1;
      double q2Times2 = 2.0 * q2;
      double q3Times2 = 2.0 * q3;
      double q4Times4 = 2.0 * q4;
      double q1TimesQ3Times2 = 2.0 * q1 * q3;
      double q3TimesQ4Times2 = 2.0 * q3 * q4;
      double q1Q1 = q1 * q1;
      double q1Q2 = q1 * q2;
      double q1Q3 = q1 * q3;
      double q1Q4 = q1 * q4;
      double q2Q2 = q2 * q2;
      double q2Q3 = q2 * q3;
      double q2Q4 = q2 * q4;
      double q3Q3 = q3 * q3;
      double q3Q4 = q3 * q4;
      double q4Q4 = q4 * q4;

      // Reference direction of Earth's magnetic field
      double q1Mx2 = 2 * q1 * mx;
      double q1My2 = 2 * q1 * my;
      double q1Mz2 = 2 * q1 * mz;
      double q2Mx2 = 2 * q2 * mx;

      double hx = mx * q1Q1 
          - q1My2 * q4 
          + q1Mz2 * q3 
          + mx * q2Q2 
          + q2Times2 * my * q3 
          + q2Times2 * mz * q4 
          - mx * q3Q3
          - mx * q4Q4;
      double hy = q1Mx2 * q4 
          + my * q1Q1 - q1Mz2 * q2 
          + q2Mx2 * q3 - my * q2Q2 
          + my * q3Q3 
          + q3Times2 * mz * q4
          - my * q4Q4;
      double bx2 = Math.sqrt(hx * hx + hy * hy);
      double bz2 = -q1Mx2 * q3 
          + q1My2 * q2 
          + mz * q1Q1 
          + q2Mx2 * q4 
          - mz * q2Q2 
          + q3Times2 * my * q4 
          - mz * q3Q3
          + mz * q4Q4;
      double bx4 = 2.0 * bx2;
      double bz4 = 2.0 * bz2;
      double bx8 = 2.0 * bx4;
      double bz8 = 2.0 * bz4;

      // Gradient descent algorithm corrective step
      double s1 = -q3Times2 * (2.0 * q2Q4 - q1TimesQ3Times2 - ax) 
          + q2Times2 * (2.0 * q1Q2 + q3TimesQ4Times2 - ay)
          - bz4 * q3 * (bx4 * (0.5 - q3Q3 - q4Q4) + bz4 * (q2Q4 - q1Q3) - mx)
          + (-bx4 * q4 + bz4 * q2) * (bx4 * (q2Q3 - q1Q4) + bz4 * (q1Q2 + q3Q4) - my)
          + bx4 * q3 * (bx4 * (q1Q3 + q2Q4) + bz4 * (0.5 - q2Q2 - q3Q3) - mz);
      double s2 = q4Times4 * (2.0 * q2Q4 - q1TimesQ3Times2 - ax) 
          + q1Times2 * (2.0 * q1Q2 + q3TimesQ4Times2 - ay)
          - 4.0 * q2 * (1.0 - 2.0 * q2Q2 - 2.0 * q3Q3 - az)
          + bz4 * q4 * (bx4 * (0.5 - q3Q3 - q4Q4) + bz4 * (q2Q4 - q1Q3) - mx)
          + (bx4 * q3 + bz4 * q1) * (bx4 * (q2Q3 - q1Q4) + bz4 * (q1Q2 + q3Q4) - my)
          + (bx4 * q4 - bz8 * q2) * (bx4 * (q1Q3 + q2Q4) + bz4 * (0.5 - q2Q2 - q3Q3) - mz);
      double s3 = -q1Times2 * (2.0 * q2Q4 - q1TimesQ3Times2 - ax) 
          + q4Times4 * (2.0 * q1Q2 + q3TimesQ4Times2 - ay)
          - 4.0 * q3 * (1.0 - 2.0 * q2Q2 - 2.0 * q3Q3 - az)
          + (-bx8 * q3 - bz4 * q1) * (bx4 * (0.5 - q3Q3 - q4Q4) + bz4 * (q2Q4 - q1Q3) - mx)
          + (bx4 * q2 + bz4 * q4) * (bx4 * (q2Q3 - q1Q4) + bz4 * (q1Q2 + q3Q4) - my)
          + (bx4 * q1 - bz8 * q3) * (bx4 * (q1Q3 + q2Q4) + bz4 * (0.5 - q2Q2 - q3Q3) - mz);
      double s4 = q2Times2 * (2.0 * q2Q4 - q1TimesQ3Times2 - ax) 
          + q3Times2 * (2.0 * q1Q2 + q3TimesQ4Times2 - ay)
          + (-bx8 * q4 + bz4 * q2) * (bx4 * (0.5 - q3Q3 - q4Q4) + bz4 * (q2Q4 - q1Q3) - mx)
          + (-bx4 * q1 + bz4 * q3) * (bx4 * (q2Q3 - q1Q4) + bz4 * (q1Q2 + q3Q4) - my)
          + bx4 * q2 * (bx4 * (q1Q3 + q2Q4) + bz4 * (0.5 - q2Q2 - q3Q3) - mz);

      norm = Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4);
      if (norm > 0.0) {
        norm = 1.0 / norm; // normalise gradient step
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;
      } else {
        break;
      }

      // Convert gyro units to rad/sec
      double gx = sample.gyroX * gyroScale;
      double gy = sample.gyroY * gyroScale;
      double gz = sample.gyroZ * gyroScale;

      // Compute rate of change of quaternion
      // then integrate to yield quaternion
      double quaternion1RateOfChange = 0.5 * (-q2 * gx - q3 * gy - q4 * gz) - beta * s1;
      q1 += quaternion1RateOfChange * sample.dt;

      double quaternion2RateOfChange = 0.5 * (q1 * gx + q3 * gz - q4 * gy) - beta * s2;
      q2 += quaternion2RateOfChange * sample.dt;

      double quaternion3RateOfChange = 0.5 * (q1 * gy - q2 * gz + q4 * gx) - beta * s3;
      q3 += quaternion3RateOfChange * sample.dt;

      double quaternion4RateOfChange = 0.5 * (q1 * gz + q2 * gy - q3 * gx) - beta * s4;
      q4 += quaternion4RateOfChange * sample.dt;

      norm = Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
      if (norm > 0.0) {
        norm = 1.0 / norm; // normalise quaternion
        q1 = q1 * norm;
        q2 = q2 * norm;
        q3 = q3 * norm;
        q4 = q4 * norm;
      }
    } while (false);

    // Convert quaternion to angles of rotation
    double xi = -Math.atan2(2 * q2 * q3 - 2 * q1 * q4, 2 * (q1 * q1) + 2 * (q2 * q2) - 1);
    double theta = -Math.asin(2 * q2 * q4 + 2 * q1 * q3);
    double rho = Math.atan2(2 * q3 * q4 - 2 * q1 * q2, 2 * (q1 * q1) + 2 * (q4 * q4) - 1);

    // Convert angles from radians to degrees
    xi = xi / Math.PI * 180.0;
    theta = theta / Math.PI * 180.0;
    rho = rho / Math.PI * 180.0;

    // Adjust angles for inverted mount of MXP sensor
    theta = -theta;
    if (rho < 0) {
      rho = 180 - Math.abs(rho);
    } else {
      rho = Math.abs(rho) - 180;
    }

    // Update global state
    synchronized (this) {
      ahrsQuaternion1 = q1;
      ahrsQuaternion2 = q2;
      ahrsQuaternion3 = q3;
      ahrsQuaternion4 = q4;
      this.yaw = xi;
      this.roll = theta;
      this.pitch = rho;
    }
  }

  // Thank you to the RoboBees for providing this elegant AHRS implementation
  // to the FIRST community!
  private void calculateComplementary(Sample sample) {
    // Description:
    // Accepts calibrated Rate Gyro, Accelerometer, and Magnetometer sensor
    // readings and applies a Complementary Filter to fuse them into a
    // single
    // composite sensor which provides accurate and stable rotation
    // indications
    // (Pitch, Roll, and Yaw). This sensor fusion approach effectively
    // combines the individual sensor's best respective properties while
    // mitigating their shortfalls.
    //
    // Design:
    // The Complementary Filter is an algorithm that allows a pair of
    // sensors
    // to contribute differently to a common, composite measurement result.
    // It effectively applies a low pass filter to one sensor, and a high
    // pass
    // filter to the other, then proportionally recombines them in such a
    // way
    // to maintain the original unit of measurement. It is computationally
    // inexpensive when compared to alternative estimation techniques such
    // as
    // the Kalman filter. The algorithm is given by:
    //
    // angle(n) = (alpha)*(angle(n-1) + gyrorate * dt) + (1-alpha)*(accel or
    // mag);
    //
    // where :
    //
    // alpha = tau / (tau + dt)
    //
    // This implementation uses the average Gyro rate across the dt period,
    // so
    // above gyrorate = [(gyrorate(n)-gyrorate(n-1)]/2
    //
    // Essentially, for Pitch and Roll, the slow moving (lower frequency)
    // part
    // of the rotation estimate is taken from the Accelerometer - ignoring
    // the
    // high noise level, and the faster moving (higher frequency) part is
    // taken
    // from the Rate Gyro - ignoring the slow Gyro drift. Same for Yaw,
    // except
    // that the Magnetometer replaces the Accelerometer to source the slower
    // moving component. This is because Pitch and Roll can be referenced to
    // the Accelerometer's sense of the Earth's gravity vector. Yaw cannot
    // be
    // referenced to this vector since this rotation does not cause any
    // relative angular change, but it can be referenced to magnetic North.
    // The parameter 'tau' is the time constant that defines the boundary
    // between the low and high pass filters. Both tau and the sample time,
    // dt, affect the parameter 'alpha', which sets the balance point for
    // how
    // much of which sensor is 'trusted' to contribute to the rotation
    // estimate.
    //
    // The Complementary Filter algorithm is applied to each X/Y/Z rotation
    // axis to compute R/P/Y outputs, respectively.
    //
    // Magnetometer readings are tilt-compensated when Tilt-Comp-(Yaw) is
    // asserted (True), by the IMU TILT subVI. This creates what is known as
    // a
    // tilt-compensated compass, which allows Yaw to be insensitive to the
    // effects of a non-level sensor, but generates error in Yaw during
    // movement (coordinate acceleration).
    //
    // The Yaw "South" crossing detector is necessary to allow a smooth
    // transition across the +/- 180 deg discontinuity (inherent in the ATAN
    // function). Since -180 deg is congruent with +180 deg, Yaw needs to
    // jump
    // between these values when crossing South (North is 0 deg). The design
    // depends upon comparison of successive Yaw readings to detect a
    // cross-over event. The cross-over detector monitors the current
    // reading
    // and evaluates how far it is from the previous reading. If it is
    // greater
    // than the previous reading by the Discriminant (= 180 deg), then Yaw
    // just
    // crossed South.
    //
    // By choosing 180 as the Discriminant, the only way the detector can
    // produce a false positive, assuming a loop iteration of 70 msec, is
    // for
    // it to rotate >2,571 dps ... (2,571=180/.07). This is faster than the
    // ST
    // L3GD20 Gyro can register. The detector produces a Boolean True upon
    // detecting a South crossing. This is used to alter the (n-1) Yaw which
    // was previously stored, either adding or subtracting 360 degrees as
    // required to place the previous Yaw in the correct quadrant whenever
    // crossing occurs. The Modulus function cannot be used here as the
    // Complementary Filter algorithm has 'state' (needs to remember
    // previous
    // Yaw).
    //
    // We are in effect stitching together two ends of a ruler for 'modular
    // arithmetic' (clock math).
    //
    // Inputs:
    // GYRO - Gyro rate and sample time measurements.
    // ACCEL - Acceleration measurements.
    // MAG - Magnetic measurements.
    // TAU ACC - tau parameter used to set sensor balance between Accel and
    // Gyro for Roll and Pitch.
    // TAU MAG - tau parameter used to set sensor balance between Mag and
    // Gyro
    // for Yaw.
    // TILT COMP (Yaw) - Enables Yaw tilt-compensation if True.
    //
    // Outputs:
    // ROLL - Filtered Roll about sensor X-axis.
    // PITCH - Filtered Pitch about sensor Y-axis.
    // YAW - Filtered Yaw about sensor Z-axis.
    //
    // Implementation:
    // It's best to establish the optimum loop sample time first. See IMU
    // READ
    // implementation notes for guidance. Each tau parameter should then be
    // adjusted to achieve optimum sensor fusion. tau acc affects Roll and
    // Pitch, tau mag affects Yaw. Start at value 1 or 2 and decrease by
    // half
    // each time until the result doesn't drift, but not so far that the
    // result
    // gets noisy. An optimum tau for this IMU is likely in the range of 1.0
    // to 0.01, for a loop sample time between 10 and 100 ms.
    //
    // Note that both sample timing (dt) and tau both affect the balance
    // parameter, 'alpha'. Adjusting either dt or tau will require the other
    // to be readjusted to maintain a particular filter performance.
    //
    // It is likely best to set Yaw tilt-compensation to off (False) if the
    // Yaw
    // value is to be used as feedback in a closed loop control application.
    // The tradeoff is that Yaw will only be accurate while the robot is
    // level.
    //
    // Since a Yaw of -180 degrees is congruent with +180 degrees (they
    // represent the same direction), it is possible that the Yaw output
    // will
    // oscillate between these two values when the sensor happens to be
    // pointing due South, as sensor noise causes slight variation. You will
    // need to account for this possibility if you are using the Yaw value
    // for
    // decision-making in code.
    //
    // ----- The RoboBees FRC Team 836! -----
    // Complement your passion to solve problems with a STEM Education!

    // Compensate for PCB-Up Mounting Config.
    sample.gyroY = -sample.gyroY;
    sample.gyroZ = -sample.gyroZ;
    sample.accelY = -sample.accelY;
    sample.accelZ = -sample.accelZ;
    sample.magY = -sample.magY;
    sample.magZ = -sample.magZ;

    // Swap axis as appropriate for yaw axis selection
    sample.adjustYawAxis(yawAxis);

    final double tauAccel = 0.95;
    final double tauMag = 0.04;

    double roll;
    double pitch;
    double yaw;
    boolean tiltCompYaw;
    synchronized (this) {
      roll = this.roll;
      pitch = this.pitch;
      yaw = this.yaw;
      tiltCompYaw = this.tiltCompYaw;
    }

    // Calculate mag angle in degrees
    double magAngle = Math.atan2(sample.magY, sample.magX) / Math.PI * 180.0;

    // Tilt compensation:
    // see http://www.freescale.com/files/sensors/doc/app_note/AN3461.pdf
    // for derivation of Pitch and Roll equations. Used eqs 37 & 38 as Rxyz.
    // Eqs 42 & 43, as Ryxz, produce same values within Pitch & Roll
    // constraints.
    //
    // Freescale's Pitch/Roll derivation is preferred over ST's as it does
    // not
    // degrade due to the Sine function linearity assumption.
    //
    // Pitch is accurate over +/- 90 degree range, and Roll is accurate
    // within
    // +/- 180 degree range - as long as accelerometer is only sensing
    // acceleration due to gravity. Movement (coordinate acceleration) will
    // add error to Pitch and Roll indications.
    //
    // Yaw is not obtainable from an accelerometer due to its geometric
    // relationship with the Earth's gravity vector. (Would have same
    // problem
    // on Mars.)
    //
    // see http://www.pololu.com/file/0J434/LSM303DLH-compass-app-note.pdf
    // for derivation of Yaw equation. Used eq 12 in Appendix A (eq 13 is
    // replaced by ATAN2 function). Yaw is obtainable from the magnetometer,
    // but is sensitive to any tilt from horizontal. This uses Pitch and
    // Roll
    // values from above for tilt compensation of Yaw, resulting in a
    // tilt-compensated compass.
    //
    // As with Pitch/Roll, movement (coordinate acceleration) will add error
    // to
    // Yaw indication.

    // Accel
    double tiltPitchRadians = Math.atan2(-sample.accelX,
        Math.sqrt(sample.accelY * sample.accelY + sample.accelZ * sample.accelZ));

    double tiltRollRadians = Math.atan2(sample.accelY,
        Math.sqrt(sample.accelX * sample.accelX * 0.01 + sample.accelZ * sample.accelZ)
            * Math.signum(sample.accelZ));

    // Mag
    double tiltYaw;
    if (tiltCompYaw) {
      double sinPitch = Math.sin(tiltPitchRadians);
      double cosPitch = Math.cos(tiltPitchRadians);
      double sinRoll = Math.sin(tiltRollRadians);
      double cosRoll = Math.cos(tiltRollRadians);
      double mx2 = sample.magX * cosPitch + sample.magZ * sinPitch;
      double my2 = sample.magX * sinRoll * sinPitch + sample.magY * cosRoll
          - sample.magZ * sinRoll * cosPitch;
      // double mz2 = -sample.mag_x * cos_roll * sin_pitch + sample.mag_y
      // * sin_roll + sample.mag_z * cos_roll * cos_pitch;
      tiltYaw = Math.atan2(my2, mx2) / Math.PI * 180.0;
    } else {
      tiltYaw = magAngle;
    }

    // Positive rotation of Magnetometer is clockwise when looking in + Z
    // direction. This is subtracted from 0 deg to reverse rotation
    // direction, as it needs to be aligned with the definition of positive
    // Gyroscope rotation, (which is CCW looking in + Z direction), to
    // enable
    // sensor fusion.
    //
    // 0 degrees is due magnetic North.
    tiltYaw = -tiltYaw;

    // "South" crossing Detector
    if (Math.abs(magAngle - previousMagAngle) >= 180) {
      if (previousMagAngle < 0) {
        yaw += -360;
      } else if (previousMagAngle > 0) {
        yaw += 360;
      }
    }
    previousMagAngle = magAngle;

    if (first) {
      previousGyroX = sample.gyroX;
      previousGyroY = sample.gyroY;
      previousGyroZ = sample.gyroZ;
      first = false;
    }

    // alpha = tau / (tau + dt)
    // gyrorate = [(gyrorate(n)-gyrorate(n-1)]/2
    // angle(n) = (alpha)*(angle(n-1) + gyrorate * dt) + (1-alpha)*(accel or
    // mag);
    double alphaAccel = tauAccel / (tauAccel + sample.dt);
    double alphaMag = tauMag / (tauMag + sample.dt);
    double tiltPitch = tiltPitchRadians / Math.PI * 180.0;
    double tiltRoll = tiltRollRadians / Math.PI * 180.0;
    roll = alphaAccel * (roll + sample.dt * (sample.gyroX + previousGyroX) / 2.0) 
        + (1 - alphaAccel) * tiltRoll;
    pitch = alphaAccel * (pitch + sample.dt * (sample.gyroY + previousGyroY) / 2.0) 
        + (1 - alphaAccel) * tiltPitch;
    yaw = alphaMag * (yaw + sample.dt * (sample.gyroZ + previousGyroZ) / 2.0) 
        + (1 - alphaMag) * tiltYaw;

    previousGyroX = sample.gyroX;
    previousGyroY = sample.gyroY;
    previousGyroZ = sample.gyroZ;

    // Update global state
    synchronized (this) {
      this.roll = roll;
      this.pitch = pitch;
      this.yaw = yaw;
    }
  }

  /**
   * {@inheritDoc}
   */
  public double getAngle() {
    return getYaw();
  }

  /**
   * {@inheritDoc}
   */
  public double getRate() {
    return getRateZ();
  }

  public synchronized double getAngleX() {
    return integratedGyroX;
  }

  public synchronized double getAngleY() {
    return integratedGyroY;
  }

  public synchronized double getAngleZ() {
    return integratedGyroZ;
  }

  public synchronized double getRateX() {
    return gyroX;
  }

  public synchronized double getRateY() {
    return gyroY;
  }

  public synchronized double getRateZ() {
    return gyroZ;
  }

  public synchronized double getAccelX() {
    return accelX;
  }

  public synchronized double getAccelY() {
    return accelY;
  }

  public synchronized double getAccelZ() {
    return accelZ;
  }

  public synchronized double getMagX() {
    return magX;
  }

  public synchronized double getMagY() {
    return magY;
  }

  public synchronized double getMagZ() {
    return magZ;
  }

  public synchronized double getPitch() {
    return pitch;
  }

  public synchronized double getRoll() {
    return roll;
  }

  public synchronized double getYaw() {
    return yaw;
  }

  public synchronized double getLastSampleTime() {
    return lastSampleTime;
  }

  public synchronized double getTemperature() {
    return temp;
  }

  // Get quaternion W for the Kalman AHRS.
  // Always returns 0 for the Complementary AHRS.
  public synchronized double getQuaternionW() {
    return ahrsQuaternion1;
  }

  // Get quaternion X for the Kalman AHRS.
  // Always returns 0 for the Complementary AHRS.
  public synchronized double getQuaternionX() {
    return ahrsQuaternion2;
  }

  // Get quaternion Y for the Kalman AHRS.
  // Always returns 0 for the Complementary AHRS.
  public synchronized double getQuaternionY() {
    return ahrsQuaternion3;
  }

  // Get quaternion Z for the Kalman AHRS.
  // Always returns 0 for the Complementary AHRS.
  public synchronized double getQuaternionZ() {
    return ahrsQuaternion4;
  }

  /**
   * Enable or disable yaw tilt-compensation for the Complementary AHRS.
   * Has no effect on the Kalman AHRS.
   *
   * <p>It is likely best to set Yaw tilt-compensation to off (False) if the Yaw
   * value is to be used as feedback in a closed loop control application.
   * The tradeoff is that Yaw will only be accurate while the robot is level.
   */
  public synchronized void setTiltCompYaw(boolean enabled) {
    tiltCompYaw = enabled;
  }

  public void initTable() {
    table = NetworkTableInstance.getDefault().getTable("Sensors on Pi");
    valueTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    pitchTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    rollTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    yawTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    accelXTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    accelYTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    accelZTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    angleXTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    angleYTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
    angleZTableEntry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateTable() {
    if (table != null) {
      valueTableEntry.setDouble(getAngle());
      pitchTableEntry.setDouble(getPitch());
      rollTableEntry.setDouble(getRoll());
      yawTableEntry.setDouble(getYaw());
      accelXTableEntry.setDouble(getAccelX());
      accelYTableEntry.setDouble(getAccelY());
      accelZTableEntry.setDouble(getAccelZ());
      angleXTableEntry.setDouble(getAngleX());
      angleYTableEntry.setDouble(getAngleY());
      angleZTableEntry.setDouble(getAngleZ());
    }
  }

  public double getMeasuresPerDegree() {
    return MEASURES_PER_DEGREE;
  }

  @Override
  public double getBarometricPressure() {
    return barometricPressure;
  }

}
