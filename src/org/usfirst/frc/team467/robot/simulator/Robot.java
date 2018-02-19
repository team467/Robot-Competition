/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Logging;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.MatchConfiguration;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;
import org.usfirst.frc.team467.robot.simulator.gui.MapController;

/**
 * The simulated robot
 */
public class Robot {
	
	private static final Logger LOGGER = Logger.getLogger(Robot.class);
	
	DriveSimulator drive;
	
	MapController simulatorView;
	
	RobotData data;
	
	ActionGroup autonomous;
	
	private MatchConfiguration matchConfig;
	
	public void robotInit() {
		
		Logging.init();
		
		RobotMap.init(RobotID.PreseasonBot);
		
		RobotMap.useSimulator = true;
		drive = DriveSimulator.getInstance();
		matchConfig = MatchConfiguration.getInstance();
		
		data = RobotData.getInstance();
		data.startServer();
		
		LOGGER.info("Started the robot simulator");
		
	}
	
	public void setView(MapController simulatorView) {
		this.simulatorView = simulatorView;
	}
		
	/*
	 * Referring to field map, mode codes represented: 
	 * Moves 1-6: Robot Starting Position - Switch Side
	 * Moves 7-12: Robot Starting Position - Scale Side
	 * Move 1: 1-A (2.5, 0)
	 * Move 2: 1-B (2.5, 0)
	 * Move 3: 2-A (12.5 , 0)
	 * Move 4: 2-B
	 * Move 5: 3-A
	 * Move 6: 3-B
	 * Move 7: 1-C
	 * Move 8: 1-D
	 * Move 9: 2-C
	 * Move 10: 2-D
	 * Move 11: 3-C
	 * Move 12: 3-D
	 */
	// (21.5, 0) Right StartingPosition.
	public void autonomousInit() {
		drive.zero();
		matchConfig.load();
		data.startingLocation(2.5, 0);
		data.send();
		autonomous = matchConfig.autonomousDecisionTree();
	}
	
	public void autonomousPeriodic() {
		autonomous.run();
	}
			
	public static void main(String[] args) {		
		Robot robot = new Robot();
		robot.robotInit();
		robot.autonomousInit();
		while(true) robot.autonomousPeriodic();
	}

}
