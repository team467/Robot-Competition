package frc.robot.usercontrol;

import frc.robot.RobotMap;
import frc.robot.autonomous.ActionGroup;
import frc.robot.drive.DriveMode;
import frc.robot.usercontrol.XBoxJoystick467.Button;

public class DriverStation467 {

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
   * @return
   */
  public static DriverStation467 getInstance() {
    if (station == null) {
      station = new DriverStation467();
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

  public boolean getTerminateAuto() {
    // TODO: Manually break out of autonoumous mode
    return true;
  }

  public ActionGroup getActionGroup() {
    // TODO: Get an action group if required
    return null; 
  }

  /**
   * @return true if button to reset the gyroscope selection is pressed
   */
  public boolean getGyroReset() {
    // TODO Check the gyro reset button
    return false;
  }

  public double getFineAdjustTurret() {
    double navVal = (Math.abs(getNavJoystick().getRightStickX()) < 0.3)? 0 : getNavJoystick().getRightStickX();

    return navVal;
  }

  public boolean getAcquireHatch() {
    //Nav
    return getNavJoystick().getRightTrigger() > 0.9;
  }
  
  public boolean getCargoArmLowRocketShipPosition() {
    //Nav
    return getNavJoystick().pov() == 0;
  }

  public boolean getCargoArmCargoShipPosition() {
    //Nav
    return getNavJoystick().pov() == 180;
  }

  public boolean getAutoTargetButtonPressed() {
    //Nav 
    //TODO: check if implemented
    return getNavJoystick().pressed(Button.a);
  }

  public boolean getTurretRight() {
    //Nav
    return getNavJoystick().pressed(Button.b);
  }

  public boolean getTurretHome() {
    //Nav
    return getNavJoystick().pressed(Button.y);
  }

  public boolean getTurretLeft() {
    //NAV
    return getNavJoystick().pressed(Button.x);
  }
  
  public boolean getHatchMode() {
    //NAV
    return getNavJoystick().down(Button.BumperLeft);
  }

  public boolean getCargoMode() {
    return getNavJoystick().down(Button.BumperRight);
  }
  
  public double getWristManualOverride() {

    return getNavJoystick().getLeftStickY();
  }

  public boolean getFireCargo(){
    return getNavJoystick().getLeftTrigger() > 0.9;
  }

  public void navSetLeftRumble(double value) {
    navJoy.leftRumble(value);
  }

  public void navSetRightRumble(double value) {
    navJoy.rightRumble(value);
  }

  //Driver controls

  public double getArcadeSpeed() {
    return (RobotMap.CONTROLS_INVERTED_FB == true) 
      ? -getDriveJoystick().getAdjustedSpeed(driverJoy.getLeftStickY()) 
      :  getDriveJoystick().getAdjustedSpeed(driverJoy.getLeftStickY());
  }
  
  public double getArcadeTurn() {
    return (RobotMap.CONTROLS_INVERTED_TURN == true) 
    ? -getDriveJoystick().getAdjustedTurnSpeed()
    : getDriveJoystick().getAdjustedTurnSpeed();
  }

  public boolean getDriveCameraBack() {
    return getDriveJoystick().pov() == 0;
  }

  public boolean getDriveCameraFront() {
    return getDriveJoystick().pov() == 180;
  }

  public boolean getAcquireBall() {
    return getDriveJoystick().getRightTrigger() == 0.9;
  }

  public boolean defenseMode() {
    if(getDriveJoystick().down(Button.BumperLeft) == true && getDriveJoystick().down(Button.BumperRight) == true) {
      return true;
    } else {
      return false;
    }
  }

  public boolean getRejectBall() {
    return getDriveJoystick().getLeftTrigger() == 0.9;
  }

  public void driverSetLeftRumble(double value) {
    driverJoy.leftRumble(value);
  }

  public void driverSetRightRumble(double value) {
    driverJoy.rightRumble(value);
  }

  public boolean fireHatch() {
    return driverJoy.down(Button.a);
  }

  public boolean restartCamera() {
    return navJoy.pressed(Button.start);
  }

}