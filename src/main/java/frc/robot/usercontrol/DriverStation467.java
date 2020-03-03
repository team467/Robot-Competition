package frc.robot.usercontrol;

import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.RobotMap;
import frc.robot.drive.DriveMode;
import frc.robot.logging.Telemetry;
import frc.robot.usercontrol.XBoxJoystick467.Button;

public class DriverStation467 {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(DriverStation467.class.getName());

  private XBoxJoystick467 driverJoy;
  // private OperatorController467 operatorController;
  // private XBoxJoystick467 navJoy;
  private OperatorController467 opCon;

  private Rumbler driverRumbler;
  // private Rumbler navRumbler;

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
    }
    return station;
  }

  /**
   * Private constructor.
   */
  private DriverStation467() {
    driverJoy = new XBoxJoystick467(0, "driver");
    // navJoy = new XBoxJoystick467(1, "nav");
    opCon = new OperatorController467(1);

    driverRumbler = new Rumbler(driverJoy);
    // navRumbler = new Rumbler(navJoy);
  }

  /**
   * Must be called prior to first button read.
   */
  public void readInputs() {
    if (driverJoy != null) {
      driverJoy.read();
      driverRumbler.periodic();
    }
    // if (navJoy != null) {
    //   navJoy.read();
    //   navRumbler.periodic();
    // }
    if (opCon != null) {
      opCon.read();
    }
  }

  public void logJoystickIDs() {
    if (driverJoy != null) {
      driverJoy.logIdentity();
    }
    // if (navJoy != null) {
    //   navJoy.logIdentity();
    // }
  }

  /**
   * Gets joystick instance used by driver.
   *
   * @return
   */
  public XBoxJoystick467 getDriveJoystick() {
    return driverJoy;
  }

  // public XBoxJoystick467 getNavJoystick() {
    // return navJoy;
  // }

  public OperatorController467 getOperatorController() {
    return opCon;
  }

  public Rumbler getDriverRumbler() {
    return driverRumbler;
  }

  // public Rumbler getNavRumbler() {
  //   return navRumbler;
  // }

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

  // indexer TODO change later

  public boolean getIntakeUp() {
    return opCon.down(1);
  }

  public boolean getIntakeDown() {
    return !opCon.down(1);
  }

  public boolean getIntakeFeed() {
    return opCon.down(2);
  }

  public boolean getIntakeReverse() {
    return opCon.down(3);
  }

  public boolean getIndexerManualMode() {
    return opCon.down(4);
  }

  public boolean getIndexerAutoMode() {
    return !opCon.down(4);
  }

  public boolean getIndexerFeed() {
    return opCon.down(5);
  }

  public boolean getIndexerReverse() {
    return opCon.down(6);
  }

  public boolean getShooterManualMode() {
    return opCon.down(7);
  }

  public boolean getShooterAutoMode() {
    return !opCon.down(7);
  }

  public boolean getFlywheelEnabled() {
    return opCon.down(8);
  }

  public boolean getShootButton() {
    return opCon.down(9);
  }

  public boolean getClimberEnable() {
    return opCon.down(10);
  }

  public boolean getClimbUp() {
    return opCon.down(11);
  }

  public boolean getClimbDown() {
    return opCon.down(12);
  }
  
  //Driver controls
  public double getArcadeSpeed() {
    return (RobotMap.CONTROLS_INVERTED_FB == true) ? -driverJoy.getAdjustedSpeed(driverJoy.getLeftStickY())
        : driverJoy.getAdjustedSpeed(driverJoy.getLeftStickY());
  }

  public double getArcadeTurn() {
    return (RobotMap.CONTROLS_INVERTED_TURN == true) ? -driverJoy.getAdjustedTurnSpeed()
        : driverJoy.getAdjustedTurnSpeed();
  }

  public boolean getDriveCameraBack() {
    return driverJoy.pov() == 0;
  }

  public boolean getDriveCameraFront() {
    return driverJoy.pov() == 180;
  }
  
  public boolean getSlow(){
    return driverJoy.getRightTrigger() > 0.9;
  }

  public boolean getTurbo(){
    return driverJoy.getLeftTrigger() > 0.9;
  }

  public boolean getDisableSafety() {
    return driverJoy.down(Button.BumperRight);
  }

}