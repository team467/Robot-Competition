/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap.RobotId;
import frc.robot.autonomous.ActionGroup;
import frc.robot.autonomous.Actions;
import frc.robot.autonomous.MatchConfiguration;
import frc.robot.drive.Drive;
import frc.robot.drive.motorcontrol.TestMotorControl;
import frc.robot.gamepieces.Climber;
import frc.robot.gamepieces.Elevator;
import frc.robot.gamepieces.Grabber;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.communications.RobotData;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.VisionProcessing;
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
  private ActionGroup autonomous;
  private Climber climber;
  private DriverStation467 driverstation;
  private Drive drive;
  private Elevator elevator;
  private Grabber grabber;
  private MatchConfiguration matchConfig;
  private RobotData data;
  private TestMotorControl testMotorControl;
  private VisionProcessing vision;

  private NetworkTableInstance table;
  private NetworkTable dashboard;

  /**
   * Time in milliseconds.
   */
  private double time;

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

    // Delete all Network Table keys; relevant ones will be added when they are set
    table = NetworkTableInstance.getDefault();
    dashboard  = table.getTable("SmartDashboard");
    //table.deleteAllEntries(); // Uncomment to clear table once.
    
    // Initialize RobotMap
    RobotMap.init(RobotId.Competition_2);

    // Used after init, should be set only by the Simulator GUI
    // this ensures that the simulator is off otherwise.
    if (enableSimulator) {
      RobotMap.setSimulator();
    }

    // Make robot objects
    driverstation = DriverStation467.getInstance();
    LOGGER.info("Initialized Driverstation");

    climber = Climber.getInstance();
    data = RobotData.getInstance();
    drive = Drive.getInstance();
    elevator = Elevator.getInstance();
    grabber = Grabber.getInstance();
    matchConfig = MatchConfiguration.getInstance();


    if (RobotMap.HAS_CAMERA) {
      vision = VisionProcessing.getInstance();
      vision.startVision();
      //made usb camera and captures video
      UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
      //set resolution and frames per second to match driverstation
      cam.setResolution(320, 240);
      cam.setFPS(15);
    }

    drive.setPidsFromRobotMap();
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
  }

  @Override
  public void autonomousInit() {
    driverstation.readInputs();
    matchConfig.load();
    autonomous = matchConfig.autonomousDecisionTree();
    LOGGER.info("Init Autonomous: {}", autonomous.getName());
    autonomous.enable();
  }

  /**
   * This function is called periodically during autonomous.
   */

   /**
    * Need to implement cancelling for autonomous
    */
  @Override
  public void autonomousPeriodic() {
    grabber.periodic();
    elevator.move(0); // Will move to height if set.
    autonomous.run();
  }

  @Override
  public void teleopInit() {
    LOGGER.info("Init Teleop");
    autonomous = Actions.doNothing();
    //drive.configPeakOutput(1.0);
    driverstation.readInputs();
    grabber.reset();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    LOGGER.debug("Match time {}", DriverStation.getInstance().getMatchTime());
    driverstation.readInputs();

    grabber.grab(driverstation.getGrabThrottle());
    elevator.move(driverstation.getElevatorSpeed());

    //grabber open and close
    if (driverstation.getGrabberOpen()) {
      LOGGER.info("Grabber Open");
      grabber.open();
    } else {
      LOGGER.info("Grabber Close");
      grabber.close();
    }

    if (driverstation.getFloorHeightButtonPressed()) {
      LOGGER.info("Dropping to bottom height");
      elevator.moveToHeight(Elevator.Stops.floor);
    } else if (driverstation.getSwitchHeightButtonPressed()) {
      LOGGER.info("Lifting to switch height");
      elevator.moveToHeight(Elevator.Stops.fieldSwitch);
    } else if (driverstation.getLowScaleHeightButtonPressed()) {
      LOGGER.info("Lifting to low scale height");
      elevator.moveToHeight(Elevator.Stops.lowScale);
    } else if (driverstation.getHighScaleHeightButtonPressed()) {
      LOGGER.info("Lifting to high scale height");
      elevator.moveToHeight(Elevator.Stops.highScale);
    }
    
    // Ramps state machines protect against conflicts
    if (driverstation.getClimbUp()) {
      LOGGER.debug("Climb Up");
      climber.climbUp();
    } else {
      climber.neutral();
      LOGGER.debug("Not climbing");
    }

    double speed = driverstation.getArcadeSpeed();
    double turn = driverstation.getArcadeTurn();

    if (Math.abs(speed) < RobotMap.MIN_DRIVE_SPEED) {
      speed = 0.0;
    }
    if (Math.abs(turn) < RobotMap.MIN_DRIVE_SPEED) {
      turn = 0.0;
    }

    switch (driverstation.getDriveMode()) {
    
      case ArcadeDrive:
        drive.arcadeDrive(speed, turn, true);
        //drive.logTelemetry(speed, turn);
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
      case 2:
        LOGGER.info("Testing motor control.");
        testMotorControl = new TestMotorControl();
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
      case 2: // New motor control
        testMotorControl.periodic();
        break;
      default:
    }
  }

  @Override
  public void disabledInit() {
    LOGGER.info("Init Disabled");
    //drive.logClosedLoopErrors();
  }

  @Override
  public void disabledPeriodic() {
    LOGGER.trace("Disabled Periodic");
    String[] autoList = {
      "None", 
      "Just_Go_Forward", 
      "Left_Switch_Only", 
      "Left_Basic", 
      "Left_Advanced", 
      "Left_Our_Side_Only",
      "Center", 
      "Center_Advanced", 
      "Right_Switch_Only", 
      "Right_Basic", 
      "Right_Advanced", 
      "Right_Our_Side_Only"
    };
    dashboard.getEntry("Auto List").setStringArray(autoList);
    //LOGGER.info("Selected Auto Mode: " + SmartDashboard.getString("Auto Selector", "None"));
  }

}
