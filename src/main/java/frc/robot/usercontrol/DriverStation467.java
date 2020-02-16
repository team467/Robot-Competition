package frc.robot.usercontrol;

import frc.robot.RobotMap;
import frc.robot.drive.DriveMode;
import frc.robot.logging.Telemetry;
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
    navJoy = new XBoxJoystick467(1, "nav");

    driverRumbler = new Rumbler(driverJoy);
    navRumbler = new Rumbler(navJoy);

    registerMetrics();
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

  // public boolean getArm() {
  // return navJoy.pov() == 1;
  // }

  // indexer TODO change later
  public boolean indexerManual() {
    return true;
  }

  public boolean getIndexerAutoMode() {
    return false;
  }

  public boolean indexerManualMove() {
    return false;
  }

  public boolean indexerFeed() {
    if (indexerManual()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean indexerReverse() {
    if (indexerManual()) {
      return true;
    } else {
      return false;
    }
  }


  // Intaker TODO change later
  public boolean arm() {
    return false;
  }

  public boolean roller() {
    return false;
  }

  // shooter TODO change later
  public boolean shooterAuto() {
    return false;
  }

  public boolean flywheel() {
    return false;
  }

  public boolean shoot() {
    return false;
  }

  // Rumble
  public void navSetLeftRumble(double value) {
    navJoy.leftRumble(value);
  }

  public void navSetRightRumble(double value) {
    navJoy.rightRumble(value);
  }

  // Driver controls

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

  public void driverSetLeftRumble(double value) {
    driverJoy.leftRumble(value);
  }

  public void driverSetRightRumble(double value) {
    driverJoy.rightRumble(value);
  }

  // public boolean getFireHatch() {
  // return navJoy.down(Button.BumperLeft);
  // }

  public boolean restartCamera() {
    return navJoy.pressed(Button.start);
  }

  public boolean getSlow() {
    return driverJoy.getRightTrigger() > 0.9;
  }

  public boolean getTurbo() {
    return driverJoy.getLeftTrigger() > 0.9;
  }

  // public boolean getDisableSafety() {
  // return driverJoy.down(Button.BumperRight);
  // }

  public void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    if (RobotMap.ENABLE_DRIVER_STATION_TELEMETRY && !RobotMap.useSimulator) {
      telemetry.addBooleanMetric("Input Restart Camera", this::restartCamera);
      telemetry.addBooleanMetric("Input Drive Camera Front", this::getDriveCameraFront);
      telemetry.addBooleanMetric("Input Drive Camera Back", this::getDriveCameraBack);
      telemetry.addDoubleMetric("Input Arcade Speed", this::getArcadeSpeed);
      telemetry.addDoubleMetric("Input Arcade Turn", this::getArcadeTurn);
    }
  }

}