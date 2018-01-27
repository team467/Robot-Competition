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
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.log4j.Logger;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class or the package after creating this project, you must also
 * update the manifest file in the resource directory.
 */

public class Robot extends IterativeRobot {
	private static final Logger LOGGER = Logger.getLogger(Robot.class);

	// Robot objects
	private DriverStation driverstation;
	private Drive drive;
//	private ActionGroup autonomous;

//	private VisionProcessing vision;
	private Gyrometer gyro;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {

		// TODO: Initialize the Robot Map

		// Initialize logging framework
		Logging.init();

		// Make robot objects
		driverstation = DriverStation.getInstance();
		LOGGER.info("inited driverstation");
		drive = Drive.getInstance();

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

		// Initialize math lookup table
		LookUpTable.init();

//		vision = VisionProcessing.getInstance();
		
		// TODO: Implement actions.doNothing
//		autonomous = Actions.doNothing();

		//made usb camera and captures video
		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		//set resolution and frames per second to match driverstation
		cam.setResolution(320, 240);
		cam.setFPS(15);

		//TODO: Create list of autonomous modes for selector
		// Setup autonomous mode selectors
	}
	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		drive.logClosedLoopErrors();
//		autonomous.terminate();
//		autonomous = Actions.doNothing();
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
	}
//TODO: Figure out the NetworkTables later.
//	String[] autoList = {
//			 			"none",
//			 				"go"
//			 		};
//			 
//			 		NetworkTable table = NetworkTable.getTable("SmartDashboard");
//			 		table.putStringArray("Auto List", autoList);
//			 		LOGGER.debug("Robot Initialized");
//	
	public void autonomousInit() {
		final String autoMode = SmartDashboard.getString("Auto Selector", "none");
		drive.initMotionMagicMode();
//	drive.initPositionMode();
		LOGGER.info(drive);
		// TODO: call appropriate auto modes based on list
		LOGGER.debug("Autonomous init: " + autoMode);
//		switch (autoMode) {
//		case "none":
//			autonomous = Actions.doNothing();
//			break;
//		default:
//			autonomous = Actions.doNothing();
//			break;
//		}
//		LOGGER.info("Init Autonomous:" + autonomous.getName());
//		autonomous.enable();
	}

	public void teleopInit() {
		
		driverstation.readInputs();
//		autonomous.terminate();
//		autonomous = Actions.doNothing();
	}

	public void testInit() {
	}

	public void testPeriodic() {
	}

	public void autonomousPeriodic() {
		
		double amountToGoLeft = Double.parseDouble(SmartDashboard.getString("DB/String 0", "1")); //Second value in feet
		double amountToGoRight = Double.parseDouble(SmartDashboard.getString("DB/String 5", "1")); //Second value in feet
		amountToGoLeft = drive.degreesToTicks(amountToGoLeft);
		amountToGoRight = drive.degreesToTicks(amountToGoRight);
		drive.logClosedLoopErrors();
		drive.publishRawSensorValues();
//		drive.PositionModeMove(drive.feetToTicks(amountToGoLeft), drive.feetToTicks(amountToGoRight));
		drive.motionMagicMove(amountToGoLeft, amountToGoRight);
		
//		autonomous.run();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.readInputs();
		//TODO: Set Min_DRIVE_SPEED in Robot Map.
		double MIN_DRIVE_SPEED = 0.1;
		driverstation.readInputs();
		double left = driverstation.getArcadeSpeed();
		double right = driverstation.getArcadeTurn();
		 		// -1* driverstation.getDriveJoystick().getJoystick()
		LOGGER.info("left " + left + " right " + right) ;
		 
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
		
	}

}