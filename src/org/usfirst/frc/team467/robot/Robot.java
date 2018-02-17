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
	
	private VisionProcessing vision;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework
		Logging.init();
		// Camera for cube detection
		
		new Thread (() -> {
			VisionProcessing vision = VisionProcessing.getInstance();
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
			
			CvSink cvSink = CameraServer.getInstance().getVideo();
			CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
			
			Mat source = new Mat();
			Mat output = new Mat();
			while(!Thread.interrupted()) {
				LOGGER.info("Is this even working?");
				
				LOGGER.info("" + vision.findCube(source));
				cvSink.grabFrame(source);
				outputStream.putFrame(source);
				
			}
			
			
		}).start();
		// Make robot objects
		}
	public void disabledInit() {
		
	}

	public void disabledPeriodic() {
			
//		}

	}
	public void autonomousInit() {
		
	}

	public void teleopInit() {
		LOGGER.info("teleop Initializing");
		 
	}

	public void testInit() {
		
	}

	public void testPeriodic() {
		

	}


	public void autonomousPeriodic() {

	}


	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
	}
}