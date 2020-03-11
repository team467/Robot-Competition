/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import static org.apache.logging.log4j.util.Unbox.box;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.RobotMap.RobotId;
import frc.robot.autonomous.ActionGroup;
import frc.robot.autonomous.MatchConfiguration;
import frc.robot.drive.Drive;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.logging.RobotLogManager;
//import frc.robot.logging.Telemetry;
import frc.robot.sensors.PowerDistributionPanel;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.usercontrol.OperatorController467;
import frc.robot.utilities.PerfTimer;
import frc.robot.vision.CameraSwitcher;
import frc.robot.vision.VisionController;
import frc.robot.tuning.TuneController;
import java.io.IOException;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * 
 * 
 */
public class Robot extends TimedRobot {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Robot.class.getName());

  private static boolean enableSimulator = false;

  // Robot objects
  NetworkTable table;
  private DriverStation467 driverstation;
  private Drive drive;
  //private Telemetry telemetry;
  private CameraSwitcher camera;
  // private PerfTimer perfTimer;
  private  GamePieceController gamePieceController;
  public VisionController visionController;
  public MatchConfiguration matchConfig;
  public ActionGroup autonomous;

  public static long time = System.nanoTime();
  public static long previousTime = time;
  public static int dt = 0;

  //private DifferentialDrive m_myRobot;

  private Joystick m_leftStick;
  private Joystick m_rightStick;

  public boolean useVelocity;


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
    RobotMap.init(RobotId.ROBOT_2020);
    mode = RobotMode.STARTED;

    SmartDashboard.putString("DB/String 0", "NO_TEST");

    SmartDashboard.putNumber("Left P", RobotMap.LEFT_DRIVE_PID_P);
    SmartDashboard.putNumber("Left I", RobotMap.LEFT_DRIVE_PID_I);
    SmartDashboard.putNumber("Left D", RobotMap.LEFT_DRIVE_PID_D);
    SmartDashboard.putNumber("Left F", RobotMap.LEFT_DRIVE_PID_F);
    SmartDashboard.putNumber("Left Max Velocity", RobotMap.VELOCITY_MULTIPLIER_LEFT);

    SmartDashboard.putNumber("Ramp Rate", RobotMap.CLOSED_LOOP_RAMP_RATE);

    SmartDashboard.putNumber("Right P", RobotMap.RIGHT_DRIVE_PID_P);
    SmartDashboard.putNumber("Right I", RobotMap.RIGHT_DRIVE_PID_I);
    SmartDashboard.putNumber("Right D", RobotMap.RIGHT_DRIVE_PID_D);
    SmartDashboard.putNumber("Right F", RobotMap.RIGHT_DRIVE_PID_F);
    SmartDashboard.putNumber("Right Max Velocity", RobotMap.VELOCITY_MULTIPLIER_LEFT);

    SmartDashboard.putNumber("Shooter P", RobotMap.SHOOTER_P);
    SmartDashboard.putNumber("Shooter I", RobotMap.SHOOTER_I);
    SmartDashboard.putNumber("Shooter D", RobotMap.SHOOTER_D);
    SmartDashboard.putNumber("Shooter F", RobotMap.SHOOTER_F);
    SmartDashboard.putNumber("Shooter Max Velocity", RobotMap.VELOCITY_MULTIPLIER_SHOOTER);
    

    m_leftStick = new Joystick(0);
    m_rightStick = new Joystick(1);


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
    driverstation = DriverStation467.getInstance();
    drive = Drive.getInstance();
    camera = CameraSwitcher.getInstance();
    gamePieceController = GamePieceController.getInstance();
    visionController = VisionController.getInstance();
    matchConfig = matchConfig.getInstance();
    LiveWindow.disableAllTelemetry();

    TuneController.loadTuners();
    drive.setPidsFromRobotMap();
    //PowerDistributionPanel.registerPowerDistributionWithTelemetry();

    // telemetry = Telemetry.getInstance();
    // telemetry.robotMode(mode);
    // telemetry.start();
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
  }

  @Override
  public void autonomousInit() {
    mode = RobotMode.AUTONOMOUS;
    driverstation.readInputs();
    matchConfig.load();
    autonomous = matchConfig.AutoDecisionTree();
    //telemetry.robotMode(mode);
    LOGGER.info("Autonomous Initialized");
    // perfTimer = PerfTimer.timer("Autonomous");
    autonomous.enable();

  }
  
  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    LOGGER.trace("Autonomous Periodic");
    // gamePieceController.periodic();
    autonomous.run();
  }

  @Override
  public void teleopInit() {
    mode = RobotMode.TELEOP;
    //telemetry.robotMode(mode);
    LOGGER.info("Teleop Initialized");
    // perfTimer = PerfTimer.timer("Teleoperated");
   // LOGGER.debug("Match time {}", box(DriverStation.getInstance().getMatchTime()));

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    //LOGGER.trace("Teleop Periodic");
    // perfTimer.start();

    driverstation.readInputs();

    double speed = driverstation.getArcadeSpeed();
    double turn = driverstation.getArcadeTurn();
    boolean autoAlign = driverstation.getShootButton(); //TODO change this to be a driverstation input

    if (Math.abs(speed) < RobotMap.MIN_DRIVE_SPEED) {
      speed = 0.0;
    }
    if (Math.abs(turn) < RobotMap.MIN_DRIVE_SPEED) {
      turn = 0.0;
    }

    if (driverstation.getSlow()) {
      double multiplier = (RobotMap.USE_VELOCITY_SPEED_CONTROL_FOR_TELOP ? RobotMap.SLOW_VELOCITY_SPEED_MULTIPLIER : RobotMap.SLOW_DRIVE_SPEED_MULTIPLIER);
      speed = speed * multiplier;
      turn = turn * multiplier;
    } else if (!driverstation.getTurbo() && !driverstation.getSlow()) {
      double multiplier = (RobotMap.USE_VELOCITY_SPEED_CONTROL_FOR_TELOP ? RobotMap.NORMAL_VELOCITY_SPEED_MULTIPLIER : RobotMap.NORMAL_DRIVE_SPEED_MULTIPLIER);
      speed = speed * multiplier;
      turn = turn * multiplier;
    }

    //LOGGER.trace("Driver Station Inputs mode: {} speed: {} turn: {}", 
        //driverstation.getDriveMode(), box(speed), box(turn));

    switch (driverstation.getDriveMode()) {
      case ArcadeDrive:
      //auto align will remove control for driver to drive and align until operator lets go
        if (autoAlign && visionController.hasAngle() && gamePieceController.ShooterAuto) {
            drive.arcadeDrive(0 , visionController.setTurn());
            gamePieceController.RobotAligned = visionController.aligned;
          } else {
            drive.arcadeDrive(speed, turn, true);
          }
          
        if (RobotMap.AUTO_CAMERA) {
          camera.autoSwitch(speed);
        }
        break;

      case CurvatureDrive:
        drive.curvatureDrive(speed, turn, true);
        break;

      case TankDrive:
        //m_myRobot.tankDrive(m_leftStick.getY(), m_rightStick.getY());
        // double leftTank = driverstation.getDriveJoystick().getLeftStickY();
        // double rightTank = driverstation.getDriveJoystick().getRightStickY();
        // drive.tankDrive(leftTank, rightTank, true);
        break;

      default:
    }
      gamePieceController.periodic(); 
    

    // perfTimer.end();
  }

  @Override
  public void testInit() {
    mode = RobotMode.TEST;
    //telemetry.robotMode(mode);
    TuneController.init();
    // perfTimer = PerfTimer.timer("Test Periodic");
  }



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    LOGGER.trace("Test Periodic");
    // perfTimer.start();
    TuneController.periodic();
    // perfTimer.end();
    
  }

  @Override
  public void disabledInit() {
    mode = RobotMode.DISABLED;
    //telemetry.robotMode(mode);
    // PerfTimer.print();
    LOGGER.info("Robot Disabled");
  }


  @Override
  public void disabledPeriodic() {
    LOGGER.trace("Disabled Periodic");
    //driverstation.readInputs();
  }

}
