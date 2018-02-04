/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;
import org.usfirst.frc.team467.robot.simulator.gui.MapController;

/**
 *
 */
public class Robot {
	
	public static final double WIDTH = 2.92;
	
	public static final double LENGTH = 3.33;
	
	Drive drive;
	
	MapController simulatorView;
	
	RobotData data;
	
	public void robotInit() {
		drive = DriveSimulator.getInstance();
		
		data = RobotData.getInstance();
		data.startServer();
	}
	
	public void setView(MapController simulatorView) {
		this.simulatorView = simulatorView;
	}
	
	int moveCount = 0;
	
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
		drive.zeroPosition();
		data.startPosition(20, 1.5);
		data.send();
		moveCount = 0;
		mode = AutonomousModes.move1;
	}
	
	public void autonomousPeriodic() {
		switch (mode) {
		
		case move1:
			move1();
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
	}
	
	private enum AutonomousModes {
		move1,
		move2;
	}
	
	private void move1() {
		switch(moveCount) {
		
		case 0:
			if (drive.moveDistance(20, 20)) {
				moveCount++;
				drive.zeroPosition();				
			}
			break;
		
		case 1:
			if (drive.moveDistance(-0.785, 0.785)) {
				//45º turn in place 
				moveCount++;
				drive.zeroPosition();				
			}
			break;
		
		case 2:
			if (drive.moveDistance(5, 5)) {
				moveCount++;
				drive.zeroPosition();
			}
			break;

		case 3:
			if (drive.moveDistance(1.57, -1.57)) {
				moveCount++;
				drive.zeroPosition();
			}
			break;

		case 4:
			if (drive.moveDistance(5, 5)) {
				moveCount++;
				drive.zeroPosition();
			}
			break;

		default:
		}

		
	}
	
	public static void main(String[] args) {		
		Robot robot = new Robot();
		robot.robotInit();
		robot.autonomousInit();
		while(true) robot.autonomousPeriodic();
	}

}
