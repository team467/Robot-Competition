package frc.robot.simulator.drive;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.RobotMap;
import frc.robot.utilities.MovingAverage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PhysicalMotor implements Runnable, PIDOutput, PIDSource {

  private static final Logger LOGGER = LogManager.getLogger(PhysicalMotor.class);
  
  private static final DecimalFormat df = new DecimalFormat("#0.0000");

  static ArrayList<PhysicalMotor> motors = new ArrayList<PhysicalMotor>();
  static ArrayList<Thread> threads = new ArrayList<Thread>();

  // Simulation Information
  public static final double MAX_RPM = 821;

  public static final int MOTOR_TIME_SLICE_IN_MS = 2;

  // Iteration period is 20 ms
  public static final double ROBOT_TIME_SLICE_IN_MS = 20.0;

  private static final double MAX_SPEED_PER_PERIOD = MAX_RPM
  / (60.0 * 1000.0 / MOTOR_TIME_SLICE_IN_MS);

  static final double ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL = 1;

  private double openLoopRampRateSecondsToFull = ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL;
  private double closedLoopRampRateSecondsToFull = ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL;

  // Simulation paramters
  private double openLoopRampRatePerPeriod;
  private double closedLoopRampRatePerPeriod;

  public static double ROBOT_MAX_THEORETICAL_VOLTAGE = 12.0;

  private static double ROBOT_MAX_POWER_IN_WATTS = 480.0;

  // TODO: Move to battery simulator. Shoud eventually measure current and reduce
  private static double batteryVoltage = 12.0;

  private boolean enabled;

  private double voltage;
  private double maxVoltage = 12.0;
  private double neutralDeadband = 0.04;

  private double simulatedTargetVelocity;

  private double simulationVelocity;

  private boolean isOpenLoop = true;

  // Sensor stuff
  private static final int NUMBER_CLOSED_LOOPS = 2;
  private FeedbackDevice[] feedbackDeviceType = new FeedbackDevice[NUMBER_CLOSED_LOOPS];
  private double revolutions = 0.0; // keep track of partial ticks
  private int[] sensors = new int[NUMBER_CLOSED_LOOPS];
  private int closedLoopIndex = 0;

  private int smoothedVelocity[] = new int[NUMBER_CLOSED_LOOPS];
  private int iterationCount = 0; // Used for velocity smoothing
  private VelocityMeasPeriod velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
  private int velocityMeasurementWindowSize = 64;
  private MovingAverage[] smoothedVelocityMeasurement = new MovingAverage[NUMBER_CLOSED_LOOPS];
  private int[][] previousSensorReadings;

  // These are the parameters for the simulated internal PID controller.

  /**
   * The Talon has an internal PID controller. 
   * This simulation uses the WPI Library implimentation;
   */
  private PIDController[] pidController = new PIDController[NUMBER_CLOSED_LOOPS];
  private PIDSourceType pidSourceType = PIDSourceType.kDisplacement;
  private static final int NUMBER_SAVE_SLOTS = 4;
  private ClosedLoopParameter[] closedLoopParameters = new ClosedLoopParameter[NUMBER_SAVE_SLOTS];
  private int pidParamSlotIndex[] = new int[NUMBER_CLOSED_LOOPS];

  // General motor information
  private int id = 0;
  private ControlMode controlMode = ControlMode.PercentOutput;

  static PhysicalMotor createMotor(int id) {
    // System.err.println("Creating motor ID: " + id);
    PhysicalMotor motor = new PhysicalMotor(id);
    motors.add(motor);

    Thread motorThread = new Thread(motor);
    motorThread.start();
    threads.add(motorThread);

    return motor;
  }

  PhysicalMotor(int id) {
    this.id = id;
    voltage = 0.0;
    simulationVelocity = 0.0;
    simulatedTargetVelocity = 0.0;
    isOpenLoop = true;

    // Period is half the motor thread time to allow for the PID control to update based inputs
//    double pidPeriod = ((double)MOTOR_TIME_SLICE_IN_MS/1000.0/2);
    double pidPeriod = 0.10;
    pidSourceType = PIDSourceType.kDisplacement;
    for (int i = 0; i < closedLoopParameters.length; i++) {
      closedLoopParameters[i] = new ClosedLoopParameter();
      // TODO: Load from file to emulate saving
    }

    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      sensors[i] = 0;
      smoothedVelocity[i] = 0;
      pidParamSlotIndex[i] = i;
      pidController[i] = new PIDController(
        closedLoopParameters[pidParamSlotIndex[i]].propGain, closedLoopParameters[pidParamSlotIndex[i]].integral, 
        closedLoopParameters[pidParamSlotIndex[i]].derivative, closedLoopParameters[pidParamSlotIndex[i]].feedForward, 
        this /* PID Source */, this /* PID Output */, pidPeriod);
      pidController[i].setOutputRange(-1.0, 1.0);
      pidController[i].setName("Simulated Motor " + id, "PID Controller: " + i);
    }
    
    revolutions = 0.0;
    iterationCount = 0; // Used for velocity smoothing
    configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms);
    configVelocityMeasurementWindow(64);
  
    // Min output should almost always be zero (stopped)
    nominalPercentOutputReverse = 0.0;
    nominalPercentOutputForward = 0.0;

    // Ramp rates control accelleration AND decelleration
    openLoopRampRatePerPeriod 
        = convertRampRateToPerPeriod(ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL); 
    closedLoopRampRatePerPeriod 
        = convertRampRateToPerPeriod(ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL); 
    
    enabled = true;
  }

  static void enableMotors() {
    for (PhysicalMotor motor : motors) {
      motor.enabled = true;
    }
    for (Thread thread : threads) {
      thread.start();
    }
  }

  static void shutdown() {
    for (PhysicalMotor motor : motors) {
      // Need to disable the motors for their threads to exit.
      motor.enabled = false;
    }

    for (Thread motorThread : threads) {
      try {
        // Joins are the best way to shutdown threads as they wait for locks to be freed on their own.
        motorThread.join();
      } catch (InterruptedException e) {
        LOGGER.error("SIMULATOR|DRIVE", e);
        e.printStackTrace();
      }
    }

    motors.clear();
    threads.clear();
  }

  ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int index) {
    if (index < 0 || index >= NUMBER_CLOSED_LOOPS) {
      return ErrorCode.SensorNotPresent;
    }
    closedLoopIndex = index;
    feedbackDeviceType[index] = feedbackDevice;
    return ErrorCode.OK;
  }

  ErrorCode configAllowableClosedLoopError(int slotIndex, int allowableClosedLoopError) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      return ErrorCode.InvalidParamValue;
    }
    this.closedLoopParameters[slotIndex].allowableClosedLoopError = allowableClosedLoopError;
    return ErrorCode.OK;
  }

  /**
   * Sets the maximum integral accumulator in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param iaccum
   *            Value of the maximum integral accumulator (closed loop error
   *            units X 1ms).
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configMaxIntegralAccumulator(int slotIndex, double iaccum) {
    // TODO
    return ErrorCode.OK;
  }

  /**
   * Sets the peak closed-loop output.  This peak output is slot-specific and
   *   is applied to the output of the associated PID loop.
   * This setting is seperate from the generic Peak Output setting.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param percentOut
   *            Peak Percent Output from 0 to 1.  This value is absolute and
   *            the magnitude will apply in both forward and reverse directions.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut) {
   // TODO
   return ErrorCode.OK;
}

  /**
   * Sets the loop time (in milliseconds) of the PID closed-loop calculations.
   * Default value is 1 ms.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param loopTimeMs
   *            Loop timing of the closed-loop calculations.  Minimum value of
   *            1 ms, maximum of 64 ms.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs) {
    // TODO
    return ErrorCode.OK;
  }

  /**
   * Sets the integral accumulator. Typically this is used to clear/zero the
   * integral accumulator, however some use cases may require seeding the
   * accumulator for a faster response.
   *
   * @param iaccum
   *            Value to set for the integral accumulator (closed loop error
   *            units X 1ms).
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode integralAccumulator(double iaccum, int closedLoopIndex) {
        // TODO
    return ErrorCode.OK;
  }

  /**
   * Gets the iaccum value.
   *
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return Integral accumulator value (Closed-loop error X 1ms).
   */
  double integralAccumulator(int closedLoopIndex) {
    // TODO: Figure out
    return 0.0;
  }


  /**
   * Gets the derivative of the closed-loop error.
   *
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return The error derivative value.
   */
  double errorDerivative(int closedLoopIndex) {
    return 0.0;
    // TODO: FIgure out which is derivative term
//    return pidController[closedLoopIndex].getD;
  }

  /**
   * Gets the current target of a given closed loop.
   *
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return The closed loop target.
   */
  int closedLoopTarget(int closedLoopIndex) {
    return (int) (pidController[closedLoopIndex].getSetpoint() 
        * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION);
  }

  /**
   * Rarely used feature that sets a minimum output for a motor.
   * 
   * @param percentOutput Sets the minimum reverse output 
   *    as a percent of bettery voltage
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configNominalOutputReverse(double percentOutput) {
    if (percentOutput < -1.0 || percentOutput > 0.0) {
      return ErrorCode.InvalidParamValue;
    }
    nominalPercentOutputReverse = percentOutput;
    return ErrorCode.OK;
  }
  
  /**
   * Rarely used feature that sets a minimum output for a motor.
   * 
   * @param percentOutput Sets the minimum forward output 
   *    as a percent of bettery voltage
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configNominalOutputForward(double percentOutput) {
    if (percentOutput < 0.0 || percentOutput > 1.0) {
      return ErrorCode.InvalidParamValue;
    }
    nominalPercentOutputForward = percentOutput;
    return ErrorCode.OK;
  }

  private double nominalPercentOutputForward = 0.0;
  private double nominalPercentOutputReverse = 0.0;
  private double peakPercentOutputForward = 1.0;
  private double peakPercentOutputReverse = -1.0;

  ErrorCode configPeakOutputForward(double percentOutput) {
    if (percentOutput < 0.0 || percentOutput > 1.0) {
      return ErrorCode.InvalidParamValue;
    }
    peakPercentOutputForward= percentOutput; 
    return ErrorCode.OK;
  }
  
  ErrorCode configPeakOutputReverse(double percentOutput) {
    if (percentOutput < -1.0 || percentOutput > 0.0) {
      return ErrorCode.InvalidParamValue;
    }
    peakPercentOutputReverse = percentOutput;
    return ErrorCode.OK;
  }

  ErrorCode configOpenLoopRamp(double secondsFromNeutralToFull) {
    if (secondsFromNeutralToFull < 0) {
      return ErrorCode.InvalidParamValue;
    }
    openLoopRampRateSecondsToFull = secondsFromNeutralToFull;
    openLoopRampRatePerPeriod = convertRampRateToPerPeriod(openLoopRampRateSecondsToFull); 
    return ErrorCode.OK;
  }
  
  ErrorCode configClosedLoopRamp(double secondsFromNeutralToFull) {
    if (secondsFromNeutralToFull < 0) {
      return ErrorCode.InvalidParamValue;
    }
    closedLoopRampRateSecondsToFull = secondsFromNeutralToFull;
    closedLoopRampRatePerPeriod = convertRampRateToPerPeriod(closedLoopRampRateSecondsToFull); 
    return ErrorCode.OK;
  }

  /**
   * Converts a ramp rate from per second to per period. Should be called when motor is 
   * created or a ramp rate is configured.
   * 
   * @param secondsFromNeutralToFull the time it takes to get to full velocity from stop
   * @return the ramp rate per motor time slice period
   */
  private double convertRampRateToPerPeriod(double secondsFromNeutralToFull) {
    if (secondsFromNeutralToFull == 0) {
      // If zero, use the maximum acceleration
      secondsFromNeutralToFull = ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL;
    } 
    // Divide the fastest velocity by the number of time slices to get to full velocity
    return MAX_SPEED_PER_PERIOD 
        / (secondsFromNeutralToFull * 1000.0 / MOTOR_TIME_SLICE_IN_MS);
  }

  ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod velocityMeasurementPeriod) {
    synchronized(this) {
      this.velocityMeasurementPeriod = velocityMeasurementPeriod;
      previousSensorReadings = new int[NUMBER_CLOSED_LOOPS][velocityMeasurementPeriod.value];
      for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
        for (int j = 0; j < velocityMeasurementPeriod.value; j++) {
          previousSensorReadings[i][j] = 0;
        }
      }
    }
    return ErrorCode.OK;
  }
  
  ErrorCode configVelocityMeasurementWindow(int windowSize) {
    // Valid options are 1, 2, 4, 8, 16, 32
    if (windowSize < 1) {
      return ErrorCode.InvalidParamValue;
    } else {
      velocityMeasurementWindowSize = 32;
      for (int possibleSize = 2; possibleSize <= 32; possibleSize *= 2) {
        if (windowSize < possibleSize) {
          velocityMeasurementWindowSize = possibleSize / 2;
          break;
        }
      }
    }
    synchronized(this) {
      for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
        smoothedVelocityMeasurement[i] = new MovingAverage(velocityMeasurementWindowSize);
      }
    }
    return ErrorCode.OK;
  }

  /**
   * Sets the 'P' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param proportionalGain
   *            Value of the P constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configProportionalGain(int slotIndex, double proportionalGain) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      return ErrorCode.InvalidParamValue;
    }
    closedLoopParameters[slotIndex].propGain = proportionalGain;
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setP(proportionalGain);
      }
    }
    return ErrorCode.OK;
  }
  
  /**
   * Sets the 'I' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param iCoefficient
   *            Value of the integral coefficient.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configICoefficient(int slotIndex, double iCoefficient) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      return ErrorCode.InvalidParamValue;
    }
    closedLoopParameters[slotIndex].integral = iCoefficient;
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setI(iCoefficient);
      }
    }
    return ErrorCode.OK;
  }

  /**
   * Sets the 'D' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param dCoefficient
   *            Value of the differential coefficient.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configDCoefficient(int slotIndex, double dCoefficient) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      return ErrorCode.InvalidParamValue;
    }
    this.closedLoopParameters[slotIndex].derivative = dCoefficient;
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setD(dCoefficient);
      }
    }
    return ErrorCode.OK;
  }
  
  /**
   * Sets the 'F' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param fCoefficient
   *            Value of the feed forward constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configFCoefficient(int slotIndex, double fCoefficient) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      return ErrorCode.InvalidParamValue;
    }
    this.closedLoopParameters[slotIndex].feedForward = fCoefficient;
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setF(fCoefficient);
      }
    }
    return ErrorCode.OK;
  }

  /**
   * Configures the output deadband percentage.
   *
   * @param percentDeadband
   *            Desired deadband percentage. Minimum is 0.1%, Maximum is 25%.
   *            Pass 0.04 for 4% (factory default).
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configNeutralDeadband(double percentDeadband) {
    if (percentDeadband < 0.001 || percentDeadband > 0.25) {
      return ErrorCode.InvalidParamValue;
    }
    neutralDeadband = percentDeadband;
    return ErrorCode.OK;
  }
  
  /**
   * Selects which profile slot to use for closed-loop control.
   *
   * @param slotIndex
   *            Profile slot to select.
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   **/
  void selectProfileSlot(int slotIndex, int closedLoopIndex) {
    this.pidParamSlotIndex[closedLoopIndex] = slotIndex;
  }

  void set4(ControlMode controlMode, double demand0, double demand1, DemandType demand1Type) {

    double totalDemand = demand0 + demand1;
    this.controlMode = controlMode;

    switch (controlMode) {

      // TODO: Fill in mode conversions
      case Current:
        isOpenLoop = true;
        break;

      case Disabled:
        isOpenLoop = true;
        voltage(0);
        pidController[closedLoopIndex].disable();;
        enabled = false;
        break;

      case Follower:
        isOpenLoop = true;
        break;

      case MotionMagic:
        isOpenLoop = true;
        break;

      case MotionProfile:
        isOpenLoop = true;
        break;

      case MotionProfileArc:
        isOpenLoop = true;
        break;

      case PercentOutput:
        isOpenLoop = true;
        voltage(totalDemand * maxVoltage);
        pidController[closedLoopIndex].disable();
        break;

      case Position:
        isOpenLoop = false;
        pidController[closedLoopIndex].setSetpoint(totalDemand);
        pidController[closedLoopIndex].enable();
        break;

      case Velocity:
        isOpenLoop = false;
        pidController[closedLoopIndex].setSetpoint(totalDemand);
        pidController[closedLoopIndex].enable();
        break;

      default:
        voltage(0);
    }
  }

  /**
   * Sets the voltage level of the motor. 
   * 
   * @param voltage the output level of the motor
   * @return the motor for chaining commands
   */
  PhysicalMotor voltage(double voltage) {

    if (Math.abs(voltage) <= (neutralDeadband * maxVoltage)) {
      voltage = 0.0;
      // Not that if there is a nominal output, the voltage will be raised to 
      // nominal even if the input is within the deadband.
    }

    if (voltage < 0) {
      if (voltage > (nominalPercentOutputReverse * batteryVoltage)) {
        voltage = nominalPercentOutputReverse * batteryVoltage;
      }
      if (voltage < (peakPercentOutputReverse * batteryVoltage)) {
        voltage = peakPercentOutputReverse * batteryVoltage;
      }
    } else {
      if (voltage < (nominalPercentOutputForward * batteryVoltage)) {
        voltage = nominalPercentOutputForward * batteryVoltage;
      }
      if (voltage > (peakPercentOutputForward * batteryVoltage)) {
        voltage = peakPercentOutputForward * batteryVoltage;
      }
    }

    // This makes sure we don't change some of the parameters while the run thread is moving
    
    synchronized(this) {

      if (Math.abs(voltage) < batteryVoltage) {
        this.voltage = voltage;
      } else {
        this.voltage = Math.signum(voltage) * batteryVoltage;
      }
      simulatedTargetVelocity 
          = (this.voltage / ROBOT_MAX_THEORETICAL_VOLTAGE) * MAX_SPEED_PER_PERIOD;
      // System.err.println("Simulated Target Velocity: " + simulatedTargetVelocity);
    }

    LOGGER.trace("", "Set voltage: {} ROBOT_MAX_THEORETICAL_VOLTAGE: {} "
        + "MAX_SPEED_PER_PERIOD: {} target velocity = {}", 
        voltage, ROBOT_MAX_THEORETICAL_VOLTAGE, 
        MAX_SPEED_PER_PERIOD, simulatedTargetVelocity);
    
    return this;
  }

  /**
   * Gets the current voltage setting of the motor.
   * 
   * @return the power level in voltage
   */
  double voltage() {
    return voltage;
  }

  /**
   * Returns the velocity of the motor and attached simulated wheels or equipment.
   * Normally this should be attached to a sensor simulator for feedback.
   * 
   * @return the velocity of the motor. Assumes ungeared.
   */
  double velocity() {
    return simulationVelocity;
  }

  /**
   * Gets the position for the primary or secondary closed-loop.
   * 
   * @param closedLoopIndex 0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return the position in sensor ticks
   */
  int position(int closedLoopIndex) {
    if (closedLoopIndex < 0 || closedLoopIndex >= NUMBER_CLOSED_LOOPS) {
      return 0;
    }
    return sensors[closedLoopIndex];
  }

  /**
   * Sets the sensor position to the given value. Useful if resetting the sensor or
   * if there is an required offset.
   * 
   * @param sensorPosition
   *            Position to set for the selected sensor (in raw sensor units).
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode setPosition(int closedLoopIndex, int sensorPosition) {
    if (closedLoopIndex < 0 || closedLoopIndex >= NUMBER_CLOSED_LOOPS) {
      return ErrorCode.InvalidParamValue;
    }
    sensors[closedLoopIndex] = sensorPosition;
    revolutions = sensorPosition / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    return ErrorCode.OK;
  }

  /**
   * Gets the velocity for the primary or secondary closed-loop.
   * 
   * @param closedLoopIndex 0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return the velocity in sensor ticks per 100ms
   */
  int velocity(int closedLoopIndex) {
    if (closedLoopIndex < 0 || closedLoopIndex >= NUMBER_CLOSED_LOOPS) {
      return 0;
    }
    return smoothedVelocity[closedLoopIndex];
  }

  /**
   * Gets the closed-loop error. The units depend on which control mode is in
   * use. See Phoenix-Documentation information on units.
   *
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return Closed-loop error value.
   */
  int closedLoopError(int closedLoopIndex) {
    if (closedLoopIndex < 0 || closedLoopIndex >= NUMBER_CLOSED_LOOPS) {
      return 0;
    }
    if (this.controlMode == ControlMode.Position) {
      return (int) pidController[closedLoopIndex].getError();
//      return (targetPosition - position(closedLoopIndex));
    } else if (controlMode == ControlMode.Velocity)  {
      int targetSensorVelocity = 
          (int) (simulatedTargetVelocity * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION);
      return (targetSensorVelocity  - velocity(closedLoopIndex));
    } else {
      return 0; // Not in (supported) closed loop, so no error;
    }
  }

  /**
   * Simulates the motor, including current velocity.
   */
  @Override
  public void run() {

    while(enabled) {
      
      try {
        double velocityDiffFromTarget = simulatedTargetVelocity - simulationVelocity;

        // LOGGER.debug("SIMULATOR|DRIVE", "Target Velocity: {} Current Velocity: {} Diff: {}", 
        //     df.format(simulatedTargetVelocity), df.format(simulationVelocity), df.format(velocityDiffFromTarget));
        
        // Make sure it's not in the middle of a change call
        synchronized(this) {
          iterationCount++;
          double rampRate;
          if (isOpenLoop) {
            rampRate = openLoopRampRatePerPeriod;
          } else {
            rampRate = closedLoopRampRatePerPeriod;
            // System.err.println("Ramp Rate: " + rampRate);
          }

          if (Math.abs(velocityDiffFromTarget) > rampRate) {
            simulationVelocity += Math.signum(velocityDiffFromTarget) * rampRate;
          } else {
            simulationVelocity = simulatedTargetVelocity;
          }
        }

        // Speed is in rotations/time slice, sensor ticks multiple by ticks per revolution
        revolutions += simulationVelocity;
        sensors[closedLoopIndex] = (int) (revolutions * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION); 
            // truncation is OK
        smoothedVelocity[closedLoopIndex] = 
            (int) (smoothedVelocityMeasurement[closedLoopIndex].average(
            sensors[closedLoopIndex] - previousSensorReadings[closedLoopIndex]
              [iterationCount % velocityMeasurementPeriod.value])
            / velocityMeasurementPeriod.value * 100); // convert to 100 ms
        previousSensorReadings[closedLoopIndex][iterationCount % velocityMeasurementPeriod.value] = sensors[closedLoopIndex];
        // System.err.println("Analog revs: " + revolutions + " digital ticks: " + sensors[selectedSensor]
        //     + " smoothed velocity: " + smoothedVelocity);

        Thread.sleep(MOTOR_TIME_SLICE_IN_MS);
      } catch (InterruptedException e) {
        LOGGER.error("SIMULATOR|DRIVE", e);
        e.printStackTrace();
      }
    }
  }

  @Override
  public void pidWrite(double output) {
    if (controlMode == ControlMode.Position || controlMode == ControlMode.Velocity) {
      // System.err.println(" Output " + output);
      voltage(output * maxVoltage);
    }
  }

  @Override
  public void setPIDSourceType(PIDSourceType pidSourceType) {
    this.pidSourceType = pidSourceType;
  }

  @Override
  public PIDSourceType getPIDSourceType() {
    return pidSourceType;
  }

  @Override
  public double pidGet() {
    if (controlMode == ControlMode.Position) {
      // System.err.println("PID Get: " + position(closedLoopIndex));
      return (position(closedLoopIndex));
    } else if (controlMode == ControlMode.Velocity) {
      return velocity(closedLoopIndex);
    } else {
      return 0.0;
    }
  }

    // ------ General Status ----------//
  /**
   * Gets the bus voltage seen by the device.
   *
   * @return The bus voltage value (in volts).
   */
  double busVoltage() {
    return voltage;
  }

  /**
   * Gets the output percentage of the motor controller.
   *
   * @return Output of the motor controller (in percent).
   */
  double motorOutputPercent() {
    return (voltage / maxVoltage);
  }



}