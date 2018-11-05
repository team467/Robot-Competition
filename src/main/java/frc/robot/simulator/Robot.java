/**
 * 
 */
package frc.robot.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotID;
import frc.robot.drive.motorcontrol.TestMotorControl;
import frc.robot.Autonomous.ActionGroup;
import frc.robot.Autonomous.MatchConfiguration;
import frc.robot.simulator.communications.RobotData;
import frc.robot.simulator.gui.MapController;
import frc.robot.simulator.gui.SimulatedData;
import frc.robot.utilities.Logging;

/**
 * The simulated robot
 */
public class Robot {

	private static final Logger LOGGER = LogManager.getLogger(Robot.class);

	DriveSimulator drive;

	MapController simulatorView;

	RobotData data;

	ActionGroup autonomous;

	TestMotorControl testMotorControl;

	private MatchConfiguration matchConfig;

	public void robotInit() {

		Logging.init();

		RobotMap.init(RobotID.Competition_1);
		RobotMap.useSimulator = true;
		RobotMap.USE_FAKE_GAME_DATA = true;

		drive = DriveSimulator.getInstance();
		matchConfig = MatchConfiguration.getInstance();

		data = RobotData.getInstance();
//		data.startServer();

		LOGGER.info("Started the robot simulator");

	}

	public void setView(MapController simulatorView) {
		this.simulatorView = simulatorView;
	}

	/*
	 * starting coordinates:
	 * Left: (2.5, 0)
	 * Center: (12.5, 0)
	 * Right: (21.58, 0)
	 */
	public void autonomousInit() {
		LOGGER.info("Init Test");

		drive.zero();
		matchConfig.load();
		
		testMotorControl = new TestMotorControl();
		if (SimulatedData.autoMode.startsWith("Left")) {
			data.startingLocation(2.5, 0);
		} else if (SimulatedData.autoMode.startsWith("Center")) {
			data.startingLocation(12.5, 0);
		} else { // Right
			data.startingLocation(21.58, 0);
		}		
		data.send();

		autonomous = matchConfig.autonomousDecisionTree();
	}

	public void autonomousPeriodic() {
//		autonomous.run();
		testMotorControl.periodic();
	}

}
