package frc.robot.simulator.drive;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;

import frc.robot.utilities.MovingAverage;

import java.util.concurrent.ConcurrentHashMap;

class PhysicalMotorManager {

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
  private boolean invertSensor = false;
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

  private int arbId = 0;
  private long handle = 0;

  private int[] motionProfStats = new int[11];

  private static ConcurrentHashMap<Long, PhysicalMotorManager> motors = new ConcurrentHashMap<Long, PhysicalMotorManager>();

  private boolean overrideLimitSwitches = false;
  private int forwardSensorLimitThresholdValue = 0;
  private int reverseSensorLimitThresholdValue = 0;
  private boolean enableForwardSoftLimit = false;
  private boolean enableReverseSoftLimit = false;
  private boolean overrideSoftLimits = false;

  private boolean remoteSensorClosedLoopDisableNeutralOnLOS = false;
  private boolean feedbackNotContinuous = false;
  private boolean clearPositionFeedbackSensorOnForwardLimitSwitchTrigger = false;
  private boolean clearPositionFeedbackSensorOnReverseLimitSwitchTrigger = false;
  private boolean clearPositionOnQuadratureIndexSignal = false;
  private boolean limitSwitchDisableNeutralOnLOS = false;
  private boolean softLimitDisableNeutralOnLOS = false;
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

  private PhysicalMotor physicalMotor = null;
  // --------------------- Constructors -----------------------------//

  /**
   * Constructor for motor controllers.
   *
   * @param arbId the bus id of arbId
   */
  private PhysicalMotorManager(int arbId) {
    this.arbId = arbId;
    physicalMotor = PhysicalMotor.createMotor(arbId);
  }

  // Static Manager calls // 

  static long create(int arbId) {
    PhysicalMotorManager motor = new PhysicalMotorManager(arbId);
    long handle = (long) arbId;
    motors.put(handle, motor);
    return handle;
  }

  /**
   * Returns the Device ID
   *     return MotControllerJNI.GetDeviceNumber(m_handle);
   *
   * @return Device number.
   */
  static int getDeviceNumber(long handle) {
    return (int) handle;
  }

  static void set4(long handle, ControlMode controlMode, double demand0,
       double demand1, DemandType demand1Type) {

    PhysicalMotorManager motor = motors.get(handle);
    // System.err.println("Set 4 demand0= " + demand0 + " handle= " 
    //     + handle + " control mode = " + controlMode);
    motor.setOutputByDemandType(demand0, demand1Type, demand1);
    motor.physicalMotor.set4(controlMode, motor.outputPower, motor.auxillaryPower, demand1Type);
  }

  static void setDemand(long handle, ControlMode controlMode, int demand,
    int demandTypeValue) {

    PhysicalMotorManager motor = motors.get(handle);
    motor.outputPower = demand;
    //motor.setOutputByDemandType(demand0, demand1Type, demand1);

  }

  private void setOutputByDemandType(double demand0,
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
//    System.err.println("Output power: " + outputPower + " Aux Power: " + auxillaryPower);
  }

  /**
   *  MotControllerJNI.SetNeutralMode(m_handle, neutralMode.value);
   */
  static void setNeutralMode(long handle, int neutralModeSetting) {
    PhysicalMotorManager motor = motors.get(handle);

    if (neutralModeSetting == NeutralMode.Brake.value
        || neutralModeSetting == NeutralMode.EEPROMSetting.value) {
      motor.neutralMode = NeutralMode.Brake;
    } else if (neutralModeSetting == NeutralMode.Coast.value) {
      motor.neutralMode = NeutralMode.Coast;
    }
  }

  private boolean headingHold = false;

    /* this routine is moot as the Set() call updates the signal on each call */
    //MotControllerJNI.EnableHeadingHold(m_handle, enable ? 1 : 0);
    static void enableHeadingHold(long handle, boolean enable) {
    motors.get(handle).headingHold = enable;
  }

  /**
   * For now this simply updates the CAN signal to the motor controller.
   * Future firmware updates will use this to control advanced auxiliary loop behavior.
   *
   */
 static void selectDemandType(long handle, DemandType demandType) {
    /* this routine is moot as the Set() call updates the signal on each call */
    //MotControllerJNI.SelectDemandType(m_handle, value ? 1 : 0);
  }

  // ------ Invert behavior ----------//

  private boolean phaseSensor = false;

  static void setSensorPhase(long handle, boolean phaseSensor) {
    motors.get(handle).phaseSensor = phaseSensor;
  }

  private boolean invert = false;

  static void setInverted(long handle, boolean invert) {
    motors.get(handle).invert = invert;
  }

    //----- Factory Default Configuration -----//

  /**
   * Configure all configurations to factory default values
   *
   * @param timeoutMs
   *            Timeout value in ms. Function will generate error if config is
   *            not successful within timeout.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configFactoryDefault(double handle, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    // TODO: Look up and set all factory defaults
    return motor.lastError;
  }

  // ----- general output shaping ------------------//
  /**
   * Configures the open-loop ramp rate of throttle output.
   *
   * @param secondsFromNeutralToFull
   *            Minimum desired time to go from neutral to full throttle. A
   *            value of '0' will disable the ramp.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configOpenLoopRamp(
      long handle, double secondsFromNeutralToFull, int timeoutMs) {
    // TODO: Figure out open loop ramp rate (separate but related to from simulation ramp rate)
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configOpenLoopRamp(secondsFromNeutralToFull);
    return motor.lastError;
  }

  /**
   * Configures the closed-loop ramp rate of throttle output.
   *
   * @param secondsFromNeutralToFull
   *            Minimum desired time to go from neutral to full throttle. A
   *            value of '0' will disable the ramp.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configClosedLoopRamp(long handle, double secondsFromNeutralToFull, int timeoutMs) {
    // TODO: Closed loop ramp rate
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configClosedLoopRamp(secondsFromNeutralToFull);
    return motor.lastError;
  }

  /**
   * Configures the forward peak output percentage.
   *
   * @param percentOut
   *            Desired peak output percentage. [0,1]
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configPeakOutputForward(long handle, double percentOut, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configPeakOutputForward(percentOut);
    return motor.lastError;
  }

  /**
   * Configures the reverse peak output percentage.
   *
   * @param percentOut
   *            Desired peak output percentage.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configPeakOutputReverse(long handle, double percentOut, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configPeakOutputReverse(percentOut);
    return motor.lastError;
  }

  /**
   * Configures the forward nominal output percentage.
   *
   * @param percentOut
   *            Nominal (minimum) percent output. [0,+1]
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configNominalOutputForward(long handle, double percentOut, int timeoutMs) {
    // TODO: config nominal output forward
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configNominalOutputForward(percentOut);
    return motor.lastError;
  }

  /**
   * Configures the reverse nominal output percentage.
   *
   * @param percentOut
   *            Nominal (minimum) percent output. [-1,0]
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configNominalOutputReverse(long handle, double percentOut, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configNominalOutputReverse(percentOut);
    return motor.lastError;
  }

  /**
   * Configures the output deadband percentage.
   *
   * @param percentDeadband
   *            Desired deadband percentage. Minimum is 0.1%, Maximum is 25%.
   *            Pass 0.04 for 4% (factory default).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configNeutralDeadband(long handle, double percentDeadband, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configNeutralDeadband(percentDeadband);
    return motor.lastError;
  }

  // ------ Voltage Compensation ----------//
  /**
   * Configures the Voltage Compensation saturation voltage.
   *
   * @param voltage
   *            This is the max voltage to apply to the hbridge when voltage
   *            compensation is enabled.  For example, if 10 (volts) is specified
   *            and a TalonSRX is commanded to 0.5 (PercentOutput, closed-loop, etc)
   *            then the TalonSRX will attempt to apply a duty-cycle to produce 5V.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configVoltageCompSaturation(long handle, double voltage, int timeoutMs) {
    // TODO: config voltage compensation saturation
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Configures the voltage measurement filter.
   *
   * @param filterWindowSamples
   *            Number of samples in the rolling average of voltage
   *            measurement.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configVoltageMeasurementFilter(
      long handle, int filterWindowSamples, int timeoutMs) {
    // TODO: set the voltage measurement filter
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Enables voltage compensation. If enabled, voltage compensation works in
   * all control modes.
   *
   * @param enable
   *            Enable state of voltage compensation.
   **/
 static void enableVoltageCompensation(long handle, boolean enable) {
    // TODO: Set voltage compensation
  }

  // ------ General Status ----------//
  /**
   * Gets the bus voltage seen by the device.
   *
   * @return The bus voltage value (in volts).
   */
  static double getBusVoltage(long handle) {
    PhysicalMotorManager motor = motors.get(handle);
    return motor.physicalMotor.busVoltage();
  }

  /**
   * Gets the output percentage of the motor controller.
   *
   * @return Output of the motor controller (in percent).
   */
  static double getMotorOutputPercent(long handle) {
    PhysicalMotorManager motor = motors.get(handle);
    return motor.physicalMotor.motorOutputPercent();
  }

  /**
   * Gets the output current of the motor controller.
   *
   * @return The output current (in amps).
   */
  static double getOutputCurrent(long handle) {
    // TODO: Simulate output current
    return 0.0;
  }

  /**
   * Gets the temperature of the motor controller.
   *
   * @return Temperature of the motor controller (in 'C)
   */
  static double getTemperature(long handle) {
    // TODO: Simulate temperature
    return 0.0;
  }

  // ------ sensor selection ----------//
  /**
   * Select the feedback device for the motor controller.
   *
   * @param feedbackDevice
   *            Feedback Device to select.
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configSelectedFeedbackSensor(
      long handle, FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configSelectedFeedbackSensor(feedbackDevice, pidIdx);
    return motor.lastError;
  }

  /**
   * The Feedback Coefficient is a scalar applied to the value of the
   * feedback sensor.  Useful when you need to scale your sensor values
   * within the closed-loop calculations.  Default value is 1.
   *
   * Selected Feedback Sensor register in firmware is the decoded sensor value
   * multiplied by the Feedback Coefficient.
   *
   * @param coefficient
   *            Feedback Coefficient value.  Maximum value of 1.
   *						Resolution is 1/(2^16).  Cannot be 0.
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configSelectedFeedbackCoefficient(
      long handle, double coefficient, int pidIdx, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    // TODO: config selected feedback coefficient
    return motor.lastError;
  }

  /**
   * Select what remote device and signal to assign to Remote Sensor 0 or Remote Sensor 1.
   * After binding a remote device and signal to Remote Sensor X, you may select Remote Sensor X
   * as a PID source for closed-loop features.
   *
   * @param deviceId
    *            The CAN ID of the remote sensor device.
   * @param remoteSensorSource
   *            The remote sensor device and signal type to bind.
   * @param remoteOrdinal
   *            0 for configuring Remote Sensor 0
   *            1 for configuring Remote Sensor 1
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configRemoteFeedbackFilter(
      long handle, int deviceId, int remoteSensorSourceValue, int remoteOrdinal, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    // TODO: config remote feedback filter
    return motor.lastError;
  }
  /**
   * Select what sensor term should be bound to switch feedback device.
   * Sensor Sum = Sensor Sum Term 0 - Sensor Sum Term 1
   * Sensor Difference = Sensor Diff Term 0 - Sensor Diff Term 1
   * The four terms are specified with this routine.  Then Sensor Sum/Difference
   * can be selected for closed-looping.
   *
   * @param sensorTermValue Which sensor term to bind to a feedback source.
   * @param feedbackDeviceValue The sensor signal to attach to sensorTerm.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configSensorTerm(
  long handle, int sensorTermValue, int feedbackDeviceValue, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    // TODO: config sensor term
    return motor.lastError;
  }

  // ------- sensor status --------- //
  /**
   * Get the selected sensor position (in raw sensor units).
   *
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop. See
   *            Phoenix-Documentation for how to interpret.
   *
   * @return Position of selected sensor (in raw sensor units).
   */
  static int getSelectedSensorPosition(long handle, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    int sensorPosition = motor.physicalMotor.position(pidIdx);
    return sensorPosition;
  }

  /**
   * Get the selected sensor velocity.
   *
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return selected sensor (in raw sensor units) per 100ms.
   * See Phoenix-Documentation for how to interpret.
   */
  static int getSelectedSensorVelocity(long handle, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    int sensorVelocity = motor.physicalMotor.velocity(pidIdx);
    return sensorVelocity;
  }

  /**
   * Sets the sensor position to the given value.
   *
   * @param sensorPos
   *            Position to set for the selected sensor (in raw sensor units).
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode setSelectedSensorPosition(long handle, int sensorPos, int pidIdx, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.setPosition(pidIdx, sensorPos);
    return motor.lastError;
  }

  /**
   * Sets the period of the given status frame.
   *
   * User ensure CAN Bus utilization is not high.
   *
   * This setting is not persistent and is lost when device is reset.
   * If this is a concern, calling application can use HasReset()
   * to determine if the status frame needs to be reconfigured.
   *
   * @param frame
   *            Frame whose period is to be changed.
   * @param periodMs
   *            Period in ms for the given frame.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode setControlFramePeriod(long handle, int frame, int periodMs) {
    // int retval = MotControllerJNI.SetControlFramePeriod(m_handle, frame, periodMs);
    // TODO Figure out status frames
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Sets the period of the given status frame.
   *
   * User ensure CAN Bus utilization is not high.
   *
   * This setting is not persistent and is lost when device is reset. If this
   * is a concern, calling application can use HasReset() to determine if the
   * status frame needs to be reconfigured.
   *
   * @param frameValue
   *            Frame whose period is to be changed.
   * @param periodMs
   *            Period in ms for the given frame.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode setStatusFramePeriod(long handle, int frameValue, int periodMs, int timeoutMs) {
    // int retval = MotControllerJNI.SetStatusFramePeriod(
      // m_handle, frameValue, periodMs, timeoutMs);
    // TODO Figure out status frames
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Gets the period of the given status frame.
   *
   * @param frame
   *            Frame to get the period of.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Period of the given status frame.
   */
  static int getStatusFramePeriod(long handle, int statusFrameEnhancedValue, int timeoutMs) {
    // return MotControllerJNI.GetStatusFramePeriod(m_handle, frame.value, timeoutMs);
    // TODO Figure out status frames
    return 0;
  }

  // ----- velocity signal conditionaing ------//

  /**
   * Sets the period over which velocity measurements are taken.
   *
   * @param period
   *            Desired period for the velocity measurement. @see
   *            #VelocityMeasPeriod
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configVelocityMeasurementPeriod(
      long handle, VelocityMeasPeriod velocityMeasurementPeriod, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configVelocityMeasurementPeriod(velocityMeasurementPeriod);
    return motor.lastError;
  }

  /**
   * Sets the number of velocity samples used in the rolling average velocity
   * measurement.
   *
   * @param windowSize
   *            Number of samples in the rolling average of velocity
   *            measurement. Valid values are 1,2,4,8,16,32. If another value
   *            is specified, it will truncate to nearest support value.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configVelocityMeasurementWindow(long handle, int windowSize, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configVelocityMeasurementWindow(windowSize);
    return motor.lastError;
  }

  // ------ remote limit switch ----------//

  /**
   * Configures the reverse limit switch for a remote source. For example, a
   * CAN motor controller may need to monitor the Limit-R pin of another Talon
   * or CANifier.
   *
   * @param type
   *            Remote limit switch source. User can choose between a remote
   *            Talon SRX, CANifier, or deactivate the feature.
   * @param normalOpenOrClose
   *            Setting for normally open, normally closed, or disabled. This
   *            setting matches the web-based configuration drop down.
   * @param deviceId
   *            Device ID of remote source (Talon SRX or CANifier device ID).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configReverseLimitSwitchSource(
      long handle, 
      LimitSwitchSource remoteLimitSwitchSourceType, 
      LimitSwitchNormal limitSwitchNormalOpenOrClose,
      int deviceId, 
      int timeoutMs) {
    // TODO: Figure out limit switches
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Configures the forward limit switch for a remote source. For example, a
   * CAN motor controller may need to monitor the Limit-F pin of another Talon
   * or CANifier.
   *
   * @param type
   *            Remote limit switch source. User can choose between a remote
   *            Talon SRX, CANifier, or deactivate the feature.
   * @param normalOpenOrClose
   *            Setting for normally open, normally closed, or disabled. This
   *            setting matches the web-based configuration drop down.
   * @param deviceID
   *            Device ID of remote source (Talon SRX or CANifier device ID).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configForwardLimitSwitchSource(
      long handle, 
      LimitSwitchSource remoteLimitSwitchSourceType, 
      LimitSwitchNormal limitSwitchNormalOpenOrClose,
      int deviceId, 
      int timeoutMs) {
    // TODO: Figure out limit switches
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Sets the enable state for limit switches.
   *
   * @param enable
   *            Enable state for limit switches.
   **/
 static void overrideLimitSwitchesEnable(long handle, boolean enable) {
    motors.get(handle).overrideLimitSwitches = enable;
  }

  // ------ soft limit ----------//
  /**
   * Configures the forward soft limit threhold.
   *
   * @param forwardSensorLimit
   *            Forward Sensor Position Limit (in raw sensor units).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configForwardSoftLimitThreshold(long handle, int forwardSensorLimit, int timeoutMs) {
    motors.get(handle).forwardSensorLimitThresholdValue = forwardSensorLimit;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Configures the reverse soft limit threshold.
   *
   * @param reverseSensorLimit
   *            Reverse Sensor Position Limit (in raw sensor units).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configReverseSoftLimitThreshold(long handle, int reverseSensorLimit, int timeoutMs) {
    motors.get(handle).reverseSensorLimitThresholdValue = reverseSensorLimit;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Configures the forward soft limit enable.
   *
   * @param enable
   *            Forward Sensor Position Limit Enable.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configForwardSoftLimitEnable(long handle, boolean enable, int timeoutMs) {
    motors.get(handle).enableForwardSoftLimit = enable;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Configures the reverse soft limit enable.
   *
   * @param enable
   *            Reverse Sensor Position Limit Enable.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configReverseSoftLimitEnable(long handle, boolean enable, int timeoutMs) {
    motors.get(handle).enableReverseSoftLimit = enable;
    // TODO: Figure out PID Loops
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Can be used to override-disable the soft limits.
   * This function can be used to quickly disable soft limits without
   * having to modify the persistent configuration.
   *
   * @param enable
   *            Enable state for soft limit switches.
   */
 static void overrideSoftLimitsEnable(long handle, boolean enable) {
    motors.get(handle).overrideSoftLimits = enable;
  }

  // ------ Current Lim ----------//
  /* not available in base */

  // ------ General Close loop ----------//
  /**
   * Sets the 'P' constant in the given parameter slot.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param value
   *            Value of the P constant.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode config_kP(long handle, int slotIdx, double value, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configProportionalGain(slotIdx, value);
    return motor.lastError;
  }

  /**
   * Sets the 'I' constant in the given parameter slot.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param value
   *            Value of the I constant.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode config_kI(long handle, int slotIdx, double value, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configICoefficient(slotIdx, value);
    return motor.lastError;
  }

  /**
   * Sets the 'D' constant in the given parameter slot.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param value
   *            Value of the D constant.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode config_kD(long handle, int slotIdx, double value, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configDCoefficient(slotIdx, value);
    return motor.lastError;
  }

  /**
   * Sets the 'F' constant in the given parameter slot.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param value
   *            Value of the F constant.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode config_kF(long handle, int slotIdx, double value, int timeoutMs) {
  PhysicalMotorManager motor = motors.get(handle);
  motor.lastError = motor.physicalMotor.configFCoefficient(slotIdx, value);
  return motor.lastError;
}

  /**
   * Sets the Integral Zone constant in the given parameter slot. If the
   * (absolute) closed-loop error is outside of this zone, integral
   * accumulator is automatically cleared. This ensures than integral wind up
   * events will stop after the sensor gets far enough from its target.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param izone
   *            Value of the Integral Zone constant (closed loop error units X
   *            1ms).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode config_IntegralZone(long handle, int slotIdx, int izone, int timeoutMs) {
  PhysicalMotorManager motor = motors.get(handle);
  //TODO
//  motor.lastError = motor.physicalMotor.configIntegralZone(slotIdx, izone);
  return motor.lastError;
}

  /**
   * Sets the allowable closed-loop error in the given parameter slot.
   *
   * @param slotIndex
   *            Parameter slot for the constant.
   * @param allowableClosedLoopError
   *            Value of the allowable closed-loop error.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configAllowableClosedLoopError(
      long handle, int slotIndex, int allowableClosedLoopError, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configAllowableClosedLoopError(slotIndex, allowableClosedLoopError);
    return motor.lastError;
  }

  /**
   * Sets the maximum integral accumulator in the given parameter slot.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param iaccum
   *            Value of the maximum integral accumulator (closed loop error
   *            units X 1ms).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configMaxIntegralAccumulator(
    long handle, int slotIdx, double iaccum, int timeoutMs) {
      PhysicalMotorManager motor = motors.get(handle);
      motor.lastError = motor.physicalMotor.configMaxIntegralAccumulator(slotIdx, iaccum);
      return motor.lastError;
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
   *						the magnitude will apply in both forward and reverse directions.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configClosedLoopPeakOutput(long handle, int slotIdx, double percentOut, int timeoutMs) {
  PhysicalMotorManager motor = motors.get(handle);
  motor.lastError = motor.physicalMotor.configClosedLoopPeakOutput(slotIdx, percentOut);
  return motor.lastError;
}

  /**
   * Sets the loop time (in milliseconds) of the PID closed-loop calculations.
   * Default value is 1 ms.
   *
   * @param slotIdx
   *            Parameter slot for the constant.
   * @param loopTimeMs
   *            Loop timing of the closed-loop calculations.  Minimum value of
   *						1 ms, maximum of 64 ms.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configClosedLoopPeriod(long handle, int slotIdx, int loopTimeMs, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.configClosedLoopPeriod(slotIdx, loopTimeMs);
    return motor.lastError;
  }

  /**
   * Sets the integral accumulator. Typically this is used to clear/zero the
   * integral accumulator, however some use cases may require seeding the
   * accumulator for a faster response.
   *
   * @param iaccum
   *            Value to set for the integral accumulator (closed loop error
   *            units X 1ms).
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode setIntegralAccumulator(
      long handle, double iaccum, int pidIdx, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.lastError = motor.physicalMotor.integralAccumulator(iaccum, pidIdx);
    return motor.lastError;
  }

  /**
   * Gets the closed-loop error. The units depend on which control mode is in
   * use. See Phoenix-Documentation information on units.
   *
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return Closed-loop error value.
   */
  static int getClosedLoopError(long handle, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    return motor.physicalMotor.closedLoopError(pidIdx);
  }

  /**
   * Gets the iaccum value.
   *
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return Integral accumulator value (Closed-loop error X 1ms).
   */
  static double getIntegralAccumulator(long handle, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    return motor.physicalMotor.integralAccumulator(pidIdx);
  }


  /**
   * Gets the derivative of the closed-loop error.
   *
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return The error derivative value.
   */
  static double getErrorDerivative(long handle, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    return motor.physicalMotor.errorDerivative(pidIdx);
  }

  /**
   * Selects which profile slot to use for closed-loop control.
   *
   * @param slotIdx
   *            Profile slot to select.
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   **/
 static void selectProfileSlot(long handle, int slotIdx, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.physicalMotor.selectProfileSlot(slotIdx, pidIdx);
  }

  /**
   * Gets the current target of a given closed loop.
   *
   * @param pidIdx
   *            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @return The closed loop target.
   */
  static int getClosedLoopTarget(long handle, int pidIdx) {
    PhysicalMotorManager motor = motors.get(handle);
    return motor.physicalMotor.closedLoopTarget(pidIdx);
  }

  /**
   * Gets the active trajectory target position using
   * MotionMagic/MotionProfile control modes.
   *
   * @return The Active Trajectory Position in sensor units.
   */
  static int getActiveTrajectoryPosition(long handle) {
    // return MotControllerJNI.GetActiveTrajectoryPosition(m_handle);
    // TODO: Figure out Motion Magic Stuff
    return 0;
  }

  /**
   * Gets the active trajectory target velocity using
   * MotionMagic/MotionProfile control modes.
   *
   * @return The Active Trajectory Velocity in sensor units per 100ms.
   */
  static int getActiveTrajectoryVelocity(long handle) {
    // return MotControllerJNI.GetActiveTrajectoryVelocity(m_handle);
    // TODO: Figure out Motion Magic Stuff
    return 0;
  }

  /**
   * Gets the active trajectory target heading using
   * MotionMagicArc/MotionProfileArc control modes.
   *
   * @return The Active Trajectory Heading in degreees.
   */
  static double getActiveTrajectoryHeading(long handle) {
    // return MotControllerJNI.GetActiveTrajectoryHeading(m_handle);
    // TODO: Figure out Motion Magic Stuff
    return 0.0;
  }

  // ------ Motion Profile Settings used in Motion Magic and Motion Profile ----------//

  /**
   * Sets the Motion Magic Cruise Velocity. This is the peak target velocity
   * that the motion magic curve generator can use.
   *
   * @param sensorUnitsPer100ms
   *            Motion Magic Cruise Velocity (in raw sensor units per 100 ms).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configMotionCruiseVelocity(long handle, int sensorUnitsPer100ms, int timeoutMs) {
    // int retval = MotControllerJNI.ConfigMotionCruiseVelocity(
    //   m_handle, sensorUnitsPer100ms, timeoutMs);
    // TODO: Figure out motion magic
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Sets the Motion Magic Acceleration. This is the target acceleration that
   * the motion magic curve generator can use.
   *
   * @param sensorUnitsPer100msPerSec
   *            Motion Magic Acceleration (in raw sensor units per 100 ms per
   *            second).
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configMotionAcceleration(long handle, int sensorUnitsPer100msPerSec, int timeoutMs) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  //------ Motion Profile Buffer ----------//
  /**
   * Clear the buffered motion profile in both controller's RAM (bottom), and in the
   * API (top).
   */
 static ErrorCode clearMotionProfileTrajectories(long handle) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Retrieve just the buffer count for the api-level (top) buffer. This
   * routine performs no CAN or data structure lookups, so its fast and ideal
   * if caller needs to quickly poll the progress of trajectory points being
   * emptied into controller's RAM. Otherwise just use GetMotionProfileStatus.
   *
   * @return number of trajectory points in the top buffer.
   */
  static int getMotionProfileTopLevelBufferCount(long handle) {
    // TODO: Figure out Motion Magic Stuff
    return 0;
  }
  /**
   * Push another trajectory point into the top level buffer (which is emptied
   * into the motor controller's bottom buffer as room allows).
   * @param trajPt to push into buffer.
   * The members should be filled in with these values...
   *
   * 		targPos:  servo position in sensor units.
   *		targVel:  velocity to feed-forward in sensor units
   *                 per 100ms.
   * 		profileSlotSelect0  Which slot to get PIDF gains. PID is used for position servo. F is used
   *						   as the Kv constant for velocity feed-forward. Typically this is hardcoded
   *						   to the a particular slot, but you are free gain schedule if need be.
   *						   Choose from [0,3]
   *		profileSlotSelect1 Which slot to get PIDF gains for auxiliary PId.
   *						   This only has impact during MotionProfileArc Control mode.
   *						   Choose from [0,1].
   * 	   isLastPoint  set to nonzero to signal motor controller to keep processing this
   *                     trajectory point, instead of jumping to the next one
   *                     when timeDurMs expires.  Otherwise MP executer will
   *                     eventually see an empty buffer after the last point
   *                     expires, causing it to assert the IsUnderRun flag.
   *                     However this may be desired if calling application
   *                     never wants to terminate the MP.
   *		zeroPos  set to nonzero to signal motor controller to "zero" the selected
   *                 position sensor before executing this trajectory point.
   *                 Typically the first point should have this set only thus
   *                 allowing the remainder of the MP positions to be relative to
   *                 zero.
   *		timeDur Duration to apply this trajectory pt.
   * 				This time unit is ADDED to the exising base time set by
   * 				configMotionProfileTrajectoryPeriod().
   * @return CTR_OKAY if trajectory point push ok. ErrorCode if buffer is
   *         full due to kMotionProfileTopBufferCapacity.
   */
  static ErrorCode pushMotionProfileTrajectory2(
      long handle,
      double trajectoryPointPosition,
      double trajectoryPointVelocity,
      double trajectoryPointAuxiliaryPosition,
      int trajectoryPointProfileSelect0,
      int trajectoryPointProfileSelect1,
      boolean isLastPoint,
      boolean zeroPosition,
      int timeDuration) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Retrieve just the buffer full for the api-level (top) buffer. This
   * routine performs no CAN or data structure lookups, so its fast and ideal
   * if caller needs to quickly poll. Otherwise just use
   * GetMotionProfileStatus.
   *
   * @return number of trajectory points in the top buffer.
   */
  static boolean isMotionProfileTopLevelBufferFull(long handle) {
    // return MotControllerJNI.IsMotionProfileTopLevelBufferFull(m_handle);
    // TODO: Figure out Motion Magic Stuff
    return false;
  }

  /**
   * This must be called periodically to funnel the trajectory points from the
   * API's top level buffer to the controller's bottom level buffer. Recommendation
   * is to call this twice as fast as the execution rate of the motion
   * profile. So if MP is running with 20ms trajectory points, try calling
   * this routine every 10ms. All motion profile functions are thread-safe
   * through the use of a mutex, so there is no harm in having the caller
   * utilize threading.
   */
  static void processMotionProfileBuffer(long handle) {
    //    MotControllerJNI.ProcessMotionProfileBuffer(m_handle);
    // TODO: Figure out Motion Magic Stuff
  }

  /**
   * Retrieve all status information.
   * For best performance, Caller can snapshot all status information regarding the
   * motion profile executer.
   *
   * @param statusToFill  Caller supplied object to fill.
   *
   * The members are filled, as follows...
   *
   *	topBufferRem:	The available empty slots in the trajectory buffer.
   * 	 				The robot API holds a "top buffer" of trajectory points, so your applicaion
   * 	 				can dump several points at once.  The API will then stream them into the
   * 	 		 		low-level buffer, allowing the motor controller to act on them.
   *
   *	topBufferRem: The number of points in the top trajectory buffer.
   *
   *	btmBufferCnt: The number of points in the low level controller buffer.
   *
   *	hasUnderrun: 	Set if isUnderrun ever gets set.
   * 	 	 	 	 	Only is cleared by clearMotionProfileHasUnderrun() to ensure
   *
   *	isUnderrun:		This is set if controller needs to shift a point from its buffer into
   *					the active trajectory point however
   *					the buffer is empty.
   *					This gets cleared automatically when is resolved.
   *
   *	activePointValid:	True if the active trajectory point has not empty, false otherwise. The members in activePoint are only valid if this signal is set.
   *
   *	isLast:	is set/cleared based on the MP executer's current
   *                trajectory point's IsLast value.  This assumes
   *                IsLast was set when PushMotionProfileTrajectory
   *                was used to insert the currently processed trajectory
   *                point.
   *
   *	profileSlotSelect: The currently processed trajectory point's
   *      			  selected slot.  This can differ in the currently selected slot used
   *       				 for Position and Velocity servo modes
   *
   *	outputEnable:		The current output mode of the motion profile
   *						executer (disabled, enabled, or hold).  When changing the set()
   *						value in MP mode, it's important to check this signal to
   *						confirm the change takes effect before interacting with the top buffer.
   */
  static ErrorCode getMotionProfileStatus2(long handle, int[] motionProfileStatistics) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
    //int retval = MotControllerJNI.GetMotionProfileStatus2(m_handle, _motionProfStats);
    // TODO: Look up type for _motionProfStats
  }

  /**
   * Clear the "Has Underrun" flag. Typically this is called after application
   * has confirmed an underrun had occured.
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode clearMotionProfileHasUnderrun(long handle, int timeoutMs) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Calling application can opt to speed up the handshaking between the robot
   * API and the controller to increase the download rate of the controller's Motion
   * Profile. Ideally the period should be no more than half the period of a
   * trajectory point.
   *
   * @param periodMs
   *            The transmit period in ms.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode changeMotionControlFramePeriod(long handle, int periodMs) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * When trajectory points are processed in the motion profile executer, the MPE determines
   * how long to apply the active trajectory point by summing baseTrajDurationMs with the
   * timeDur of the trajectory point (see TrajectoryPoint).
   *
   * This allows general selection of the execution rate of the points with 1ms resolution,
   * while allowing some degree of change from point to point.
   * @param baseTrajDurationMs The base duration time of every trajectory point.
   * 							This is summed with the trajectory points unique timeDur.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configMotionProfileTrajectoryPeriod(
      long handle, int baseTrajDurationMs, int timeoutMs) {
    // TODO: Figure out Motion Magic Stuff
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  //------Feedback Device Interaction Settings---------//

  /**
   * Disables wrapping the position. If the signal goes from 1023 to 0 a motor
   * controller will by default go to 1024. If wrapping the position is disabled,
   * it will go to 0;
   *
   * @param feedbackNotContinuous     disable wrapping the position.
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configFeedbackNotContinuous(
      long handle, boolean feedbackNotContinuous, int timeoutMs) {
    motors.get(handle).feedbackNotContinuous = feedbackNotContinuous;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Disables going to neutral (brake/coast) when a remote sensor is no longer detected.
   *
   * @param remoteSensorClosedLoopDisableNeutralOnLOS     disable going to neutral
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configRemoteSensorClosedLoopDisableNeutralOnLOS(
        long handle, boolean remoteSensorClosedLoopDisableNeutralOnLOS, int timeoutMs) {
    motors.get(handle).remoteSensorClosedLoopDisableNeutralOnLOS
        = remoteSensorClosedLoopDisableNeutralOnLOS;
        PhysicalMotorManager motor = motors.get(handle);
        return motor.lastError;
      }

  /**
   * Enables clearing the position of the feedback sensor when the forward
   * limit switch is triggered
   *
   * @param clearPositionOnLimitF     Whether clearing is enabled, defaults false
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configClearPositionOnLimitF(
      long handle, boolean clearPositionOnLimitF, int timeoutMs) {
    motors.get(handle).clearPositionFeedbackSensorOnForwardLimitSwitchTrigger
        = clearPositionOnLimitF;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Enables clearing the position of the feedback sensor when the reverse
   * limit switch is triggered
   *
   * @param clearPositionOnLimitR     Whether clearing is enabled, defaults false
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configClearPositionOnLimitR(
        long handle, boolean clearPositionOnLimitR, int timeoutMs) {
    motors.get(handle).clearPositionFeedbackSensorOnReverseLimitSwitchTrigger
        = clearPositionOnLimitR;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Enables clearing the position of the feedback sensor when the quadrature index signal
   * is detected
   *
   * @param clearPositionOnQuadIdx    Whether clearing is enabled, defaults false
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configClearPositionOnQuadIdx(
      long handle, boolean clearPositionOnQuadIdx, int timeoutMs) {
    motors.get(handle).clearPositionOnQuadratureIndexSignal = clearPositionOnQuadIdx;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Disables limit switches triggering (if enabled) when the sensor is no longer detected.
   *
   * @param limitSwitchDisableNeutralOnLOS    disable triggering
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configLimitSwitchDisableNeutralOnLOS(
      long handle, boolean limitSwitchDisableNeutralOnLOS, int timeoutMs) {
    motors.get(handle).limitSwitchDisableNeutralOnLOS
        = limitSwitchDisableNeutralOnLOS;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Disables soft limits triggering (if enabled) when the sensor is no longer detected.
   *
   * @param softLimitDisableNeutralOnLOS    disable triggering
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  static ErrorCode configSoftLimitDisableNeutralOnLOS(
      long handle, boolean softLimitDisableNeutralOnLOS, int timeoutMs) {
    motors.get(handle).softLimitDisableNeutralOnLOS
        = softLimitDisableNeutralOnLOS;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Sets the edges per rotation of a pulse width sensor. (This should be set for
   * tachometer use).
   *
   * @param pulseWidthPeriod_EdgesPerRot    edges per rotation
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configPulseWidthPeriod_EdgesPerRot(long handle,
      int pulseWidthSensorEdgesPerRotation, int timeoutMs) {
    motors.get(handle).pulseWidthSensorEdgesPerRotation = pulseWidthSensorEdgesPerRotation;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Sets the number of samples to use in smoothing a pulse width sensor with a rolling
   * average. Default is 1 (no smoothing).
   *
   * @param pulseWidthPeriod_FilterWindowSz   samples for rolling avg
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configPulseWidthPeriod_FilterWindowSz(long handle,
      int pulseWidthSensorSmoothingWindowSize, int timeoutMs) {
    motors.get(handle).pulseWidthSensorSmoothingWindowSize = pulseWidthSensorSmoothingWindowSize;
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  // ------ error ----------//
  /**
   * Gets the last error generated by this object. Not all functions return an
   * error code but can potentially report errors. This function can be used
   * to retrieve those error codes.
   *
   * @return Last Error Code generated by a function.
   */
 static ErrorCode getLastError(long handle) {
  PhysicalMotorManager motor = motors.get(handle);
  return motor.lastError;
}

  // ------ Faults ----------//
  /**
   * Polls the various fault flags.
   *
   * @return Last Error Code generated by a function.
   */
  static int getFaults(long handle) {
    // Todo: Convert faults to flags and return
    return motors.get(handle).faults;
  }

  /**
   * Polls the various sticky fault flags.
   *
   * @return Last Error Code generated by a function.
   */
  static int getStickyFaults(long handle) {
    // TODO: Convert stick faults to bit flags are return
    return motors.get(handle).stickyFaults;
  }

  /**
   * Clears all sticky faults.
   *
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Last Error Code generated by a function.
   */
 static ErrorCode clearStickyFaults(long handle, int timeoutMs) {
    // TODO: Create list of faults, then method for clearing
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  // ------ Firmware ----------//
  /**
   * Gets the firmware version of the device.
   *
   * @return Firmware version of device. For example: version 1-dot-2 is
   *         0x0102.
   */
  static int getFirmwareVersion(long handle) {
    return motors.get(handle).firmwareVersion;
  }

  /**
   * Returns true if the device has reset since last call.
   *
   * @return Has a Device Reset Occurred?
   */
  static boolean hasResetOccurred(long handle) {
    return motors.get(handle).resetOccurred;
  }

  //------ Custom Persistent Params ----------//
  /**
   * Sets the value of a custom parameter. This is for arbitrary use.
   *
   * Sometimes it is necessary to save calibration/limit/target information in
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
 static ErrorCode configSetCustomParam(long handle, int newValue, int paramIndex, int timeoutMs) {
    // TODO : Figure out custom parameters
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Gets the value of a custom parameter.
   *
   * @param paramIndex
   *            Index of custom parameter [0,1].
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Value of the custom param.
   */
  static int configGetCustomParam(long handle, int paramIndex, int timeoutMs) {
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
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
 static ErrorCode configSetParameter(long handle, int param, double value,
      int subValue, int ordinal, int timeoutMs) {
    // int retval = MotControllerJNI.ConfigSetParameter(m_handle, param,  value, subValue, ordinal,
    //     timeoutMs);
    // TODO: Figure out set parameter with subValue
    PhysicalMotorManager motor = motors.get(handle);
    return motor.lastError;
  }

  /**
   * Gets a parameter.
   *
   * @param param
   *            Parameter enumeration.
   * @param ordinal
   *            Ordinal of parameter.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Value of parameter.
   */
  static double configGetParameter(long handle, int param, int ordinal, int timeoutMs) {
    return 0.0;
    // TODO: Figure out how parameters are referenced by int
  }

  // ------ Misc. ----------//

  // ----- Follower ------//
  /**
   * Set the control mode and output value so that this motor controller will
   * follow another motor controller. Currently supports following Victor SPX
   * and Talon SRX.
   *
   * @param masterToFollow Motor Controller object to follow.
   * @param followerType
   *    Type of following control.  Use AuxOutput1 to follow the master
   *        device's auxiliary output 1.
   *        Use PercentOutput for standard follower mode.
   */
 static void follow(IMotorController masterToFollow, FollowerType followerType) {
    // int id32 = masterToFollow.getBaseID();
    // int id24 = id32;
    // id24 >>= 16;
    // id24 = (short) id24;
    // id24 <<= 8;
    // id24 |= (id32 & 0xFF);
    // set(ControlMode.Follower, id24);
  }

/**
   * Configure the peak allowable current (when current limit is enabled).
   *
   * Current limit is activated when current exceeds the peak limit for longer
   * than the peak duration. Then software will limit to the continuous limit.
   * This ensures current limiting while allowing for momentary excess current
   * events.
   *
   * For simpler current-limiting (single threshold) use
   * ConfigContinuousCurrentLimit() and set the peak to zero:
   * ConfigPeakCurrentLimit(0).
   *
   * @param amps
   *            Amperes to limit.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   */
  static ErrorCode configPeakCurrentLimit(long handle, int amps, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.peakCurrentLimitAmps = amps;
    return motor.lastError;
  }

  /**
   * Configure the peak allowable duration (when current limit is enabled).
   *
   * Current limit is activated when current exceeds the peak limit for longer
   * than the peak duration. Then software will limit to the continuous limit.
   * This ensures current limiting while allowing for momentary excess current
   * events.
   *
   * For simpler current-limiting (single threshold) use
   * ConfigContinuousCurrentLimit() and set the peak to zero:
   * ConfigPeakCurrentLimit(0).
   *
   * @param milliseconds
   *            How long to allow current-draw past peak limit.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   */
  static ErrorCode configPeakCurrentDuration(long handle, int milliseconds, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.currentLimitMillisecondsPastPeak = milliseconds;
    return motor.lastError;
  }

  /**
   * Configure the continuous allowable current-draw (when current limit is
   * enabled).
   *
   * Current limit is activated when current exceeds the peak limit for longer
   * than the peak duration. Then software will limit to the continuous limit.
   * This ensures current limiting while allowing for momentary excess current
   * events.
   *
   * For simpler current-limiting (single threshold) use
   * ConfigContinuousCurrentLimit() and set the peak to zero:
   * ConfigPeakCurrentLimit(0).
   *
   * @param amps
   *            Amperes to limit.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   */
  static ErrorCode configContinuousCurrentLimit(long handle, int amps, int timeoutMs) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.continuousCurrentLimitAmps = amps;
    return motor.lastError;
  }

  /**
   * Enable or disable Current Limit.
   *
   * @param enable
   *    Enable state of current limit.
   * @see configPeakCurrentLimit, configPeakCurrentDuration,
   *    configContinuousCurrentLimit
   */
  static void enableCurrentLimit(long handle, boolean enable) {
    PhysicalMotorManager motor = motors.get(handle);
    motor.currentLimit = enable;
  }

}