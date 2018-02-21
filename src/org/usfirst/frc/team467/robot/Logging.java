package org.usfirst.frc.team467.robot;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.vision.VisionIntegration;
import org.usfirst.frc.team467.robot.Autonomous.MatchConfiguration;
import org.usfirst.frc.team467.robot.simulator.DriveSimulator;
import org.usfirst.frc.team467.robot.simulator.draw.RobotShape;

public class Logging {
	public static void init() {
		setupDefaultLogging();

		// Enable extra logging for classes you want to debug
		Logger.getLogger(ActionGroup.class).setLevel(Level.WARN);
		Logger.getLogger(Drive.class).setLevel(Level.DEBUG);
		Logger.getLogger(DriveSimulator.class).setLevel(Level.WARN);
		Logger.getLogger(Elevator.class).setLevel(Level.INFO);
		Logger.getLogger(Grabber.class).setLevel(Level.DEBUG);
		Logger.getLogger(MatchConfiguration.class).setLevel(Level.DEBUG);
		Logger.getLogger(OpticalSensor.class).setLevel(Level.WARN);
		Logger.getLogger(Ramp.class).setLevel(Level.INFO);
		Logger.getLogger(Ramps.class).setLevel(Level.INFO);
		Logger.getLogger(Robot.class).setLevel(Level.INFO);
		Logger.getLogger(RobotShape.class).setLevel(Level.WARN);
		Logger.getLogger(Rumbler.class).setLevel(Level.WARN);
		Logger.getLogger(TalonSpeedControllerGroup.class).setLevel(Level.TRACE);
		Logger.getLogger(VisionIntegration.class).setLevel(Level.WARN);
		Logger.getLogger(XBoxJoystick467.class).setLevel(Level.WARN);
		Logger.getLogger(TiltMonitor.class).setLevel(Level.DEBUG);
	}

	private static void setupDefaultLogging() {
		// Create a logging appender that writes our pattern to the console.
		// Our pattern looks like the following:
		// 42ms INFO MyClass - This is my info message
		String pattern = "%rms %p %c - %m%n";
		PatternLayout layout = new PatternLayout(pattern);
		Logger.getRootLogger().addAppender(new ConsoleAppender(layout));
		try {
			RollingFileAppender rollingFileAppender = new RollingFileAppender(layout, "/home/admin/log/Robot467.log");
			rollingFileAppender.setMaxBackupIndex(20);
			rollingFileAppender.setMaximumFileSize(1_000_000);
			rollingFileAppender.rollOver();
			Logger.getRootLogger().addAppender(rollingFileAppender);
		} catch (IOException e) {
			System.out.println("Failed to create log file appender: " + e.getMessage());
		}


		// Set the default log level to INFO.
		Logger.getRootLogger().setLevel(Level.INFO); // changing log level
	}
}