package frc.robot.usercontrol;

import frc.robot.RobotMap;
import frc.robot.drive.DriveMode;
import frc.robot.logging.Telemetry;
import frc.robot.usercontrol.XBoxJoystick467.Button;

public class DriverStation467 {

  private XBoxJoystick467 driverJoy;
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

    registerMetrics();
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

  public void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance(); 
    if (RobotMap.ENABLE_DRIVER_STATION_TELEMETRY && !RobotMap.useSimulator) {
      // telemetry.addBooleanMetric("Input Restart Camera", this::restartCamera);
      // telemetry.addBooleanMetric("Input Drive Camera Front", this::getDriveCameraFront);
      // telemetry.addBooleanMetric("Input Drive Camera Back", this::getDriveCameraBack);
      // telemetry.addBooleanMetric("Input Disable Safety", this::getDisableSafety);
      // telemetry.addBooleanMetric("Input Defense Mode", this::getDefenseMode);
      // telemetry.addBooleanMetric("Input Hatch Mode", this::getHatchMode);
      // telemetry.addBooleanMetric("Input Cargo Mode", this::getCargoMode);
      // telemetry.addBooleanMetric("Input Intake Up", this::getIntakeUp);
      // telemetry.addBooleanMetric("Input Intake Down", this::getIntakeDown);
      // telemetry.addBooleanMetric("Input Acquire Ball", this::getAcquireBall);
      // telemetry.addBooleanMetric("Input Fire Cargo", this::getFireCall);
      // telemetry.addBooleanMetric("Input Wrist - Cargo Ship", 
      //     this::getCargoWristCargoShipPosition);
      // telemetry.addBooleanMetric("Input Fire Hatch", this::getFireHatch);
      // telemetry.addBooleanMetric("Input Reject Ball", this::getRejectBall);
      // telemetry.addBooleanMetric("Input Intake Ball", this::getIntakeBall);
      // telemetry.addBooleanMetric("Input Turret Home", this::getTurretHome);
      // telemetry.addBooleanMetric("Input Turret Left", this::getTurretLeft);
      // telemetry.addBooleanMetric("Input Turret Right", this::getTurretRight);
      // telemetry.addBooleanMetric("Input Target Lock", this::getAutoTargetButtonPressed);
      // telemetry.addDoubleMetric("Input Arcade Speed", this::getArcadeSpeed);
      // telemetry.addDoubleMetric("Input Arcade Turn", this::getArcadeTurn);
    }
  }

}