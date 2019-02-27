package frc.robot.usercontrol;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.RobotMap;
import frc.robot.drive.DriveMode;
import frc.robot.logging.TelemetryBuilder;
import frc.robot.usercontrol.XBoxJoystick467.Button;

public class DriverStation467 extends SendableBase implements Sendable {

  private XBoxJoystick467 driverJoy;
  private XBoxJoystick467 navJoy;

  private Rumbler driverRumbler;
  private Rumbler navRumbler;

  private static DriverStation467 station;

  // Mapping of functions to Controller Buttons for normal operation
  // TODO: Create enum for buttons
  /**
   * Singleton instance of the object.
   *
   * @return the instance
   */
  public static DriverStation467 getInstance() {
    if (station == null) {
      station = new DriverStation467();
      station.initSendable(TelemetryBuilder.getInstance());
    }
    return station;
  }

  /**
   * Private constructor.
   */
  private DriverStation467() {
    driverJoy = new XBoxJoystick467(0, "driver");
    navJoy = new XBoxJoystick467(1, "nav");

    driverRumbler = new Rumbler(driverJoy);
    navRumbler = new Rumbler(navJoy);

    setSubsystem("Telemetry");
    setName("Driver Station");
  }

  /**
   * Must be called prior to first button read.
   */
  public void readInputs() {
    if (driverJoy != null) {
      driverJoy.read();
    }
    if (navJoy != null) {
      navJoy.read();
    }
    driverRumbler.periodic();
    navRumbler.periodic();
  }

  public void logJoystickIDs() {
    if (driverJoy != null) {
      driverJoy.logIdentity();
    }
    if (navJoy != null) {
      navJoy.logIdentity();
    }
  }

  /**
   * Gets joystick instance used by driver.
   *
   * @return
   */
  public XBoxJoystick467 getDriveJoystick() {
    return driverJoy;
  }

  public XBoxJoystick467 getNavJoystick() {
    return navJoy;
  }

  public Rumbler getDriverRumbler() {
    return driverRumbler;
  }

  public Rumbler getNavRumbler() {
    return navRumbler;
  }

  public double getTurnSensivity() {
    return 0.0;
  }

  // All button mappings are accessed through the functions below

  /**
   * returns the current drive mode. Modes lower in the function will override
   * those higher up. only 1 mode can be active at any time
   *
   * @return currently active drive mode.
   */
  public DriveMode getDriveMode() {
    return DriveMode.ArcadeDrive;
  }

  /**
   * @return true if button to reset the gyroscope selection is pressed
   */
  public boolean getGyroReset() {
    // TODO Check the gyro reset button
    return false;
  }

  public double getManualTurretMove() {
    double baseMode = (Math.abs(getNavJoystick().getRightStickX()) < 0.2) ? 0 : getNavJoystick().getRightStickX();
    double squaredInputs = Math.signum(baseMode) * Math.pow(baseMode, 2);
    return (RobotMap.TURRET_SQR_INP)? squaredInputs : baseMode;
  }

  public boolean getAcquireHatch() {
    // Nav getNavJoystick().getRightTrigger() > 0.9
    return getNavJoystick().down(Button.BumperRight);
  }

  public boolean getCargoWristLowRocketPosition() {
    // Nav
    return getNavJoystick().pov() == 0;
  }

  public boolean getCargoWristCargoShipPosition() {
    // Nav
    return getNavJoystick().pov() == 180;
  }

  public boolean getAutoTargetButtonPressed() {
    // Nav
    // TODO: check if implemented
    return getNavJoystick().pressed(Button.a);
  }

  public boolean getTurretRight() {
    // Nav
    return getNavJoystick().pressed(Button.b);
  }

  public boolean getTurretHome() {
    // Nav
    return getNavJoystick().pressed(Button.y);
  }

  public boolean getTurretLeft() {
    // NAV
    return getNavJoystick().pressed(Button.x);
  }

  public boolean getHatchMode() {
    // NAV getNavJoystick().down(Button.BumperLeft)
    return getNavJoystick().getLeftTrigger() > 0.9;
  }

  public boolean getCargoMode() {
    return getNavJoystick().getRightTrigger() > 0.9;
  }

  public double getManualWristMove() {
    double baseMode = (Math.abs(getNavJoystick().getLeftStickY()) < 0.2) ? 0 : getNavJoystick().getLeftStickY();
    double squaredInputs = Math.signum(baseMode) * Math.pow(baseMode, 2);
    return (RobotMap.WRIST_SQR_INP)? squaredInputs : baseMode;
  }

  public boolean getFireCall() {
    return getNavJoystick().down(Button.BumperLeft);
  }

  public void navSetLeftRumble(double value) {
    navJoy.leftRumble(value);
  }

  public void navSetRightRumble(double value) {
    navJoy.rightRumble(value);
  }

  // Driver controls

  public double getArcadeSpeed() {
    return (RobotMap.CONTROLS_INVERTED_FB == true) ? -getDriveJoystick().getAdjustedSpeed(driverJoy.getLeftStickY())
        : getDriveJoystick().getAdjustedSpeed(driverJoy.getLeftStickY());
  }

  public double getArcadeTurn() {
    return (RobotMap.CONTROLS_INVERTED_TURN == true) ? -getDriveJoystick().getAdjustedTurnSpeed()
        : getDriveJoystick().getAdjustedTurnSpeed();
  }

  public boolean getDriveCameraBack() {
    return getDriveJoystick().pov() == 0;
  }

  public boolean getDriveCameraFront() {
    return getDriveJoystick().pov() == 180;
  }

  public boolean getAcquireBall() {
    return getNavJoystick().down(Button.y);// getDriveJoystick().getRightTrigger() >= 0.9;
  }

  public boolean getDefenseMode() {
    if (getDriveJoystick().down(Button.start) == true && getDriveJoystick().down(Button.back) == true) {
      return true;
    } else {
      return false;
    }
  }

  public boolean getRejectBall() {
    return getDriveJoystick().down(Button.BumperLeft);
  }

  public boolean getIntakeBall() {
    return getDriveJoystick().down(Button.BumperRight);
  }

  public void driverSetLeftRumble(double value) {
    driverJoy.leftRumble(value);
  }

  public void driverSetRightRumble(double value) {
    driverJoy.rightRumble(value);
  }

  public boolean getFireHatch() {
    return getNavJoystick().down(Button.BumperLeft);
  }

  public boolean restartCamera() {
    return driverJoy.pressed(Button.start);
  }

  public boolean getSlow(){
    return getDriveJoystick().getRightTrigger() > 0.9;
  }

  public boolean getTurbo(){
    return getDriveJoystick().getLeftTrigger() > 0.9;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    if (RobotMap.ENABLE_DRIVER_STATION_TELEMETRY && !RobotMap.useSimulator) {
      builder.addBooleanProperty("Input Restart Camera", this::restartCamera, null);
      builder.addBooleanProperty("Input Drive Camera Front", this::getDriveCameraFront, null);
      builder.addBooleanProperty("Input Drive Camera Back", this::getDriveCameraBack, null);
      builder.addBooleanProperty("Input Defense Mode", this::getDefenseMode, null);
      builder.addBooleanProperty("Input Hatch Mode", this::getHatchMode, null);
      builder.addBooleanProperty("Input Cargo Mode", this::getCargoMode, null);
      builder.addBooleanProperty("Input Acquire Ball", this::getAcquireBall, null);
      builder.addBooleanProperty("Input Fire Cargo", this::getFireCall, null);
      builder.addBooleanProperty("Input Wrist - Cargo Ship", 
          this::getCargoWristCargoShipPosition, null);
      builder.addBooleanProperty("Input Wrist - Low Rocket", 
          this::getCargoWristLowRocketPosition, null);
      builder.addBooleanProperty("Input Acquire Hatch", this::getAcquireHatch, null);
      builder.addBooleanProperty("Input Fire Hatch", this::getFireHatch, null);
      builder.addBooleanProperty("Input Reject Ball", this::getRejectBall, null);
      builder.addBooleanProperty("Input Intake Ball", this::getIntakeBall, null);
      builder.addBooleanProperty("Input Turret Home", this::getTurretHome, null);
      builder.addBooleanProperty("Input Turret Left", this::getTurretLeft, null);
      builder.addBooleanProperty("Input Turret Right", this::getTurretRight, null);
      builder.addBooleanProperty("Input Target Lock", this::getAutoTargetButtonPressed, null);
      builder.addDoubleProperty("Input Manual Wrist", this::getManualWristMove, null);
      builder.addDoubleProperty("Input Manual Turret", this::getManualTurretMove, null);
      builder.addDoubleProperty("Input Arcade Speed", this::getArcadeSpeed, null);
      builder.addDoubleProperty("Input Arcade Turn", this::getArcadeTurn, null);
    }
  }

}