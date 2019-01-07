package frc.robot.sensors.imu;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.InterruptableSensorBase;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class is for the ADIS16448 IMU that connects to the RoboRIO MXP port.
 */
public class Adis16448Imu extends ImuBase implements Imu {

  private static final double degreePerSecondPerLsb = 1.0 / 25.0;
  private static final double gPerLsb = 1.0 / 1200.0;
  private static final double milligaussPerLsb = 1.0 / 7.0;
  private static final double millibarPerLsb = 0.02;
  private static final double kDegCPerLSB = 0.07386;
  private static final double kDegCOffset = 31;
  private static final int globCmd = 0x3E;
  private static final int registerSimplePrd = 0x36;
  private static final int registerSensorAverage = 0x38;
  private static final int registerMiscControl = 0x34;
  private static final int registerProductId = 0x56;
  private static final int registerXGyroOff = 0x1A;

  private SPI spi;

  /**
   * Constructor.
   */
  public Adis16448Imu(Axis yawAxis, AhrsAlgorithm algorithm) {
    super(yawAxis, algorithm);

    spi = new SPI(SPI.Port.kMXP);
    spi.setClockRate(1000000);
    spi.setMSBFirst();
    spi.setClockActiveLow();
    spi.setChipSelectActiveLow();

    readRegister(registerProductId); // dummy read

    // Validate the product ID
    if (readRegister(registerProductId) != 16448) {
      spi = null;
      DriverStation.reportError("could not find ADIS16448", false);
      return;
    }

    // Set IMU internal decimation to 204.8 SPS
    writeRegister(registerSimplePrd, 201);

    // Enable Data Ready (LOW = Good Data) on DIO1 (PWM0 on MXP) & PoP
    writeRegister(registerMiscControl, 0x44);

    // Configure IMU internal Bartlett filter
    writeRegister(registerSensorAverage, 400);

    // If we have offset data, use it. Otherwise need to generate it.
    if (readRegister(registerXGyroOff) == 0) {
      calibrate();
    }

  }

  /*
   * Constructor assuming Complementary AHRS algorithm.
   */
  public Adis16448Imu(Axis yawAxis) {
    this(yawAxis, AhrsAlgorithm.Complementary);
  }

  /*
   * Constructor assuming yaw axis is "Z" and Complementary AHRS algorithm.
   */
  public Adis16448Imu() {
    this(Axis.kZ, AhrsAlgorithm.Complementary);
  }

  private int readRegister(int reg) {
    ByteBuffer buf = ByteBuffer.allocateDirect(2);
    buf.order(ByteOrder.BIG_ENDIAN);
    buf.put(0, (byte) (reg & 0x7f));
    buf.put(1, (byte) 0);

    spi.write(buf, 2);
    spi.read(false, buf, 2);

    return ((int) buf.getShort(0)) & 0xffff;
  }

  private void writeRegister(int reg, int val) {
    ByteBuffer buf = ByteBuffer.allocateDirect(2);
    // low byte
    buf.put(0, (byte) (0x80 | reg));
    buf.put(1, (byte) val);
    spi.write(buf, 2);
    // high byte
    buf.put(0, (byte) (0x81 | reg));
    buf.put(1, (byte) (val >> 8));
    spi.write(buf, 2);
  }

  protected void acquire() {
    ByteBuffer cmd = ByteBuffer.allocateDirect(26);
    cmd.put(0, (byte) globCmd);
    cmd.put(1, (byte) 0);

    ByteBuffer resp = ByteBuffer.allocateDirect(26);
    resp.order(ByteOrder.BIG_ENDIAN);

    synchronized (this) {
      lastSampleTime = Timer.getFPGATimestamp();
    }

    while (!freed.get()) {
      if (interrupt.waitForInterrupt(timeout) == InterruptableSensorBase.WaitResult.kTimeout) {
        continue;
      }

      double sampleTime = interrupt.readFallingTimestamp();
      double dt;
      synchronized (this) {
        dt = sampleTime - lastSampleTime;
        lastSampleTime = sampleTime;
      }

      spi.transaction(cmd, resp, 26);

      double gyroX = resp.getShort(4) * degreePerSecondPerLsb;
      double gyroY = resp.getShort(6) * degreePerSecondPerLsb;
      double gyroZ = resp.getShort(8) * degreePerSecondPerLsb;
      double accelX = resp.getShort(10) * gPerLsb;
      double accelY = resp.getShort(12) * gPerLsb;
      double accelZ = resp.getShort(14) * gPerLsb;
      double magX = resp.getShort(16) * milligaussPerLsb;
      double magY = resp.getShort(18) * milligaussPerLsb;
      double magZ = resp.getShort(20) * milligaussPerLsb;
      double barometricPressure = resp.getShort(22) * millibarPerLsb;
      double temp = resp.getShort(24) * kDegCPerLSB + kDegCOffset;

      samplesMutex.lock();
      try {
        // If the FIFO is full, just drop it
        if (calculateStarted && samplesCount < samplesDepth) {
          Sample sample = samples[samplesPutIndex];
          sample.gyroX = gyroX;
          sample.gyroY = gyroY;
          sample.gyroZ = gyroZ;
          sample.accelX = accelX;
          sample.accelY = accelY;
          sample.accelZ = accelZ;
          sample.magX = magX;
          sample.magY = magY;
          sample.magZ = magZ;
          sample.dt = dt;
          samplesPutIndex += 1;
          if (samplesPutIndex == samples.length) {
            samplesPutIndex = 0;
          }
          samplesCount += 1;
          samplesNotEmpty.signal();
        }
      } finally {
        samplesMutex.unlock();
      }

      // Update global state
      synchronized (this) {
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.magX = magX;
        this.magY = magY;
        this.magZ = magZ;
        this.barometricPressure = barometricPressure;
        this.temp = temp;

        accumulatedCount += 1;
        accumulatedGyroX += gyroX;
        accumulatedGyroY += gyroY;
        accumulatedGyroZ += gyroZ;

        integratedGyroX += (gyroX - gyroOffsetX) * dt;
        integratedGyroY += (gyroY - gyroOffsetY) * dt;
        integratedGyroZ += (gyroZ - gyroOffsetZ) * dt;
      }
    }
  }

}
