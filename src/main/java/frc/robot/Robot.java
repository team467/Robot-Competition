/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.RobotMap.RobotId;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;
import frc.robot.simulator.communications.RobotData;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;

import org.apache.logging.log4j.Logger;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Robot.class.getName());

  private static boolean enableSimulator = false;

  // Robot objects
  private DriverStation467 driverstation;
  private Drive drive;
  private RobotData data;
  private TelemetryBuilder telemetry;
  private CameraSwitcher camera;
  private NetworkTableInstance table;
  private GamePieceController gamePieceController;

  private int tuneSlot = 0;
  private double tuningValue = 0.0;

  public static void enableSimulator() {
    Robot.enableSimulator = true;
  }

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    table = NetworkTableInstance.getDefault();
    // Delete all Network Table keys; relevant ones will be added when they are set
    //table.deleteAllEntries(); // Uncomment to clear table once.
    
    // Initialize RobotMap
    RobotMap.init(RobotId.MINIBOT);

    // Used after init, should be set only by the Simulator GUI
    // this ensures that the simulator is off otherwise.
    if (enableSimulator) {
      RobotMap.setSimulator();
    }

    // Make robot objects
    driverstation = DriverStation467.getInstance();
    LOGGER.info("Initialized Driverstation");
    data = RobotData.getInstance();
    drive = Drive.getInstance();
    telemetry = TelemetryBuilder.getInstance();
    camera = CameraSwitcher.getInstance();
    gamePieceController = GamePieceController.getInstance();

    drive.setPidsFromRobotMap();
    data.log();
    data.send();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    data.log();
    data.send();
    telemetry.updateTable();
  }

  @Override
  public void autonomousInit() {
    LOGGER.info("No Autonomous");
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    LOGGER.info("Init Teleop");
    driverstation.readInputs();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    LOGGER.debug("Match time {}", DriverStation.getInstance().getMatchTime());
    driverstation.readInputs();

    double speed = driverstation.getArcadeSpeed();
    double turn = driverstation.getArcadeTurn();

    if (Math.abs(speed) < RobotMap.MIN_DRIVE_SPEED) {
      speed = 0.0;
    }
    if (Math.abs(turn) < RobotMap.MIN_DRIVE_SPEED) {
      turn = 0.0;
    }

    LOGGER.debug("Driver Station Inputs mode: {} speed: {} turn: {}", 
        driverstation.getDriveMode(), speed, turn);

    switch (driverstation.getDriveMode()) {

      case ArcadeDrive:
        drive.arcadeDrive(speed, turn, true);
        break;

      case CurvatureDrive:
        drive.curvatureDrive(speed, turn, true);
        break;

      case TankDrive:
        double leftTank = driverstation.getDriveJoystick().getLeftStickY();
        double rightTank = driverstation.getDriveJoystick().getRightStickY();
        drive.tankDrive(leftTank, rightTank, true);
        break;

      default:
    }

    gamePieceController.periodic();

    if (driverstation.getNavJoystick().getJoystick().getPOV() == 0) {
      camera.forward();
    } else if (driverstation.getNavJoystick().getJoystick().getPOV() == 90) {
      camera.cargo();
    } else if (driverstation.getNavJoystick().getJoystick().getPOV() == 180) {
      camera.backward();
    } else if (driverstation.getNavJoystick().getJoystick().getPOV() == 270) {
      camera.hatch();
    }

    if (driverstation.restartCamera()) {
      camera.restart();
    }

  }

  @Override
  public void testInit() {
    LOGGER.info("Init Test");
    tuneSlot = Integer.parseInt(SmartDashboard.getString("DB/String 5", "0"));
    switch (tuneSlot) {
      case 0:
      case 1:
        LOGGER.info("Tuning PID Slot {}", tuneSlot);
        drive.readPidsFromSmartDashboard(tuneSlot);
        tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0"));
        LOGGER.info("Tuning Value: " + tuningValue);
        break;
      default:
        LOGGER.info("Invalid Tune Mode: {}", tuneSlot);
    }
    drive.zero();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    switch (tuneSlot) {
      case 0: // Drive PID SLot
        drive.tuneForward(tuningValue, RobotMap.PID_SLOT_DRIVE);
        LOGGER.debug("Distance {} feet", drive.getLeftDistance());
        break;
      case 1: // Turn PID Slot
        drive.tuneTurn(tuningValue, RobotMap.PID_SLOT_TURN);
        LOGGER.debug("Turn {} degrees",Math.toDegrees(drive.getLeftDistance()));
        break;
      default:
    }
  }

  @Override
  public void disabledInit() {
    LOGGER.info("Init Disabled");
    driverstation.readInputs();

  }

  @Override
  public void disabledPeriodic() {
    data.log();
    LOGGER.trace("Disabled Periodic");
    data.log();
    data.send();
    driverstation.readInputs();

    if (driverstation.getNavJoystick().getJoystick().getPOV() == 0) {
      camera.forward();
    } else if (driverstation.getNavJoystick().getJoystick().getPOV() == 90) {
      camera.cargo();
    } else if (driverstation.getNavJoystick().getJoystick().getPOV() == 180) {
      camera.backward();
    } else if (driverstation.getNavJoystick().getJoystick().getPOV() == 270) {
      camera.hatch();
    }

    if (driverstation.restartCamera()) {
      camera.restart();
    }

  }


}
