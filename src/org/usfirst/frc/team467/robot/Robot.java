package org.usfirst.frc.team467.robot;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.vision.VisionProcessing;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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
	
	private VisionProcessing vision;

	private Gyrometer gyro;

	private Elevator elevator;
	private Grabber grabber;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {

	
		
		// Initialize logging framework
		Logging.init();
		// Initialize RobotMap
		RobotMap.init(RobotMap.RobotID.PreseasonBot);
		
		// Camera for cube detection
		
//		vision.init();
		new Thread (() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
			
			CvSink cvSink = CameraServer.getInstance().getVideo();
			CvSource outputStream = CameraServer.getInstance().putVideo("Camera Video", 640, 480);
			
			Mat source = new Mat();
			Mat output = new Mat();
			
			while(!Thread.interrupted()) {
				cvSink.grabFrame(source);
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
				outputStream.putFrame(output);
				
			}
			
		}).start();
		
		// Make robot objects
		driverstation = DriverStation.getInstance();
		LOGGER.info("Initialized Driverstation");

		drive = Drive.getInstance();

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();
		
		grabber = Grabber.getInstance();
		elevator = Elevator.getInstance();
		
		// Initialize math lookup table
		LookUpTable.init();

		// TODO: Implement actions.doNothing
		//		autonomous = Actions.doNothing();

	}
	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		//		autonomous.terminate();
		//		autonomous = Actions.doNothing();
	}

	public void disabledPeriodic() {
//		LOGGER.trace("Disabled Periodic");
//		LOGGER.info("Angle Measure in Degrees = " + Math.toDegrees(vision.averageAngle()));
//		double angle = vision.angleMeasure();
//		if (!Double.isNaN(angle)) {
//			LOGGER.info("Angle Measure in Degrees = " + Math.toDegrees(angle));
//			LOGGER.info("Angle Measure in Radians = " + angle);			
//		}

	}

	//TODO: Figure out the NetworkTables later.
	//	String[] autoList = {"none", "go"};
	//			 
	//	NetworkTable table = NetworkTable.getTable("SmartDashboard");
	//	table.putStringArray("Auto List", autoList);
	//	LOGGER.debug("Robot Initialized");
	//	
	public void autonomousInit() {
		final String autoMode = SmartDashboard.getString("Auto Selector", "none");

		LOGGER.info(drive);
		// TODO: call appropriate auto modes based on list
		LOGGER.debug("Autonomous init: " + autoMode);
		switch (autoMode) {
		case "none":
			autonomous = Actions.doNothing();
			break;
		default:
			autonomous = Actions.doNothing();
			break;
		}
		LOGGER.info("Init Autonomous:" + autonomous.getName());
		autonomous.enable();
	}

	public void teleopInit() {

		driverstation.readInputs();
		//		autonomous.terminate();
		//		autonomous = Actions.doNothing();
		driverstation.periodic();
	}

	public void testInit() {
		vision.camera();
	}

	public void testPeriodic() {
		vision.showFrame();
//		elevator.periodic();
//		driverstation.readInputs();

	}


	public void autonomousPeriodic() {
//		drive.motionMagicMove(amountToGoLeft, amountToGoRight);
//		autonomous.run();
		drive.arcadeDrive(0, 0);
	}


	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.readInputs();
		//TODO: Set Min_DRIVE_SPEED in Robot Map.
		// TODO Drive class should handle MIN_DRIVE_SPEED
		double MIN_DRIVE_SPEED = 0.1;
		double left = driverstation.getArcadeSpeed();
		double right = driverstation.getArcadeTurn();
		
//		LOGGER.debug("left " + left + " right " + right);
//		LOGGER.debug(grabber.hasCube());
		
		if (Math.abs(left) < MIN_DRIVE_SPEED) {
			left = 0.0;
		}
		if (Math.abs(right) < MIN_DRIVE_SPEED) {
			right = 0.0;
		}
		
		switch (driverstation.getDriveMode()) {
		case ArcadeDrive:
			double speed = driverstation.getArcadeSpeed();
			double turn = driverstation.getArcadeTurn();
			drive.arcadeDrive(speed, turn, true);
			break;
		case TankDrive:	
			double leftTank = driverstation.getDriveJoystick().getLeftStickY();
			double rightTank = driverstation.getDriveJoystick().getRightStickY();
			drive.tankDrive(leftTank, rightTank, true);
			break;
		case MotionMagic:
			//TODO: Add things here later.
			break;
		}
		
		elevator.manualMove(driverstation.getElevatorSpeed());
		LOGGER.debug("Elevator Moving");
		if (grabber.hasCube()) {
			driverstation.getNavRumbler().rumble(100, 1.0);
		}
		
		grabber.grab(driverstation.getGrabThrottle());
		
		//changed to arcade drive
		drive.arcadeDrive(left, right, true);
	}
}