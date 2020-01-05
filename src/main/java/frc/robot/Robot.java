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
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.sensors.LedI2C;
import frc.robot.sensors.PowerDistributionPanel;
import frc.robot.tuning.TuneController;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.utilities.PerfTimer;
import frc.robot.vision.CameraSwitcher;
import java.io.IOException;
import org.apache.logging.log4j.Logger;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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
  private LedI2C leds;
  private PerfTimer perfTimer;

  public static long time = System.nanoTime();
  public static long previousTime = time;
  public static int dt = 0;
  
  private CANSparkMax smMotor;
  private CANEncoder smEncoder;

  private DifferentialDrive m_myRobot;

  private Joystick m_leftStick;
  private Joystick m_rightStick;

  private static final int leftLeadDeviceID = 1; 
  private static final int leftFollowerDeviceID = 2;

  private static final int rightLeadDeviceID = 3; 
  private static final int rightFollowerDeviceID = 4;

  private CANSparkMax m_leftLeadMotor;
  private CANSparkMax m_leftFollowerMotor;

  private CANSparkMax m_rightLeadMotor;
  private CANSparkMax m_rightFollwerMotor;

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
    RobotMap.init(RobotId.KITBOT2019);
    mode = RobotMode.STARTED;

    m_leftLeadMotor = new CANSparkMax(leftLeadDeviceID, MotorType.kBrushless);
    m_leftFollowerMotor = new CANSparkMax(leftFollowerDeviceID, MotorType.kBrushless);

    m_rightLeadMotor = new CANSparkMax(rightLeadDeviceID, MotorType.kBrushless);
    m_rightFollwerMotor = new CANSparkMax(rightFollowerDeviceID, MotorType.kBrushless);
   
    m_leftFollowerMotor.follow(m_leftLeadMotor);
    m_rightFollwerMotor.follow(m_rightLeadMotor);
    
    m_myRobot = new DifferentialDrive(m_leftLeadMotor, m_rightLeadMotor);

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
    leds = LedI2C.getInstance();

    TuneController.loadTuners();
    drive.setPidsFromRobotMap();
    PowerDistributionPanel.registerPowerDistributionWithTelemetry();

    telemetry = Telemetry.getInstance();
    telemetry.robotMode(mode);
    telemetry.start();
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
    telemetry.robotMode(mode);
    LOGGER.info("No Autonomous");
    perfTimer = PerfTimer.timer("Autonomous");
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
    telemetry.robotMode(mode);
    LOGGER.info("Init Teleop");
    perfTimer = PerfTimer.timer("Teleoperated");
    LOGGER.debug("Match time {}", box(DriverStation.getInstance().getMatchTime()));
    LOGGER.debug("Match time {}", box(DriverStation.getInstance().getMatchTime()));
    smMotor = new CANSparkMax(11, MotorType.kBrushless);
    smEncoder = smMotor.getEncoder();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    perfTimer.start();
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
        driverstation.getDriveMode(), box(speed), box(turn));

    switch (driverstation.getDriveMode()) {

      case ArcadeDrive:
        drive.arcadeDrive(speed, turn, true);
        if (RobotMap.AUTO_CAMERA) {
          camera.autoSwitch(speed);
        }
        
        smMotor.set(speed);
        LOGGER.warn(smEncoder.getPosition());
        break;

      case CurvatureDrive:
        drive.curvatureDrive(speed, turn, true);
        break;

      case TankDrive:
        m_myRobot.tankDrive(m_leftStick.getY(), m_rightStick.getY());

        // double leftTank = driverstation.getDriveJoystick().getLeftStickY();
        // double rightTank = driverstation.getDriveJoystick().getRightStickY();
        // drive.tankDrive(leftTank, rightTank, true);
        break;

      default:
    }


    if (driverstation.restartCamera()) {
      camera.restart();
    }

    perfTimer.end();
  }

  @Override
  public void testInit() {
    mode = RobotMode.TEST;
    telemetry.robotMode(mode);
    TuneController.init();
    perfTimer = PerfTimer.timer("Test Periodic");
  }



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    perfTimer.start();
    TuneController.periodic();
    perfTimer.end();
  }

  @Override
  public void disabledInit() {
    mode = RobotMode.DISABLED;
    telemetry.robotMode(mode);
    leds.whenDisabled();
    PerfTimer.print();
    LOGGER.info("Init Disabled");
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
