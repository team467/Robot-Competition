/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;

import frc.robot.RobotMap.RobotId;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.sensors.LedI2C;
import frc.robot.sensors.PowerDistributionPanel;
import frc.robot.tuning.TuneController;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;

import java.io.IOException;

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
  NetworkTable table;
  private DriverStation467 driverstation;
  private Drive drive;
  private Telemetry telemetry;
  private CameraSwitcher camera;
  private GamePieceController gamePieceController;
  private LedI2C leds;

  public static long time = System.nanoTime();
  public static long previousTime = time;
  public static int dt = 0;

  /**
   * Used for timing.
   */
  public static void tick() {
    dt = (int) (time - previousTime);
    previousTime = time;
    time = System.nanoTime();
  }

  public static void enableSimulator() {
    Robot.enableSimulator = true;
  }

  // For tracking state in telemetry
  public enum RobotMode {
    STARTED, DISABLED, AUTONOMOUS, TELEOP, TEST, EXTERNAL_TEST // Not for test periodic
  }

  private RobotMode mode;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {

    // Delete all Network Table keys; relevant ones will be added when they are set
    // NetworkTableInstance.getDefault().deleteAllEntries(); // Uncomment to clear
    // table once.

    // Initialize RobotMap
    RobotMap.init(RobotId.ROBOT_2019);
    mode = RobotMode.STARTED;

    // Used after init, should be set only by the Simulator GUI
    // this ensures that the simulator is off otherwise.
    if (enableSimulator) {
      RobotMap.setSimulator();
    }

    // Mounting USB
    if (!RobotMap.useSimulator) { // Only mount on the RoboRIO
      ProcessBuilder builder = new ProcessBuilder();
      builder.command("sudo", "mount", "/dev/sda1", "/media");
      try {
        builder.start();
      } catch (IOException e) {
        e.printStackTrace();
      }  
    }

    // Make robot objects
    telemetry = Telemetry.getInstance();
    driverstation = DriverStation467.getInstance();
    drive = Drive.getInstance();
    camera = CameraSwitcher.getInstance();
    gamePieceController = GamePieceController.getInstance();
    leds = LedI2C.getInstance();

    TuneController.loadTuners();
    drive.setPidsFromRobotMap();
    PowerDistributionPanel.registerPowerDistributionWithTelemetry();

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */

  @Override
  public void robotPeriodic() {
    telemetry.robotMode(mode);
    telemetry.updateTable();
  }

  @Override
  public void autonomousInit() {
    mode = RobotMode.AUTONOMOUS;
    LOGGER.info("No Autonomous");
    gamePieceController.runOnTeleopInit();
  }
  
  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();
  }

  @Override
  public void teleopInit() {
    mode = RobotMode.TELEOP;
    LOGGER.info("Init Teleop");
    LOGGER.debug("Match time {}", DriverStation.getInstance().getMatchTime());
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    driverstation.readInputs();

    double speed = driverstation.getArcadeSpeed();
    double turn = driverstation.getArcadeTurn();

    if (Math.abs(speed) < RobotMap.MIN_DRIVE_SPEED) {
      speed = 0.0;
    }
    if (Math.abs(turn) < RobotMap.MIN_DRIVE_SPEED) {
      turn = 0.0;
    }

    if (driverstation.getSlow()) {
      speed = speed * RobotMap.SLOW_DRIVE_SPEED_MULTIPLIER;
      turn = turn * RobotMap.SLOW_DRIVE_SPEED_MULTIPLIER;
    } else if (!driverstation.getTurbo() && !driverstation.getSlow()) {
      speed = speed * RobotMap.NORMAL_DRIVE_SPEED_MULTIPLIER;
      turn = turn * RobotMap.NORMAL_DRIVE_SPEED_MULTIPLIER;
    }

    LOGGER.debug("Driver Station Inputs mode: {} speed: {} turn: {}", 
        driverstation.getDriveMode(), speed, turn);

    switch (driverstation.getDriveMode()) {

      case ArcadeDrive:
        drive.arcadeDrive(speed, turn, true);
        if (RobotMap.AUTO_CAMERA) {
          camera.autoSwitch(speed);
        }
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

    if (driverstation.restartCamera()) {
      camera.restart();
    }

  }

  @Override
  public void testInit() {
    mode = RobotMode.TEST;
    TuneController.init();
  }



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    TuneController.periodic();
  }

  @Override
  public void disabledInit() {
    leds.whenDisabled();
    LOGGER.info("Init Disabled");
    telemetry.flush();
  }


  @Override
  public void disabledPeriodic() {
    LOGGER.trace("Disabled Periodic");
    leds.whenDisabled();
    
    driverstation.readInputs();
    if (driverstation.restartCamera()) {
      camera.restart();
    } else {
      camera.fourWaySwitch(driverstation.getNavJoystick().getJoystick().getPOV());
    }

  }

}
