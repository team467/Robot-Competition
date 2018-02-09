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
	
	public void robotInit() {
		
		Logging.init();
		
		RobotMap.init(RobotID.PreseasonBot);
		
		RobotMap.useSimulator = true;
		drive = DriveSimulator.getInstance(); 
		
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
	 * Move 1: 1-A
	 * Move 2: 1-B
	 * Move 3: 2-A
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
	
	AutonomousModes mode;
	
	public void autonomousInit() {
		drive.zero();
		data.startPosition(2.5, 0);
		data.send();
		mode = AutonomousModes.move1;
		switch (mode) {
		
		case move1:
			autonomous = Actions.startSwitchSide1A();
			break;
		default:
			break;
			
		/*case move2:
			move2();
			break;
			
		case move3:
			move3();
			break;
			
		case move4:
			move4();
			break;
			
		case move5:
			move5();
			break;
			
		case move6:
			move6();
			break;
			
		case move7:
			move7();
			break;
			
		case move8:
			move8();
			break;
			
		case move9:
			move9();
			break;
			
		case move10:
			move10();
			break;
			
		case move11:
			move11();
			break;
			
		case move12:
			move12();
			break;
			
		default:
		*/
		}
		autonomous.enable();
	}
	
	public void autonomousPeriodic() {
		autonomous.run();
	}
	
	private enum AutonomousModes {
		move1,
		move2,
		move3,
		move4,
		move5,
		move6,
		move7,
		move8,
		move9,
		move10,
		move11,
		move12;
	}
		
	public static void main(String[] args) {		
		Robot robot = new Robot();
		robot.robotInit();
		robot.autonomousInit();
		while(true) robot.autonomousPeriodic();
	}

}
