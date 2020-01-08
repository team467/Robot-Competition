package frc.robot.simulator.drive;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ErrorCollection;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

/**
 * CTRE Talon SRX Motor Controller when used on CAN Bus.
 */
public class TalonSrx extends BaseMotorController
    implements IMotorControllerEnhanced {

  private SensorCollection sensorCollection;

  /**
   * Creates a talon referenced by a specific CAN device number.
   */
  public TalonSrx(int deviceNumber) {
    super(deviceNumber);
    // The original Talon SRX used super(deviceNumber | 0x02040000);
    // Changed to make channel map connection easier to debug.

    //TODO: Simulate sensor collection
    //sensorColl = new SensorCollection(this);
  }

  /**
   * @return object that can get/set individual raw sensor values.
   */
  public SensorCollection getSensorCollection() {
    return sensorCollection;
  }

  /**
   * Sets the period of the given status frame. User ensure CAN Bus utilization is not high.
   *
   * <p>This setting is not persistent and is lost when device is reset.
   * If this is a concern, calling application can use HasReset()
   * to determine if the status frame needs to be reconfigured.
   *
   * @param frame
   *            Frame whose period is to be changed.
   * @param periodMs
   *            Period in ms for the given frame.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
    return super.setStatusFramePeriod(frame.value, periodMs, timeoutMs);
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
  public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {

    return super.getStatusFramePeriod(frame, timeoutMs);
  }

  /**
   * Configures the period of each velocity sample.
   * Every 1ms a position value is sampled, and the delta between that sample
   * and the position sampled kPeriod ms ago is inserted into a filter.
   * kPeriod is configured with this function.
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
  public ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
    return super.configVelocityMeasurementPeriod(period, timeoutMs);
  }

  /**
   * Sets the number of velocity samples used in the rolling average velocity
   * measurement.
   *
   * @param windowSize
   *            Number of samples in the rolling average of velocity
   *            measurement. Valid values are 1,2,4,8,16,32. If another
   *            value is specified, it will truncate to nearest support value.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
    return super.configVelocityMeasurementWindow(windowSize, timeoutMs);
  }

  /**
   * Configures a limit switch for a local/remote source.
   *
   * <p>For example, a CAN motor controller may need to monitor the Limit-R pin
   * of another Talon, CANifier, or local Gadgeteer feedback connector.
   *
   * <p>If the sensor is remote, a device ID of zero is assumed.
   * If that's not desired, use the four parameter version of this function.
   *
   * @param type
   *            Limit switch source.
   *            User can choose between the feedback connector, 
   *            remote Talon SRX, CANifier, or deactivate the feature.
   * @param normalOpenOrClose
   *            Setting for normally open, normally closed, or disabled. This setting
   *            matches the web-based configuration drop down.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for
   *            config success and report an error if it times out.
   *            If zero, no blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configForwardLimitSwitchSource(
      LimitSwitchSource type, 
      LimitSwitchNormal normalOpenOrClose,
      int timeoutMs) {

    return super.configForwardLimitSwitchSource(type, normalOpenOrClose, 0x00000000, timeoutMs);
  }

  /**
   * Configures a limit switch for a local/remote source.
   *
   * <p>For example, a CAN motor controller may need to monitor the Limit-R pin
   * of another Talon, CANifier, or local Gadgeteer feedback connector.
   *
   * <p>If the sensor is remote, a device ID of zero is assumed. If that's not
   * desired, use the four parameter version of this function.
   *
   * @param type
   *            Limit switch source. @see #LimitSwitchSource User can choose
   *            between the feedback connector, remote Talon SRX, CANifier, or
   *            deactivate the feature.
   * @param normalOpenOrClose
   *            Setting for normally open, normally closed, or disabled. This
   *            setting matches the web-based configuration drop down.
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configReverseLimitSwitchSource(
      LimitSwitchSource type, 
      LimitSwitchNormal normalOpenOrClose,
      int timeoutMs) {
    return super.configReverseLimitSwitchSource(type, normalOpenOrClose, 0x00000000, timeoutMs);
  }

  // ------ Current Lim ----------//
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
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   */
  public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
    return  PhysicalMotorManager.configPeakCurrentLimit(handle, amps, timeoutMs);
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
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   */
  public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
    return PhysicalMotorManager.configPeakCurrentDuration(handle, milliseconds, timeoutMs);
  }

  public ErrorCode configPeakCurrentDuration(int milliseconds) {
    int timeoutMs = 0;
    return configPeakCurrentDuration(milliseconds,  timeoutMs);
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
   * @param timeoutMs
   *            Timeout value in ms. If nonzero, function will wait for config
   *            success and report an error if it times out. If zero, no
   *            blocking or checking is performed.
   */
  public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
    return  PhysicalMotorManager.configContinuousCurrentLimit(handle, amps, timeoutMs);
  }

  /**
   * Enable or disable Current Limit.
   * 
   * @param enable
   *            Enable state of current limit.
   * @see configPeakCurrentLimit, configPeakCurrentDuration,
   *      configContinuousCurrentLimit
   */
  public void enableCurrentLimit(boolean enable) {
    PhysicalMotorManager.enableCurrentLimit(handle, enable);
  }

  /**
   * Configures all PID set peristant settings (overloaded so timeoutMs is 50 ms
   * and pidIdx is 0).
   *
   * @param pid               Object with all of the PID set persistant settings
   * @param pidIdx            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @param timeoutMs
   *              Timeout value in ms. If nonzero, function will wait for
   *              config success and report an error if it times out.
   *              If zero, no blocking or checking is performed.
   *
   * @return Error Code generated by function. 0 indicates no error. 
   */
  public ErrorCode configurePid(TalonSRXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
    ErrorCollection errorCollection = new ErrorCollection();

    //------ sensor selection ----------//      

    errorCollection.NewError(baseConfigurePid(pid, pidIdx, timeoutMs));
    errorCollection.NewError(
        configSelectedFeedbackSensor(pid.selectedFeedbackSensor, pidIdx, timeoutMs));
    

    return errorCollection._worstError;
  }

  /**
   * Gets all PID set persistant settings.
   *
 * @param pid               Object with all of the PID set persistant settings
 * @param pidIdx            0 for Primary closed-loop. 1 for auxiliary closed-loop.
   * @param timeoutMs
   *              Timeout value in ms. If nonzero, function will wait for
   *              config success and report an error if it times out.
   *              If zero, no blocking or checking is performed.
   */
  public void getPidConfigs(TalonSRXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
    baseGetPidConfigs(pid, pidIdx, timeoutMs);
    pid.selectedFeedbackSensor = FeedbackDevice.valueOf(
        configGetParameter(ParamEnum.eFeedbackSensorType, pidIdx, timeoutMs));
  }

  /**
   * Configures all peristant settings.
   *
   * @param allConfigs        Object with all of the persistant settings
   * @param timeoutMs
   *              Timeout value in ms. If nonzero, function will wait for
   *              config success and report an error if it times out.
   *              If zero, no blocking or checking is performed.
   *
   * @return Error Code generated by function. 0 indicates no error. 
   */
  public ErrorCode configAllSettings(TalonSRXConfiguration allConfigs, int timeoutMs) {
    ErrorCollection errorCollection = new ErrorCollection();

    errorCollection.NewError(baseConfigAllSettings(allConfigs, timeoutMs));

    //------ limit switch ----------//   
    errorCollection.NewError(
        PhysicalMotorManager.configForwardLimitSwitchSource(
            handle, allConfigs.forwardLimitSwitchSource,
            allConfigs.forwardLimitSwitchNormal, allConfigs.forwardLimitSwitchDeviceID, timeoutMs));
    errorCollection.NewError(
        PhysicalMotorManager.configReverseLimitSwitchSource(
            handle, allConfigs.reverseLimitSwitchSource,
            allConfigs.reverseLimitSwitchNormal, allConfigs.reverseLimitSwitchDeviceID, timeoutMs));
    


    //--------PIDs---------------//

    errorCollection.NewError(configurePid(allConfigs.primaryPID, 0, timeoutMs));
    errorCollection.NewError(configurePid(allConfigs.auxiliaryPID, 1, timeoutMs));
    errorCollection.NewError(configSensorTerm(SensorTerm.Sum0, allConfigs.sum0Term, timeoutMs));
    errorCollection.NewError(configSensorTerm(SensorTerm.Sum1, allConfigs.sum1Term, timeoutMs));
    errorCollection.NewError(configSensorTerm(SensorTerm.Diff0, allConfigs.diff0Term, timeoutMs));
    errorCollection.NewError(configSensorTerm(SensorTerm.Diff1, allConfigs.diff1Term, timeoutMs));
    

    //--------Current Limiting-----//
    errorCollection.NewError(configPeakCurrentLimit(allConfigs.peakCurrentLimit, timeoutMs));
    errorCollection.NewError(configPeakCurrentDuration(allConfigs.peakCurrentDuration, timeoutMs));
    errorCollection.NewError(configContinuousCurrentLimit(
        allConfigs.continuousCurrentLimit, timeoutMs));

    return errorCollection._worstError;
  }

  /**
   * Gets all persistant settings.
   *
   * @param allConfigs        Object with all of the persistant settings
   * @param timeoutMs
   *              Timeout value in ms. If nonzero, function will wait for
   *              config success and report an error if it times out.
   *              If zero, no blocking or checking is performed.
   */
  public void getAllConfigs(TalonSRXConfiguration allConfigs, int timeoutMs) {
  
    baseGetAllConfigs(allConfigs, timeoutMs);

    getPidConfigs(allConfigs.primaryPID, 0, timeoutMs);
    getPidConfigs(allConfigs.auxiliaryPID, 1, timeoutMs);
    allConfigs.sum0Term =  FeedbackDevice.valueOf(
        configGetParameter(ParamEnum.eSensorTerm, 0, timeoutMs));
    allConfigs.sum1Term =  FeedbackDevice.valueOf(
        configGetParameter(ParamEnum.eSensorTerm, 1, timeoutMs));
    allConfigs.diff0Term = FeedbackDevice.valueOf(
        configGetParameter(ParamEnum.eSensorTerm, 2, timeoutMs));
    allConfigs.diff1Term = FeedbackDevice.valueOf(
        configGetParameter(ParamEnum.eSensorTerm, 3, timeoutMs));


    allConfigs.forwardLimitSwitchSource = LimitSwitchSource.valueOf(
        configGetParameter(ParamEnum.eLimitSwitchSource, 0, timeoutMs));
    allConfigs.reverseLimitSwitchSource = LimitSwitchSource.valueOf(
        configGetParameter(ParamEnum.eLimitSwitchSource, 1, timeoutMs));
    allConfigs.peakCurrentLimit 
        = (int) configGetParameter(ParamEnum.ePeakCurrentLimitAmps, 0, timeoutMs);
    allConfigs.peakCurrentDuration 
        = (int) configGetParameter(ParamEnum.ePeakCurrentLimitMs, 0, timeoutMs);
    allConfigs.continuousCurrentLimit 
        = (int) configGetParameter(ParamEnum.eContinuousCurrentLimitAmps, 0, timeoutMs);

  }

  /**
   * Gets all persistant settings (overloaded so timeoutMs is 50 ms).
   *
 * @param allConfigs        Object with all of the persistant settings
   */
  public void getAllConfigs(TalonSRXConfiguration allConfigs) {
    int timeoutMs = 50;
    getAllConfigs(allConfigs, timeoutMs);
  }

  @Override
  public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
    return null; 
  }

}