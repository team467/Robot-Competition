package org.usfirst.frc.team467.robot;

import java.util.zip.Deflater;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectFileRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectWriteRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.usfirst.frc.team467.robot.Autonomous.Action;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.vision.VisionIntegration;
import org.usfirst.frc.team467.robot.Autonomous.MatchConfiguration;
import org.usfirst.frc.team467.robot.simulator.DriveSimulator;
import org.usfirst.frc.team467.robot.simulator.draw.RobotShape;

public class Logging {
	public static void init() {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		
		// Modify all the loggers
		config.addLogger(Robot.class.getName(), new LoggerConfig(Robot.class.getName(), Level.INFO, true));
		
		// Enable extra logging for classes you want to debug

		config.addLogger(Action.class.getName(), new LoggerConfig(Action.class.getName(), Level.INFO, true));
		config.addLogger(Drive.class.getName(), new LoggerConfig(Drive.class.getName(), Level.TRACE, true));
		config.addLogger(ActionGroup.class.getName(), new LoggerConfig(ActionGroup.class.getName(), Level.WARN, true));
		config.addLogger(DriveSimulator.class.getName(), new LoggerConfig(DriveSimulator.class.getName(), Level.WARN, true));
		config.addLogger(Elevator.class.getName(), new LoggerConfig(Elevator.class.getName(), Level.INFO, true));
		config.addLogger(Grabber.class.getName(), new LoggerConfig(Grabber.class.getName(), Level.DEBUG, true));
		config.addLogger(MatchConfiguration.class.getName(), new LoggerConfig(MatchConfiguration.class.getName(), Level.INFO, true));
		config.addLogger(OpticalSensor.class.getName(), new LoggerConfig(OpticalSensor.class.getName(), Level.WARN, true));
		config.addLogger(Ramp.class.getName(), new LoggerConfig(Ramp.class.getName(), Level.INFO, true));
		config.addLogger(Ramps.class.getName(), new LoggerConfig(Ramps.class.getName(), Level.INFO, true));
		config.addLogger(org.usfirst.frc.team467.robot.simulator.Robot.class.getName(), 
				new LoggerConfig(org.usfirst.frc.team467.robot.simulator.Robot.class.getName(), Level.INFO, true));
		config.addLogger(RobotShape.class.getName(), new LoggerConfig(RobotShape.class.getName(), Level.WARN, true));
		config.addLogger(Rumbler.class.getName(), new LoggerConfig(Rumbler.class.getName(), Level.WARN, true));
		config.addLogger(TalonSpeedControllerGroup.class.getName(), new LoggerConfig(TalonSpeedControllerGroup.class.getName(), Level.INFO, true));
		config.addLogger(VisionIntegration.class.getName(), new LoggerConfig(VisionIntegration.class.getName(), Level.WARN, true));
		config.addLogger(XBoxJoystick467.class.getName(), new LoggerConfig(XBoxJoystick467.class.getName(), Level.WARN, true));
		config.addLogger(TiltMonitor.class.getName(), new LoggerConfig(TiltMonitor.class.getName(), Level.INFO, true));

//		PatternLayout patternLayout = PatternLayout.newBuilder()
//				.withPattern("%rms %p %c - %m%n")
//				.build();
//		DirectWriteRolloverStrategy strategy = DirectWriteRolloverStrategy.newBuilder()
//				.withMaxFiles("20")
//				.withCompressionLevelStr("" + Deflater.NO_COMPRESSION) // No compression
//				.build();
		
//		RollingFileAppender appender = RollingFileAppender.newBuilder()
//				.withName("RollingFile")
//				.withAppend(false)
//				.withBufferedIo(true)
//				.withFilter(null)
//				.withLayout(patternLayout)
////				.withStrategy(strategy)
//				.withPolicy(SizeBasedTriggeringPolicy.createPolicy("1M"))
//				.withPolicy(OnStartupTriggeringPolicy.createPolicy(1))
//				.withFileName("./Robot467.log")
//				.withFilePattern("./archive/rolling-%d{MM-dd-yy}.log")
////				.withFileName("/home/admin/log/Robot467.log")
//				.build();
//		System.out.println(appender.getName());
//		
//		config.addAppender(RollingFileAppender.createAppender("./Robot467.log", "./archive/rolling-%d{MM-dd-yy}.log",
//				true, "Rolling",
//				true, 8192, true, 
//				SizeBasedTriggeringPolicy.createPolicy("1M"),
////					strategy, 
////					layout, 
//					null, true, false, null, config));
//	
		
		ctx.updateLoggers();
		
	}
	
//	private static void setupDefaultLogging() {
//		// Create a logging appender that writes our pattern to the console.
//		// Our pattern looks like the following:
//		// 42ms INFO MyClass - This is my info message
//		String pattern = "%rms %p %c - %m%n";
////		PatternLayout layout = new PatternLayout(pattern);
//        
//		
//		//		Logger.getRootLogger().addAppender(new ConsoleAppender(layout));
////		try {
////			RollingFileAppender rollingFileAppender = new RollingFileAppender(layout, "/home/admin/log/Robot467.log");
////			rollingFileAppender.setMaxBackupIndex(20);
////			rollingFileAppender.setMaximumFileSize(1_000_000);
////			rollingFileAppender.rollOver();
////			Logger.getRootLogger().addAppender(rollingFileAppender);
////		} catch (IOException e) {
////			System.out.println("Failed to create log file appender: " + e.getMessage());
////		}
//
//	}

}