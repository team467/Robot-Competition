package org.usfirst.frc.team467.robot;

//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.MatchConfiguration;
import org.usfirst.frc.team467.robot.vision.VisionProcessing;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class or the package after creating this project, you must also
 * update the manifest file in the resource directory.
 */

public class Robot extends TimedRobot {
	private static final Logger LOGGER = Logger.getLogger(Robot.class);
	
	// Robot objects
	private DriverStation driverstation;
	private Drive drive;
	private ActionGroup autonomous;
	private MatchConfiguration matchConfig;
	private VisionProcessing vision;
	private Gyrometer gyro;
	private Elevator elevator;
	private Grabber grabber;
	private TiltMonitor tiltMonitor;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;


	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework
		Logging.init();

		// Initialize RobotMap
		RobotMap.init(RobotID.Competition_1);

		// Make robot objects
		driverstation = DriverStation.getInstance();
		LOGGER.info("Initialized Driverstation");

		drive = Drive.getInstance();
		elevator = Elevator.getInstance();
		grabber = Grabber.getInstance();
		matchConfig = MatchConfiguration.getInstance();
		tiltMonitor = TiltMonitor.getInstance();

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

//		vision = VisionProcessing.getInstance();
//		vision.startVision();
//		//made usb camera and captures video
//		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
//		//set resolution and frames per second to match driverstation
//		cam.setResolution(320, 240);
//		cam.setFPS(15);
		
	}
	
	public void disabledInit() {
		
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
//		drive.logClosedLoopErrors();
	}
	
	public void testInit() {
	}

	public void testPeriodic() {
	}

	double distance = 0.0;
	public void autonomousInit() {
		driverstation.readInputs();
//		matchConfig.load();
//		autonomous = matchConfig.autonomousDecisionTree();
		autonomous = Actions.moveDistance(5);
		LOGGER.info("Init Autonomous:" + autonomous.getName());
		autonomous.enable();
		distance = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0")); //198		
		drive.zero();
		}

	public void autonomousPeriodic() {
		tiltMonitor.periodic();
		elevator.periodic();
		grabber.periodic();
//		autonomous.run();
		drive.moveFeet(distance);
	}

	public void teleopInit() {
//		autonomous.terminate();
		autonomous = Actions.doNothing();
		driverstation.readInputs();
	}
	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.readInputs();
		grabber.grab(driverstation.getGrabThrottle());
		elevator.manualMove(driverstation.getElevatorSpeed());

		tiltMonitor.periodic();
		elevator.periodic();
		
		drive.logClosedLoopErrors();

		double speed = driverstation.getArcadeSpeed();
		double turn = driverstation.getArcadeTurn();
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
	}

}