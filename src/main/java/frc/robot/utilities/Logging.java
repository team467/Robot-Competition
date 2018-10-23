package frc.robot.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import frc.robot.Robot;
import frc.robot.Autonomous.MatchConfiguration;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.Climber;
import frc.robot.gamepieces.Elevator;
import frc.robot.gamepieces.Grabber;
import frc.robot.gamepieces.GrabberSolenoid;

public class Logging {
	public static void init() {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		
		// Modify all the loggers
		config.addLogger(Robot.class.getName(), new LoggerConfig(Robot.class.getName(), Level.INFO, true));
// LOOGER.info("WRITE SOMETHING IN HERE WITH: {}" WriteVaribleHere(), andHereIfThereIsAnotherOne());		
		
		// Enable extra logging for classes you want to debug
		config.addLogger(Climber.class.getName(), new LoggerConfig(Climber.class.getName(), Level.INFO, true));
//		config.addLogger(Action.class.getName(), new LoggerConfig(Action.class.getName(), Level.INFO, true));
		config.addLogger(Drive.class.getName(), new LoggerConfig(Drive.class.getName(), Level.INFO, true));
//		config.addLogger(ActionGroup.class.getName(), new LoggerConfig(ActionGroup.class.getName(), Level.WARN, true));
//		config.addLogger(DriveSimulator.class.getName(), new LoggerConfig(DriveSimulator.class.getName(), Level.WARN, true));
		config.addLogger(Elevator.class.getName(), new LoggerConfig(Elevator.class.getName(), Level.INFO, true));
		config.addLogger(Grabber.class.getName(), new LoggerConfig(Grabber.class.getName(), Level.INFO, true));
		config.addLogger(MatchConfiguration.class.getName(), new LoggerConfig(MatchConfiguration.class.getName(), Level.INFO, true));
//		config.addLogger(OpticalSensor.class.getName(), new LoggerConfig(OpticalSensor.class.getName(), Level.WARN, true));
//		config.addLogger(Ramp.class.getName(), new LoggerConfig(Ramp.class.getName(), Level.INFO, true));
//		config.addLogger(Ramps.class.getName(), new LoggerConfig(Ramps.class.getName(), Level.INFO, true));
//		config.addLogger(frc.robot.simulator.Robot.class.getName(), 
//				new LoggerConfig(frc.robot.simulator.Robot.class.getName(), Level.INFO, true));
//		config.addLogger(RobotShape.class.getName(), new LoggerConfig(RobotShape.class.getName(), Level.WARN, true));
//		config.addLogger(Rumbler.class.getName(), new LoggerConfig(Rumbler.class.getName(), Level.WARN, true));
//		config.addLogger(TalonSpeedControllerGroup.class.getName(), new LoggerConfig(TalonSpeedControllerGroup.class.getName(), Level.INFO, true));
//		config.addLogger(VisionIntegration.class.getName(), new LoggerConfig(VisionIntegration.class.getName(), Level.WARN, true));
//		config.addLogger(XBoxJoystick467.class.getName(), new LoggerConfig(XBoxJoystick467.class.getName(), Level.WARN, true));
//		config.addLogger(TiltMonitor.class.getName(), new LoggerConfig(TiltMonitor.class.getName(), Level.INFO, true));
		config.addLogger(GrabberSolenoid.class.getName(), new LoggerConfig(GrabberSolenoid.class.getName(), Level.INFO, true));

		ctx.updateLoggers();
		
	}
	
}