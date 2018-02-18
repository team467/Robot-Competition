/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.MatchConfiguration;
import org.usfirst.frc.team467.robot.simulator.DriveSimulator;

import org.usfirst.frc.team467.robot.XBoxJoystick467.Button;
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
		RobotMap.init(RobotID.PreseasonBot);
		RobotMap.init(RobotID.Competition_1);


		// Make robot objects
		driverstation = DriverStation.getInstance();
		LOGGER.info("Initialized Driverstation");

		drive = Drive.getInstance();

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();


		Grabber grabber = Grabber.getInstance();
		Elevator elevator = Elevator.getInstance();
		// Initialize math lookup table
		LookUpTable.init();

		matchConfig = MatchConfiguration.getInstance();

		//		vision = VisionProcessing.getInstance();

		// TODO: Implement actions.doNothing
		//		autonomous = Actions.doNothing();

		//made usb camera and captures video

		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		//set resolution and frames per second to match driverstation
		cam.setResolution(320, 240);
		cam.setFPS(15);
		
		//		 cam = CameraServer.getInstance().startAutomaticCapture();
		//		//set resolution and frames per second to match driverstation
		//		cam.setResolution(320, 240);
		//		cam.setFPS(15);

		//TODO: Create list of autonomous modes for selector
		// Setup autonomous mode selectors
	}
	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		//		autonomous.terminate();
		//		autonomous = Actions.doNothing();
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");

		driverstation.logJoystickIDs();
		//LOGGER.debug("Right: "	+drive.getRightDistance() + " Left: " + drive.getLeftDistance());
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
		case "StartSwitchSide1A": 
			//			autonomous = Actions.startSwitchSide1A();
			autonomous = Actions.moveDistance(2.0);
			break;
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
	}

	public void testInit() {
	}

	public void testPeriodic() {
		driverstation.readInputs();

		if (driverstation.getDriveJoystick().pressed(Button.a)){
			driverstation.getDriverRumbler().rumble(1000, 1.0);
			LOGGER.info("You pressed a");
		}
		if (driverstation.getDriveJoystick().pressed(Button.b)){ 
			driverstation.getDriverRumbler().rumble(150, 0.5);
			LOGGER.info("You pressed b");
		}

		driverstation.periodic();
//		MatchConfiguration.getInstance().setAllianceColor();
//		MatchConfiguration.getInstance().matchTime();
	}


	public void autonomousPeriodic() {


		//		drive.motionMagicMove(amountToGoLeft, amountToGoRight);

		//		autonomous.run();

		autonomous.run();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.periodic();
		driverstation.readInputs();
		//TODO: Set Min_DRIVE_SPEED in Robot Map.

		double MIN_DRIVE_SPEED = 0.1;
		driverstation.readInputs();

		double left = driverstation.getArcadeSpeed();
		double right = driverstation.getArcadeTurn();

		LOGGER.info("left " + left + " right " + right) ;
		if (Math.abs(left) < MIN_DRIVE_SPEED) {

		// TODO Drive class should handle MIN_DRIVE_SPEED
		double left1 = driverstation.getArcadeSpeed(); // changed the name of left to left1
		double right1 = driverstation.getArcadeTurn(); // changed the name of right to right2

		LOGGER.debug("left " + left1 + " right " + right1);
		if (Math.abs(left1) < RobotMap.MIN_DRIVE_SPEED) {

			left1 = 0.0;
		}
		if (Math.abs(right1) < RobotMap.MIN_DRIVE_SPEED) {
			right1 = 0.0;
		}

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
		case MotionMagic:
			//TODO: Add things here later.
			break;
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

		elevator.move(driverstation.getElevatorSpeed());

		if (grabber.justGotCube()) {
			driverstation.getNavRumbler().rumble(100, 1.0);
		}

		grabber.grab(driverstation.getGrabThrottle());

		//changed to arcade drive
		drive.arcadeDrive(left1, right1, true);

		TiltMonitor.getInstance().periodic();

	}
		driverstation.readInputs();

		if (driverstation.getDriveJoystick().pressed(Button.a)){
			driverstation.getDriverRumbler().rumble(1000, 1.0);
			LOGGER.info("You pressed a");
		}
		if (driverstation.getDriveJoystick().pressed(Button.b)){ 
			driverstation.getDriverRumbler().rumble(150, 0.5);
			LOGGER.info("You pressed b");
		}

		driverstation.periodic();
}
}