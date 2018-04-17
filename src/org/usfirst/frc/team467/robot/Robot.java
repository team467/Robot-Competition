package org.usfirst.frc.team467.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final Logger LOGGER = LogManager.getLogger(Robot.class);

	// Robot objects
	private DriverStation467 driverstation;
	private Drive drive;
	private ActionGroup autonomous;
	private MatchConfiguration matchConfig;
	private VisionProcessing vision;
	private Gyrometer gyro;
	private Elevator elevator;
	private Grabber grabber;
	private Ramps ramps;

	private NetworkTableInstance table;
	private NetworkTable dashboard;

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

		// Delete all Network Table keys; relevant ones will be added when they are set
		table = NetworkTableInstance.getDefault();
		dashboard  = table.getTable("SmartDashboard");
		//table.deleteAllEntries();

		// Initialize RobotMap
		RobotMap.init(RobotID.Competition_2);

		// Make robot objects
		driverstation = DriverStation467.getInstance();
		LOGGER.info("Initialized Driverstation");

		drive = Drive.getInstance();
		elevator = Elevator.getInstance();
		grabber = Grabber.getInstance();
		matchConfig = MatchConfiguration.getInstance();

		ramps = Ramps.getInstance();
		ramps.reset();

		drive.configPeakOutput(1.0);

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
		LOGGER.info("Init Disabled");
		ramps.reset();
//		drive.logClosedLoopErrors();
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
		String[] autoList = {"None", "Just_Go_Forward", "Left_Switch_Only", "Left_Basic", "Left_Advanced", "Left_Our_Side_Only",
				"Center", "Center_Advanced", "Right_Switch_Only", "Right_Basic", "Right_Advanced", "Right_Our_Side_Only"};
		dashboard.getEntry("Auto List").setStringArray(autoList);
		LOGGER.info("Selected Auto Mode: " + SmartDashboard.getString("Auto Selector", "None"));
	}

	double tuningValue = 0.0;

	public void testInit() {
		LOGGER.info("Init Test");
		drive.setPIDSFromRobotMap();
		driverstation.readInputs();
		tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0"));
		LOGGER.info("Tuning Value: " + tuningValue);
		if (tuningValue <= 30.0 && tuningValue >= -30.0) {
			drive.readPIDSFromSmartDashboard(RobotMap.PID_SLOT_DRIVE);
		} else {
			drive.readPIDSFromSmartDashboard(RobotMap.PID_SLOT_TURN);
		}
		drive.zero();
	}

	public void testPeriodic() {
		if (tuningValue <= 30.0 && tuningValue >= -30.0) {
			drive.tuneForward(tuningValue, RobotMap.PID_SLOT_DRIVE);
		} else {
			drive.tuneTurn(tuningValue, RobotMap.PID_SLOT_TURN);
		}
		//drive.logClosedLoopErrors();
	}

	public void autonomousInit() {
		driverstation.readInputs();
		matchConfig.load();
		autonomous = matchConfig.autonomousDecisionTree();
		LOGGER.info("Init Autonomous: {}", autonomous.getName());
		ramps.reset();
		autonomous.enable();
	}

	public void autonomousPeriodic() {
		grabber.periodic();
		elevator.move(0); // Will move to height if set.
		autonomous.run();
	}

	public void teleopInit() {
		LOGGER.info("Init Teleop");
		autonomous = Actions.doNothing();
		drive.configPeakOutput(1.0);
		driverstation.readInputs();
		ramps.reset();
	}
	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.readInputs();

		grabber.grab(driverstation.getGrabThrottle());
		elevator.move(driverstation.getElevatorSpeed());
		drive.setRamp(elevator.getHeight());

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

		if (ramps.isDeployed()) {
			if (driverstation.getLeftRampLiftButton()) {
				ramps.liftLeft();
			}
			if (driverstation.getLeftRampDropButton()) {
				ramps.dropLeft();
			}
			if (driverstation.getRightRampLiftButton()) {
				ramps.liftRight();
			}
			if (driverstation.getRightRampDropButton()) {
				ramps.dropRight();
			}
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
//	        drive.logTelemetry(speed, turn);
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