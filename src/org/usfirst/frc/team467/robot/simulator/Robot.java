/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

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
		drive.zeroPosition();
		data.startPosition(5.5, 1.26);
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
	
	//0.785
	
	private void move1() {
		switch(moveCount) {
		
		case 0:
			if (drive.moveDistance(12.33, 12.33)) {
				moveCount++;
				drive.zeroPosition();				
			}
			break;
			
		case 1:
			if (drive.moveDistance(1.57, -1.57)) {
				moveCount++;
				drive.zeroPosition();				
			}
			break;
		
		case 2:
			if (drive.moveDistance(1.48, 1.48)) {
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
