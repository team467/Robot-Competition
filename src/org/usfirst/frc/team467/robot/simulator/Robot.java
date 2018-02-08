/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.Logging;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;
import org.usfirst.frc.team467.robot.simulator.gui.MapController;

/**
 *
 */
public class Robot {
	
	private static final Logger LOGGER = Logger.getLogger(Robot.class);
	
	DriveSimulator drive;
	
	MapController simulatorView;
	
	RobotData data;
	
	ActionGroup autonomous;
	
	public void robotInit() {
		
		Logging.init();
		
		RobotMap.init(RobotID.PreseasonBot);
		
		RobotMap.useSimulator = true;
		drive = DriveSimulator.getInstance(); 
		
		data = RobotData.getInstance();
		data.startServer();
		
	}
	
	public void setView(MapController simulatorView) {
		this.simulatorView = simulatorView;
	}
		
	/*
	 * Referring to field map, mode codes represented: 
	 * Modes 1-6: Robot Starting Position - Switch Side
	 * Modes 7-12: Robot Starting Position - Scale Side
	 * Mode 1: 1-A
	 * Mode 2: 1-B
	 * Mode 3: 2-A
	 * Mode 4: 2-B
	 * Mode 5: 3-A
	 * Mode 6: 3-B
	 * Mode 7: 1-C
	 * Mode 8: 1-D
	 * Mode 9: 2-C
	 * Mode 10: 2-D
	 * Mode 11: 3-C
	 * Mode 12: 3-D
	 */
	
	AutonomousModes mode;
	
	public void autonomousInit() {
		drive.zero();
		data.startPosition(4, 0);
		data.send();
		mode = AutonomousModes.move1;
		switch (mode) {
		
		case move1:
			autonomous = Actions.powerUp1A();
			break;
			
		/*case move2:
			move2();
			break;
			
		case move3:
			move3();
			break;
			
		
		*/
			
		default:
		
		}
		autonomous.enable();
	}
	
	public void autonomousPeriodic() {
		autonomous.run();
	}
	
	private enum AutonomousModes {
		move1,
		move2;
	}
		
	public static void main(String[] args) {		
		Robot robot = new Robot();
		robot.robotInit();
		robot.autonomousInit();
		while(true) robot.autonomousPeriodic();
	}

}
