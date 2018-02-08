/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import java.text.DecimalFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

/**
 *
 */
public class DriveSimulator {
	
	public static final double MAX_RPM = 821;
		
	private static DriveSimulator instance = null;
	
	private double maxFeetPerPeriod; // Period is 20 ms
	
	RobotData data = RobotData.getInstance();
	
	Logger LOGGER = Logger.getLogger(DriveSimulator.class);
	
	private DecimalFormat df = new DecimalFormat("####0.00");
	
	private double rightPositionReading;
	private double leftPositionReading;
	
	private double absoluteRightPositionReadingOffset;
	private double absoluteLeftPositionReadingOffset;
	
	private boolean isMoving = false;
	
	private DriveSimulator() {
		maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * MAX_RPM / 60 / 10000; // actually 60/500
		zero();
		absoluteRightPositionReadingOffset = 0.0;
		absoluteLeftPositionReadingOffset = 0.0;
		LOGGER.setLevel(Level.INFO);
	}
	
	public static DriveSimulator getInstance() {
		if (instance == null) {
			instance = new DriveSimulator();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#zeroPosition()
	 */
	public void zero() {
		absoluteRightPositionReadingOffset = rightPositionReading;
		absoluteLeftPositionReadingOffset = leftPositionReading;
		rightPositionReading = 0;
		leftPositionReading = 0;
		isMoving = false;
		data.zero();
	}
	
	public double rightPosition() {
		return rightPositionReading;//absoluteRightPositionReadingOffset + rightPositionReading;
	}
	
	public double leftPosition() {
		return leftPositionReading;//absoluteLeftPositionReadingOffset + leftPositionReading;
	}
	
	public void setMaxMotionMagicSpeed(double percentOfMaxSpeed) {
		if (percentOfMaxSpeed < 0) {
			percentOfMaxSpeed = 0;
		} else if (percentOfMaxSpeed > 1) {
			percentOfMaxSpeed = 1;
		}
		maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * percentOfMaxSpeed * MAX_RPM / 60 / 1000;
	}
	
	
	public void move(double distance) {
		moveDistance(distance, 0);
	}
		
	public void moveDistance(double distance, double rotation) {

	    double leftDistance = distance + rotation * (RobotMap.WHEEL_BASE_WIDTH / 2);
	    double rightDistance = distance - rotation * (RobotMap.WHEEL_BASE_WIDTH / 2);
	    
		if (leftPositionReading == leftDistance && rightPositionReading == rightDistance) {
			isMoving = false;
			return; // At destination
		}
		
		isMoving = true;

		if (Math.abs((leftDistance - leftPositionReading)) > maxFeetPerPeriod) {
			if (leftDistance < 0) {
				leftPositionReading -= maxFeetPerPeriod;
			} else {
				leftPositionReading += maxFeetPerPeriod;
			}
		} else {
			leftPositionReading = leftDistance;
		}
		
		if (Math.abs((rightDistance - rightPositionReading)) > maxFeetPerPeriod) {
			if (rightDistance < 0) {
				rightPositionReading -= maxFeetPerPeriod;
			} else {
				rightPositionReading += maxFeetPerPeriod;
			}
		} else {
			rightPositionReading = rightDistance;
		}
		
		LOGGER.debug("Left Target: " + df.format(leftDistance) + " Right Target: " + df.format(rightDistance));
		LOGGER.debug("Left Move: " + df.format(leftPositionReading) 
			+ " Right Move: " + df.format(rightPositionReading));
		
		data.update(rightPosition(), leftPosition());
		
	}

	public boolean isStopped() {
		return !isMoving;
	}

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double absoluteDistanceMoved() {
		double absoluteLeftDistance =  Math.abs(leftPositionReading);
		double absoluteRightDistance = Math.abs(rightPositionReading);
		if (absoluteLeftDistance < absoluteRightDistance) {
			return absoluteRightDistance;
		} else {
			return absoluteLeftDistance;
		}
	}
	
		public void turn(double rotation) {
			moveDistance(0, Math.toRadians(rotation));
			
		}


	public static void main(String[] args) {
		RobotMap.init(RobotID.PreseasonBot);
		DriveSimulator drive = DriveSimulator.getInstance();
		drive.zero();
		RobotData.getInstance().startPosition(20, 0);
		
//		do {
//			drive.moveDistance(4, 0);
//		} while (!drive.isStopped());	
		
		do {
			drive.turn(360+90);
		} while (!drive.isStopped());	
		
	}

}
