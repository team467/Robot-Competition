package org.usfirst.frc.team467.robot;

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
	private DriverStation467 driverstation;
	private Drive drive;
	private ActionGroup autonomous;
	private MatchConfiguration matchConfig;
	private VisionProcessing vision;
	private Gyrometer gyro;
	private Elevator elevator;
	private Grabber grabber;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	private Ramps ramps;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework
		Logging.init();

		// Initialize RobotMap
		RobotMap.init(RobotID.Competition_2);

		// Make robot objects
		driverstation = DriverStation467.getInstance();
		LOGGER.info("Initialized Driverstation");

		drive = Drive.getInstance();
		elevator = Elevator.getInstance();
		grabber = Grabber.getInstance();
		matchConfig = MatchConfiguration.getInstance();

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

		if (RobotMap.HAS_CAMERA) {
			vision = VisionProcessing.getInstance();
			vision.startVision();
			//made usb camera and captures video
			UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
			//set resolution and frames per second to match driverstation
			cam.setResolution(320, 240);
			cam.setFPS(15);
		}
		

	}
	
	public void disabledInit() {
		
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
	}
	
	double tuningValue = 0.0;
	
	public void testInit() {
		drive.readPIDSFromSmartDashboard();
		driverstation.readInputs();
		tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0")); //198		
		drive.zero();
	}

	public void testPeriodic() {
		if (tuningValue <= 30.0 && tuningValue >= -30.0) {
			drive.moveFeet(tuningValue);
		} else {
			drive.rotateByAngle(tuningValue);
		}
		drive.logClosedLoopErrors();
	}

	public void autonomousInit() {
		driverstation.readInputs();
		matchConfig.load();
//		autonomous = matchConfig.autonomousDecisionTree();
		autonomous = Actions.rightBasicSwitchRight();
		LOGGER.info("Init Autonomous:" + autonomous.getName());
		autonomous.enable();
		}

	public void autonomousPeriodic() {
		grabber.periodic();
		elevator.move(0); // Will move to height if set.
		autonomous.run();
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
		elevator.move(driverstation.getElevatorSpeed());

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
		if (driverstation.getDeployButtonsDown()) {
			LOGGER.debug("Deploy Buttons down");
			ramps.deploy();
		}

		if (driverstation.getLeftRampButtonPressed()) {
			LOGGER.debug("Left Ramp Button Pressed");
			ramps.toggleLeftState();
		}

		if (driverstation.getRightRampButtonPressed()) {
			LOGGER.debug("Right Ramp Button Pressed");
			ramps.toggleRightState();
		}
		
//		ramps.periodic();

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
		
		drive.logClosedLoopErrors();
	}

}