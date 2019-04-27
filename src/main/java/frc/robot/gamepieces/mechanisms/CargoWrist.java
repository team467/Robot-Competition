package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoWrist extends GamePieceBase implements GamePiece {

  private static CargoWrist instance = null; // set to null

  private static final Logger LOGGER = RobotLogManager.getMainLogger(CargoWrist.class.getName());

  // State
  private controlStates wristControlState = controlStates.Position;

  //init vals
  private static final int TALON_SENSOR_ID = 0;
  private static final int TALON_PID_SLOT_ID = 0;
  private static double simulatedReading = 0.0;
  private static WpiTalonSrxInterface talon;

  //controller instance
  private wristController wristcontroller = new wristController();

  boolean sensorsZeroed;
  
  private CargoWrist() {
    super("Telemetry","CargoWrist");
    if (RobotMap.HAS_CARGO_MECHANISM) {
      talon = TalonProxy.create(RobotMap.CARGO_MECH_WRIST_MOTOR_CHANNEL);
      talon.setName("Telemetry", "Cargo Wrist Motor");
      talon.setInverted(RobotMap.CARGO_MECH_WRIST_MOTOR_INVERTED);
      talon.setSensorPhase(RobotMap.CARGO_MECH_WRIST_SENSOR_INVERTED);
      talon.configSelectedFeedbackSensor(FeedbackDevice.Analog, TALON_PID_SLOT_ID, 
      RobotMap.TALON_TIMEOUT);
      talon.selectProfileSlot(TALON_PID_SLOT_ID, TALON_SENSOR_ID);
      talon.config_kP(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_P, RobotMap.TALON_TIMEOUT);
      talon.config_kI(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_I, RobotMap.TALON_TIMEOUT);
      talon.config_kD(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_D, RobotMap.TALON_TIMEOUT);
      talon.config_kF(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_F, RobotMap.TALON_TIMEOUT);
      talon.configForwardSoftLimitThreshold(RobotMap.CARGO_WRIST_UP_LIMIT_TICKS, RobotMap.TALON_TIMEOUT);
      talon.configReverseSoftLimitThreshold(RobotMap.CARGO_WRIST_DOWN_LIMIT_TICKS, RobotMap.TALON_TIMEOUT);
      talon.configForwardSoftLimitEnable(false, RobotMap.TALON_TIMEOUT);
      talon.configReverseSoftLimitEnable(false, RobotMap.TALON_TIMEOUT);
      talon.configAllowableClosedloopError(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS, RobotMap.TALON_TIMEOUT);
    } else {
      talon = null;
    }
  }

  public static CargoWrist getInstance(){
    if(instance == null){
      instance = new CargoWrist();
    }
    return instance;
  }

  public static int cargoMechWristTickValueIn() {
    return talon.getSensorCollection().getAnalogIn();
  }

  public static int cargoMechWristTickValueInRaw() {
    return talon.getSensorCollection().getAnalogInRaw();
  }


  /**
   * configs PIDs for the wrist talons, should be configured upon initialization from robotmap; however, if manually tuning us this.
   *@param kP The P value
   *@param kI The I value
   *@param kD The D value
   */
  public void configPIDs(double kP, double kI, double kD){
    talon.config_kP(TALON_PID_SLOT_ID, kP, RobotMap.TALON_TIMEOUT);
    talon.config_kI(TALON_PID_SLOT_ID, kI, RobotMap.TALON_TIMEOUT);
    talon.config_kD(TALON_PID_SLOT_ID, kD, RobotMap.TALON_TIMEOUT);
  }

  public synchronized void zeroSensors(){
    //unused
    talon.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
    sensorsZeroed = true;
  }

  private synchronized void manual(double speed) {
    if (RobotMap.HAS_CARGO_MECHANISM) {
      wristControlState = controlStates.PercentOutput;
      wristcontroller.demand = speed;
      LOGGER.debug("Manual override cargo mech wrist Speed: {}, Channel: {}, talon speed = {}, Control mode: {}",
          box(speed), box(talon.getDeviceID()), box(talon.getMotorOutputPercent()), talon.getControlMode());
    }
  }

  public synchronized void read() {
    wristcontroller.wristPositionTicks = cargoMechWristTickValueIn();
    wristcontroller.output = talon.getMotorOutputPercent();
  }

  public synchronized void setPosition(double ticks) {
    double neededPos = proportionalheight(ticks);

    if(wristControlState != controlStates.Position) {
      wristControlState = controlStates.Position;
    }

    wristcontroller.demand = neededPos;
  }

  
  public synchronized void actuate() {
    LOGGER.debug("Actuating cargo mechanism wrist: {}", this);
    if (RobotMap.HAS_CARGO_MECHANISM) {
      if (wristControlState == controlStates.Position) {
        talon.set(ControlMode.Position, wristcontroller.demand);
        if (!RobotMap.useSimulator) { // No sensor collection capability in the talon simulator
          LOGGER.debug("Height set on wrist: {}, Sensor: {}, Error: {}",
              box(wristcontroller.wristPositionTicks), box(talon.getSensorCollection().getAnalogIn()), box(talon.getClosedLoopError(0)));
        }
      }
    } 
  }

  public void tuneMove(double heightProportion) {
    if (heightProportion > 1.0 || heightProportion < 0.0) {
      LOGGER.warn("Tune move needs a number between 0.0 to 1.0");
      return;
    }

    talon.set(ControlMode.Position, proportionalheight(heightProportion));

    LOGGER.debug(" Height proportion: {}, Height set on wrist: {}, Sensor: {}, Error: {}",
        box(heightProportion), box(proportionalheight(heightProportion)), 
        box(talon.getSensorCollection().getAnalogIn()), box(talon.getClosedLoopError(0)));
  }

  public enum controlStates{
    PercentOutput,
    Velocity,
    Position,
    MotionMagic;
  }

  public void manualWristMove(double speed) {
    manual(speed);
  }

  public void periodic() {
    //will fix
    read();
    actuate();
  }

  static void simulatedSensorData(double reading) {
    simulatedReading = reading;
  }

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    if (RobotMap.HAS_CARGO_MECHANISM) {
    }
  }

  public double getTickPos(){
    return wristcontroller.wristPositionTicks;
  }

  //TODO: write more gets

  private double proportionalheight(double heightProportion) {
    double height;
    height =  ((heightProportion) * RobotMap.CARGO_MECH_WRIST_TOP_TICKS + (1.0 - heightProportion) * RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS);
    LOGGER.debug("Setting wrist height for {} to {} ticks", this, box(height));
    return height;
  }

  //add vars as needed
  public static class wristController{

    public double wristPositionTicks;
    public double output;

    public double demand;
  }

  @Override
  public boolean systemCheck() {
    return true;
  }

  @Override
  public void stop() {
    manualWristMove(0.0);
  }

}