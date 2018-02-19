package org.usfirst.frc.team467.robot;

//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;

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

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	private Elevator elevator;
	private Grabber grabber;

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

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

		matchConfig = MatchConfiguration.getInstance();
		vision = VisionProcessing.getInstance();
		vision.startVision();

		//made usb camera and captures video
		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		//set resolution and frames per second to match driverstation
		cam.setResolution(320, 240);
		cam.setFPS(15);
		
	}

	public void disabledInit() {
		
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
	}
	
	public void testInit() {
	}

	public void testPeriodic() {
		driverstation.readInputs();
		driverstation.periodic();
	}


	public void autonomousInit() {
		matchConfig.load();
		autonomous = matchConfig.autonomousDecisionTree();
		LOGGER.info("Init Autonomous:" + autonomous.getName());
		autonomous.enable();
	}

	public void autonomousPeriodic() {
		autonomous.run();
	}

	public void teleopInit() {
		autonomous.terminate();
		autonomous = Actions.doNothing();
		driverstation.readInputs();
	}
	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.periodic();
		driverstation.readInputs();
		TiltMonitor.getInstance().periodic();
	
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