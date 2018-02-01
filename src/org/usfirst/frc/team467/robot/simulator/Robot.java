/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

/**
 * @author Bryan Duerk
 *
 */
public class Robot {
	
	public static final double WIDTH = 2;
	
	public static final double LENGTH = 3;
	
	Drive drive;
	
	MapController simulatorView;
	
	RobotData data;
	
	public void robotInit() {
		drive = Drive.getInstance();
		
		data = RobotData.getInstance();
		data.startServer();
	}
	
	public void setView(MapController simulatorView) {
		this.simulatorView = simulatorView;
	}
	
	int moveCount = 0;
	
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
			if (drive.moveDistance(5, 5)) {
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
