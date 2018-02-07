/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
// import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.vision.VisionProcessing;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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

	private VisionProcessing vision;
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
		
		vision = VisionProcessing.getInstance();

		// Initialize logging framework
		Logging.init();
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(320, 240);
			camera.setFPS(30);

			camera.setBrightness(50);
			camera.setWhiteBalanceManual(WhiteBalance.kFixedFluorescent1);
//			camera.setExposureManual(0);
			
//			camera.setBrightness(100);
			camera.setExposureAuto();
			camera.setWhiteBalanceAuto();
			
			
			CvSink cvsink = CameraServer.getInstance().getVideo();
			CvSource outputsource = CameraServer.getInstance().putVideo("CubeCam", 320, 240); //CubeCam
			
			Mat source = new Mat();
			Mat output = new Mat();
			
			while(!Thread.interrupted()) {
				cvsink.grabFrame(source);
				vision.findCube(source);
//				if (!source.empty()) {
//					Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
//					outputsource.putFrame(output);
//				}
			}
		}).start();

		// Make robot objects
		driverstation = DriverStation.getInstance();
		LOGGER.info("inited driverstation");
		drive = Drive.getInstance();

		drive.setDefaultDriveMode();

		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

		// Initialize math lookup table
		LookUpTable.init();

//		vision = VisionProcessing.getInstance();
		
		// TODO: Implement actions.doNothing
//		autonomous = Actions.doNothing();

		//TODO: Create list of autonomous modes for selector
		// Setup autonomous mode selectors
		String[] autoList = {
				"none",
				"go"
		};

		LOGGER.debug("Robot Initialized");
	}

	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		drive.logClosedLoopErrors();
//		autonomous.terminate();
//		autonomous = Actions.doNothing();
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
		LOGGER.info("Angle Measure in Degrees = " + Math.toDegrees(vision.averageAngle()));
//		double angle = vision.angleMeasure();
//		if (!Double.isNaN(angle)) {
//			LOGGER.info("Angle Measure in Degrees = " + Math.toDegrees(angle));
//			LOGGER.info("Angle Measure in Radians = " + angle);			
//		}

	}

	public void autonomousInit() {
		final String autoMode = SmartDashboard.getString("Auto Selector", "none");

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
		drive.setDefaultDriveMode();
		driverstation.readInputs();
//		autonomous.terminate();
//		autonomous = Actions.doNothing();
	}

	public void testInit() {
	}

	public void testPeriodic() {
	}

	public void autonomousPeriodic() {
//		autonomous.run();
		drive.arcadeDrive(0, 0);
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
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
		
		//changed to arcade drive
		drive.arcadeDrive(left, right, true);
	}

}