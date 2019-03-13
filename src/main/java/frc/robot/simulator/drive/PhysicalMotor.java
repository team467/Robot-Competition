package frc.robot.simulator.drive;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.utilities.MovingAverage;
import frc.robot.utilities.PreferencesStorage;

import java.lang.Thread.State;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;


public class PhysicalMotor implements Runnable, PIDOutput, PIDSource {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(PhysicalMotor.class.getName());
  
  private static final DecimalFormat df = new DecimalFormat("#0.0000");

  static HashMap<Integer, PhysicalMotor> motors = new HashMap<Integer, PhysicalMotor>();
  private Thread thread = null;

  // Simulation Information
  public static final double MAX_RPM = 821;

  public static final int MOTOR_TIME_SLICE_IN_MS = 10;

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
  private double current = 0.0; // TODO: Figure out current simulation

  private double simulatedTargetVelocity;

  private double simulationVelocity;

  private boolean isOpenLoop = true;

  // Sensor stuff
  private static final int NUMBER_CLOSED_LOOPS = 2;
  private FeedbackDevice[] feedbackDeviceType = new FeedbackDevice[NUMBER_CLOSED_LOOPS];
  private double revolutions = 0.0; // keep track of partial ticks
  private int[] sensors = new int[NUMBER_CLOSED_LOOPS];
  private int closedLoopIndex = 0;

  private int[] smoothedVelocity = new int[NUMBER_CLOSED_LOOPS];
  private int iterationCount = 0; // Used for velocity smoothing
  private VelocityMeasPeriod velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
  private int velocityMeasurementWindowSize = 64;
  private MovingAverage[] smoothedVelocityMeasurement = new MovingAverage[NUMBER_CLOSED_LOOPS];
  private int[][] previousSensorReadings;

  private long lastCallTime;

  // These are the parameters for the simulated internal PID controller.

  /**
   * The Talon has an internal PID controller. 
   * This simulation uses the WPI Library implimentation;
   */
  private PIDController[] pidController = new PIDController[NUMBER_CLOSED_LOOPS];
  private PIDSourceType pidSourceType = PIDSourceType.kDisplacement;
  private static final int NUMBER_SAVE_SLOTS = 4;
  private ClosedLoopParameter[] closedLoopParameters = new ClosedLoopParameter[NUMBER_SAVE_SLOTS];
  private int[] pidParamSlotIndex = new int[NUMBER_CLOSED_LOOPS];

  // General motor information
  private int id = 0;
  private String name;

  // Mode Settings
  private ControlMode controlMode = ControlMode.PercentOutput;
  private NeutralMode neutralMode = NeutralMode.Brake;

  // Current power
  private double outputPower = 0.0;
  private double auxillaryPower = 0.0;

  // Current state information
  private int currentSensorReading;
  private double speed;
  private long currentTime;
  private double currentVoltage;

  // previous state information
  private int previousSensorReading;
  private double previousVelocity;
  private long previousTime;
  private MovingAverage averageVoltageReadings;

  // Targeted state
  private int targetSensorValue;
  private double targetVelocity;

  // Other state
  private boolean brakeEnabled = true;
  private boolean phaseSensor = false;
  private boolean headingHold = false;
  private boolean inverted = false;
  private int timeoutMs = 0;
  private double openLoopSecondsFromNeutralToFull = 0.0;
  private double closedLoopSecondsFromNeutralToFull = 0.0;
  private double forwardPeakOutput = 1.0;
  private double reversePeakOutput = 1.0;
  private double nominalForwardOutput = 0.0;
  private double nominalReverseOutput = 0.0;
  private double percentDeadband = 0.04;
  private int voltageReadingWindow = 8;
  private boolean voltageCompensationEnabled = false;
  private double temperatureC = 50.0;

  private int[] motionProfStats = new int[11];

  private boolean overrideLimitSwitches = false;
  private int forwardSensorLimitThresholdValue = 0;
  private int reverseSensorLimitThresholdValue = 0;
  private boolean enableForwardSoftLimit = false;
  private boolean enableReverseSoftLimit = false;
  private boolean overrideSoftLimits = false;

  private boolean remoteSensorClosedLoopDisableNeutralOnLossOfSignal = false;
  private boolean feedbackNotContinuous = false;
  private boolean clearPositionFeedbackSensorOnForwardLimitSwitchTrigger = false;
  private boolean clearPositionFeedbackSensorOnReverseLimitSwitchTrigger = false;
  private boolean clearPositionOnQuadratureIndexSignal = false;
  private boolean limitSwitchDisableNeutralOnLossOfSignal = false;
  private boolean softLimitDisableNeutralOnLossOfSignal = false;
  private int pulseWidthSensorSmoothingWindowSize = 0;
  private int pulseWidthSensorEdgesPerRotation = 4;
  private int peakCurrentLimitAmps = 0;
  private int currentLimitMillisecondsPastPeak = 0;
  private int continuousCurrentLimitAmps = 0;
  private boolean currentLimit = false;
  private boolean resetOccurred = true;
  private int firmwareVersion = 100;
  private ErrorCode lastError = ErrorCode.OK;
  private int faults = 0;
  private int stickyFaults = 0;

  static PhysicalMotor createMotor(int id) {
    if (motors.containsKey(id)) {
      return motors.get(id);
    }
    LOGGER.info("Creating motor ID: {}", id);
    PhysicalMotor motor = new PhysicalMotor(id);
    motor.enable();
    motors.put(id, motor);
    return motor;
  }

  private void enable() {
    if (!enabled) {
      LOGGER.debug("Enable motor {}", id);
      enabled = true;
      if (thread == null) {
        thread = new Thread(this);
        thread.setName(name + thread);
        // Setting priority to improve simulation stability of real-time system
        // Default priority is 5, max is 10
        // On Linux, the command line argument -XX:+UseThreadPriorities is also required.
        thread.setPriority(8);
        thread.start();
      }
      lastCallTime = System.nanoTime();
    }
  }

  /**
   * Enables the motors, useful for simulator reset. Motors are automatically enabled on creation.
   */
  public static void enableMotors() {
    for (PhysicalMotor motor : motors.values()) {
      motor.enable();
    }
  }

  private void resetControllers() {
    if (enabled) {
      LOGGER.debug("Reset motor {}", id);
      for (PIDController controller : pidController) {
        controller.reset();
      }
    }
  }

  /**
   * Shutsdown the motors. Mostly useful for simulation.
   */
  public static void reset() {
    for (PhysicalMotor motor : motors.values()) {
      motor.resetControllers();
    }
  }

  PhysicalMotor(int id) {
    this.id = id;
    name = "Motor-" + id + "-";
    voltage = 0.0;
    simulationVelocity = 0.0;
    simulatedTargetVelocity = 0.0;
    isOpenLoop = true;

    // Period is half the motor thread time to allow for the PID control to update based inputs
    double pidPeriod = ((double) MOTOR_TIME_SLICE_IN_MS / 1000.0 / 4.0);
    pidSourceType = PIDSourceType.kDisplacement;
    for (int i = 0; i < closedLoopParameters.length; i++) {
      closedLoopParameters[i] = new ClosedLoopParameter(name + "-" + i);
    }

    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      sensors[i] = 0;
      smoothedVelocity[i] = 0;
      pidParamSlotIndex[i] = i;
      pidController[i] = new PIDController(
        closedLoopParameters[pidParamSlotIndex[i]].proportionalGain(), 
        closedLoopParameters[pidParamSlotIndex[i]].integral(), 
        closedLoopParameters[pidParamSlotIndex[i]].derivative(), 
        closedLoopParameters[pidParamSlotIndex[i]].feedForward(), 
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

      }

  int id() {
    return id;
  }

  ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int index) {
    if (index < 0 || index >= NUMBER_CLOSED_LOOPS) {
      lastError = ErrorCode.SensorNotPresent;
      return lastError;
    }
    closedLoopIndex = index;
    feedbackDeviceType[index] = feedbackDevice;
    lastError = ErrorCode.OK;
    return lastError;
  }

  ErrorCode configAllowableClosedLoopError(int slotIndex, int allowableClosedLoopError) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    this.closedLoopParameters[slotIndex].allowableClosedLoopError(allowableClosedLoopError);
    lastError = ErrorCode.OK;
    return lastError;
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
    lastError = ErrorCode.OK;
    return lastError;
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
  ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut) {
    // TODO
    lastError = ErrorCode.OK;
    return lastError;
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
    lastError = ErrorCode.OK;
    return lastError;
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
    lastError = ErrorCode.OK;
    return lastError;
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
    //return pidController[closedLoopIndex].getD;
  }

  /**
   * Gets the current target of a given closed loop.
   *
   * @param closedLoopIndex
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return The closed loop target.
   */
  double closedLoopTarget(int closedLoopIndex) {
    return (pidController[closedLoopIndex].getSetpoint() 
        * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION);
  }

  /**
   * Rarely used feature that sets a minimum output for a motor.
   * 
   * @param percentOutput Sets the minimum reverse output 
   *      as a percent of bettery voltage
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configNominalOutputReverse(double percentOutput) {
    if (percentOutput < -1.0 || percentOutput > 0.0) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    nominalPercentOutputReverse = percentOutput;
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  /**
   * Rarely used feature that sets a minimum output for a motor.
   * 
   * @param percentOutput Sets the minimum forward output 
   *      as a percent of bettery voltage
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configNominalOutputForward(double percentOutput) {
    if (percentOutput < 0.0 || percentOutput > 1.0) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    nominalPercentOutputForward = percentOutput;
    lastError = ErrorCode.OK;
    return lastError;
  }

  private double nominalPercentOutputForward = 0.0;
  private double nominalPercentOutputReverse = 0.0;
  private double peakPercentOutputForward = 1.0;
  private double peakPercentOutputReverse = -1.0;

  ErrorCode configPeakOutputForward(double percentOutput) {
    if (percentOutput < 0.0 || percentOutput > 1.0) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    peakPercentOutputForward = percentOutput; 
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  ErrorCode configPeakOutputReverse(double percentOutput) {
    if (percentOutput < -1.0 || percentOutput > 0.0) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    peakPercentOutputReverse = percentOutput;
    lastError = ErrorCode.OK;
    return lastError;
  }

  ErrorCode configOpenLoopRamp(double secondsFromNeutralToFull) {
    if (secondsFromNeutralToFull < 0) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    openLoopRampRateSecondsToFull = secondsFromNeutralToFull;
    openLoopRampRatePerPeriod = convertRampRateToPerPeriod(openLoopRampRateSecondsToFull); 
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  ErrorCode configClosedLoopRamp(double secondsFromNeutralToFull) {
    if (secondsFromNeutralToFull < 0) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    closedLoopRampRateSecondsToFull = secondsFromNeutralToFull;
    closedLoopRampRatePerPeriod = convertRampRateToPerPeriod(closedLoopRampRateSecondsToFull); 
    lastError = ErrorCode.OK;
    return lastError;
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
        / (secondsFromNeutralToFull * 1000.0 / ((double)MOTOR_TIME_SLICE_IN_MS));
  }

  ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod velocityMeasurementPeriod) {
    synchronized (this) {
      this.velocityMeasurementPeriod = velocityMeasurementPeriod;
      previousSensorReadings = new int[NUMBER_CLOSED_LOOPS][velocityMeasurementPeriod.value];
      for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
        for (int j = 0; j < velocityMeasurementPeriod.value; j++) {
          previousSensorReadings[i][j] = 0;
        }
      }
    }
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  ErrorCode configVelocityMeasurementWindow(int windowSize) {
    // Valid options are 1, 2, 4, 8, 16, 32
    if (windowSize < 1) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    } else {
      velocityMeasurementWindowSize = 32;
      for (int possibleSize = 2; possibleSize <= 32; possibleSize *= 2) {
        if (windowSize < possibleSize) {
          velocityMeasurementWindowSize = possibleSize / 2;
          break;
        }
      }
    }
    synchronized (this) {
      for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
        smoothedVelocityMeasurement[i] = new MovingAverage(velocityMeasurementWindowSize);
      }
    }
    lastError = ErrorCode.OK;
    return lastError;
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
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    closedLoopParameters[slotIndex].proportionalGain(proportionalGain);
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setP(proportionalGain);
      }
    }
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  /**
   * Sets the 'I' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param integralCoefficient
   *            Value of the integral coefficient.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configIntegralCoefficient(int slotIndex, double integralCoefficient) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    closedLoopParameters[slotIndex].integral(integralCoefficient);
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setI(integralCoefficient);
      }
    }
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Sets the 'D' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param derivativeCoefficient
   *            Value of the differential coefficient.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configDerivativeCoefficient(int slotIndex, double derivativeCoefficient) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    this.closedLoopParameters[slotIndex].derivative(derivativeCoefficient);
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setD(derivativeCoefficient);
      }
    }
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  /**
   * Sets the 'F' coefficient in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param feedForwardCoefficient
   *            Value of the feed forward constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configFeedForwardCoefficient(int slotIndex, double feedForwardCoefficient) {
    if (slotIndex < 0 || slotIndex > NUMBER_SAVE_SLOTS) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    this.closedLoopParameters[slotIndex].feedForward(feedForwardCoefficient);
    for (int i = 0; i < NUMBER_CLOSED_LOOPS; i++) {
      if (pidParamSlotIndex[i] == slotIndex) {
        pidController[i].setF(feedForwardCoefficient);
      }
    }
    lastError = ErrorCode.OK;
    return lastError;
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
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    neutralDeadband = percentDeadband;
    lastError = ErrorCode.OK;
    return lastError;
  }

  void neutralMode(NeutralMode neutralMode) {
    this.neutralMode = neutralMode;
  }

  void headingHold(boolean headingHold) {
    this.headingHold = headingHold;
  }

  void phaseSensor(boolean phaseSensor) {
    this.phaseSensor = phaseSensor;
  }
  
  void invert(boolean invert) {
    this.inverted = invert;
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

  void setOutputByDemandType(double demand0,
      DemandType demand1Type, double demand1) {

    switch (demand1Type) {

      case AuxPID:
        outputPower = demand0;
        auxillaryPower = demand1;
        break;

      case ArbitraryFeedForward:
        outputPower = demand0 + demand1;
        auxillaryPower = 0.0;
        break;

      case Neutral:
      default:
        outputPower = demand0;
        auxillaryPower = 0.0;
    }
    //System.err.println("Output power: " + outputPower + " Aux Power: " + auxillaryPower);
  }

  void setDemand(ControlMode controlMode, int demand, DemandType demandType) {
    set4(controlMode, 0, demand, demandType);
  }




  void set4(ControlMode controlMode, double demand0, double demand1, DemandType demand1Type) {

    setOutputByDemandType(demand0, demand1Type, demand1);
    double totalDemand = outputPower + auxillaryPower;
    totalDemand *= inverted ? -1.0 : 1.0;
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
    
    synchronized (this) {

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
   * Gets the position for the primary or secondary closed-loop.
   * 
   * @param closedLoopIndex 0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return the position in sensor ticks
   */
  int position(int closedLoopIndex) {
    if (closedLoopIndex < 0 || closedLoopIndex >= NUMBER_CLOSED_LOOPS) {
      return 0;
    }
    if (inverted) {
      return -sensors[closedLoopIndex];
    } else {
      return sensors[closedLoopIndex];
    }
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
  ErrorCode position(int closedLoopIndex, int sensorPosition) {
    if (closedLoopIndex < 0 || closedLoopIndex >= NUMBER_CLOSED_LOOPS) {
      lastError = ErrorCode.InvalidParamValue;
      return lastError;
    }
    sensors[closedLoopIndex] = sensorPosition;
    if (inverted) {
      sensors[closedLoopIndex] *= -1.0;
    }
    revolutions = sensorPosition / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    pidController[closedLoopIndex].reset();
    pidController[closedLoopIndex].enable();
    lastError = ErrorCode.OK;
    return lastError;
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
      // return (targetPosition - position(closedLoopIndex));
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

    while (enabled) {
      long currentTime = System.nanoTime();
      long duration = (currentTime - lastCallTime) / 1000000; // Nanoseconds to Milliseconds
      double numberOfPeriods = ((double) duration) / ((double) MOTOR_TIME_SLICE_IN_MS);
      lastCallTime = currentTime;
      try {
        double velocityDiffFromTarget = simulatedTargetVelocity - simulationVelocity;

        LOGGER.trace("Target Velocity: {} Current Velocity: {} Diff: {}", 
            df.format(simulatedTargetVelocity), 
            df.format(simulationVelocity), 
            df.format(velocityDiffFromTarget));
        
        // Make sure it's not in the middle of a change call
        synchronized (this) {
          iterationCount++;
          double rampRate;
          if (isOpenLoop) {
            rampRate = openLoopRampRatePerPeriod;
          } else {
            rampRate = closedLoopRampRatePerPeriod;
          }

          rampRate *= numberOfPeriods;
          LOGGER.trace("Time since last call: {} - periods: {}", duration, numberOfPeriods);

          if (Math.abs(velocityDiffFromTarget) > rampRate) {
            simulationVelocity += Math.signum(velocityDiffFromTarget) * rampRate;
          } else {
            simulationVelocity = simulatedTargetVelocity;
          }
        }

        // Speed is in rotations/time slice, sensor ticks multiple by ticks per revolution
        revolutions += simulationVelocity;
        sensors[closedLoopIndex] 
            = (int) (revolutions * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION); 
        if (inverted) {
          sensors[closedLoopIndex] *= -1.0;
        }
        // truncation is OK
        smoothedVelocity[closedLoopIndex] = 
            (int) (smoothedVelocityMeasurement[closedLoopIndex].average(
            sensors[closedLoopIndex] - previousSensorReadings[closedLoopIndex]
              [iterationCount % velocityMeasurementPeriod.value])
            / velocityMeasurementPeriod.value * 100); // convert to 100 ms
        previousSensorReadings[closedLoopIndex][iterationCount % velocityMeasurementPeriod.value] 
            = sensors[closedLoopIndex];

        Thread.sleep(MOTOR_TIME_SLICE_IN_MS);
      } catch (InterruptedException e) {
        LOGGER.error(e);
        e.printStackTrace();
      }
    }
  }

  @Override
  public void pidWrite(double output) {
    if (controlMode == ControlMode.Position || controlMode == ControlMode.Velocity) {
      // System.err.println(id + ": " + output);
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
  double outputPercent() {
    return (voltage / maxVoltage);
  }

    /**
   * Gets the output current of the motor controller.
   *
   * @return The output current (in amps).
   */
  double current() {
    // TODO: Simulate output current
    return current;
  }

  /**
   * Gets the temperature of the motor controller.
   *
   * @return Temperature of the motor controller (in 'C)
   */
  double temperature() {
    // TODO: Simulate temperature
    return temperatureC;
  }

  void overrideLimitSwitches(boolean enable) {
    this.overrideLimitSwitches = enable;
  }

  /**
   * Configures the forward soft limit threhold.
   *
   * @param forwardSensorLimit
   *            Forward Sensor Position Limit (in raw sensor units).
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode forwardSensorLimitThresholdValue(int forwardSensorLimit) {
    forwardSensorLimitThresholdValue = forwardSensorLimit;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Configures the reverse soft limit threshold.
   *
   * @param reverseSensorLimit
   *            Reverse Sensor Position Limit (in raw sensor units).
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode reverseSensorLimitThresholdValue(int reverseSensorLimit) {
    reverseSensorLimitThresholdValue = reverseSensorLimit;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Configures the forward soft limit enable.
   *
   * @param enable
   *            Forward Sensor Position Limit Enable.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configForwardSoftLimit(boolean enable) {
    enableForwardSoftLimit = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Configures the reverse soft limit enable.
   *
   * @param enable
   *            Reverse Sensor Position Limit Enable.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configReverseSoftLimit(boolean enable) {
    // TODO
    enableReverseSoftLimit = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Can be used to override-disable the soft limits.
   * This function can be used to quickly disable soft limits without
   * having to modify the persistent configuration.
   *
   * @param enable enable state for soft limit switches.
   */
  void overrideSoftLimits(boolean enable) {
    // TODO
    overrideSoftLimits = enable;
  }

//------Feedback Device Interaction Settings---------//

  /**
   * Disables wrapping the position. If the signal goes from 1023 to 0 a motor
   * controller will by default go to 1024. If wrapping the position is disabled,
   * it will go to 0;
   *
   * @param enable disable wrapping the position.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode feedbackNotContinuous(boolean enable) {
    // TODO
    feedbackNotContinuous = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }
  
  /**
   * Disables going to neutral (brake/coast) when a remote sensor is no longer detected.
   *
   * @param enable disable going to neutral
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode remoteSensorClosedLoopDisableNeutralOnLossOfSignal(boolean enable) {
    // TODO
    remoteSensorClosedLoopDisableNeutralOnLossOfSignal = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Enables clearing the position of the feedback sensor when the forward
   * limit switch is triggered
   *
   * @param enable whether clearing is enabled, defaults false
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode clearPositionFeedbackSensorOnForwardLimitSwitchTrigger(boolean enable) {
    // TODO
    clearPositionFeedbackSensorOnForwardLimitSwitchTrigger = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Enables clearing the position of the feedback sensor when the reverse
   * limit switch is triggered
   *
   * @param enable whether clearing is enabled, defaults false
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode clearPositionFeedbackSensorOnReverseLimitSwitchTrigger(boolean enable) {
    // TODO
    clearPositionFeedbackSensorOnReverseLimitSwitchTrigger = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Enables clearing the position of the feedback sensor when the quadrature index signal
   * is detected
   *
   * @param enable whether clearing is enabled, defaults false
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode clearPositionOnQuadratureIndexSignal(boolean enable) {
    // TODO
    clearPositionOnQuadratureIndexSignal = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Disables limit switches triggering (if enabled) when the sensor is no longer detected.
   *
   * @param enable disable triggering
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode limitSwitchDisableNeutralOnLossOfSignal(boolean enable) {
    // TODO
    limitSwitchDisableNeutralOnLossOfSignal = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Disables soft limits triggering (if enabled) when the sensor is no longer detected.
   *
   * @param enable disable triggering
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode softLimitDisableNeutralOnLossOfSignal(boolean enable) {
    // TODO
    softLimitDisableNeutralOnLossOfSignal = enable;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Sets the edges per rotation of a pulse width sensor. (This should be set for
   * tachometer use).
   *
   * @param edgesPerRotation edges per rotation
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode pulseWidthSensorEdgesPerRotation(int edgesPerRotation) {
    // TODO
    pulseWidthSensorEdgesPerRotation = edgesPerRotation;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Sets the number of samples to use in smoothing a pulse width sensor with a rolling
   * average. Default is 1 (no smoothing).
   *
   * @param windowSize samples for rolling avg
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode pulseWidthSensorSmoothingWindowSize(int windowSize) {
    // TODO
    pulseWidthSensorSmoothingWindowSize = windowSize;
    lastError = ErrorCode.OK;
    return lastError;
  }

  // ------ error ----------//
  /**
   * Gets the last error generated by this object. Not all functions return an
   * error code but can potentially report errors. This function can be used
   * to retrieve those error codes.
   *
   * @return Last Error Code generated by a function.
   */
  ErrorCode lastError() {
    return lastError;
  }

  // ------ Faults ----------//
  /**
   * Polls the various fault flags.
   *
   * @return Last Error Code generated by a function.
   */
  int faults() {
    // TODO: Convert faults to flags and return
    return faults;
  }

  /**
   * Polls the various sticky fault flags.
   *
   * @return Last Error Code generated by a function.
   */
  int stickyFaults() {
    // TODO: Convert stick faults to bit flags are return
    return stickyFaults;
  }

  /**
   * Clears all sticky faults.
   *
   * @return Last Error Code generated by a function.
   */
  ErrorCode clearStickyFaults() {
    // TODO: Create list of faults, then method for clearing
    stickyFaults = 0;
    lastError = ErrorCode.OK;
    return lastError;
  }

  // ------ Firmware ----------//
  /**
   * Gets the firmware version of the device.
   *
   * @return Firmware version of device. For example: version 1-dot-2 is
   *         0x0102.
   */
  int firmwareVersion() {
    return firmwareVersion;
  }

  /**
   * Returns true if the device has reset since last call.
   *
   * @return Has a Device Reset Occurred?
   */
  boolean resetOccurred() {
    // TODO: mark reset off on other calls
    return resetOccurred;
  }

  //------ Custom Persistent Params ----------//
  /**
   * Sets the value of a custom parameter. This is for arbitrary use.
   *
   * <p>Sometimes it is necessary to save calibration/limit/target information in
   * the device. Particularly if the device is part of a subsystem that can be
   * replaced.
   *
   * @param newValue
   *            Value for custom parameter.
   * @param paramIndex
   *            Index of custom parameter [0,1]
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configSetCustomParam(int newValue, int paramIndex) {
    // TODO : Figure out custom parameters
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Gets the value of a custom parameter.
   *
   * @param paramIndex
   *            Index of custom parameter [0,1].
   * @return Value of the custom param.
   */
  int configGetCustomParam(int paramIndex) {
    // TODO : Figure out custom parameters
    return 0;
  }

  /**
   * Sets a parameter. Generally this is not used. This can be utilized in -
   * Using new features without updating API installation. - Errata
   * workarounds to circumvent API implementation. - Allows for rapid testing
   * / unit testing of firmware.
   *
   * @param param
   *            Parameter enumeration.
   * @param value
   *            Value of parameter.
   * @param subValue
   *            Subvalue for parameter. Maximum value of 255.
   * @param ordinal
   *            Ordinal of parameter.
   * @return Error Code generated by function. 0 indicates no error.
   */
  ErrorCode configSetParameter(int param, double value,
      int subValue, int ordinal) {
    // int retval = MotControllerJNI.ConfigSetParameter(m_handle, param,  
    // value, subValue, ordinal,
    //     timeoutMs);
    // TODO: Figure out set parameter with subValue
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Gets a parameter.
   *
   * @param param
   *            Parameter enumeration.
   * @param ordinal
   *            Ordinal of parameter.
   * @return Value of parameter.
   */
  double configGetParameter(int param, int ordinal) {
    return 0.0;
    // TODO: Figure out how parameters are referenced by int
  }

  /**
   * Configure the peak allowable current (when current limit is enabled).
   *
   * <p>Current limit is activated when current exceeds the peak limit for longer
   * than the peak duration. Then software will limit to the continuous limit.
   * This ensures current limiting while allowing for momentary excess current
   * events.
   *
   * <p>For simpler current-limiting (single threshold) use
   * ConfigContinuousCurrentLimit() and set the peak to zero:
   * ConfigPeakCurrentLimit(0).
   *
   * @param amps
   *            Amperes to limit.
   */
  ErrorCode peakCurrentLimitAmps(int amps) {
    //TODO: Current limiting
    peakCurrentLimitAmps = amps;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Configure the peak allowable duration (when current limit is enabled).
   *
   * <p>Current limit is activated when current exceeds the peak limit for longer
   * than the peak duration. Then software will limit to the continuous limit.
   * This ensures current limiting while allowing for momentary excess current
   * events.
   *
   * <p>For simpler current-limiting (single threshold) use
   * ConfigContinuousCurrentLimit() and set the peak to zero:
   * ConfigPeakCurrentLimit(0).
   *
   * @param milliseconds
   *            How long to allow current-draw past peak limit.
   */
  ErrorCode currentLimitMillisecondsPastPeak(int milliseconds) {
    //TODO: Current limiting
    currentLimitMillisecondsPastPeak = milliseconds;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Configure the continuous allowable current-draw (when current limit is
   * enabled).
   *
   * <p>Current limit is activated when current exceeds the peak limit for longer
   * than the peak duration. Then software will limit to the continuous limit.
   * This ensures current limiting while allowing for momentary excess current
   * events.
   *
   * <p>For simpler current-limiting (single threshold) use
   * ConfigContinuousCurrentLimit() and set the peak to zero:
   * ConfigPeakCurrentLimit(0).
   *
   * @param amps
   *            Amperes to limit.
   */
  ErrorCode continuousCurrentLimitAmps(int amps) {
    //TODO: Current limiting
    continuousCurrentLimitAmps = amps;
    lastError = ErrorCode.OK;
    return lastError;
  }

  /**
   * Enable or disable Current Limit.
   *
   * @param enable
   *    Enable state of current limit.
   * @see configPeakCurrentLimit, configPeakCurrentDuration,
   *      configContinuousCurrentLimit
   */
  void currentLimit(boolean enable) {
    //TODO: Current limiting
    currentLimit = enable;
  }

}