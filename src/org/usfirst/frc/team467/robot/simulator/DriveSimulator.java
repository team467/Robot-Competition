/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import java.text.DecimalFormat;

import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

/**
 *
 */
public class DriveSimulator implements Drive {
	
	public static final double MAX_RPM = 821;
	
	public static final double WHEEL_CIRCUMFERENCE = 18.7;
	
	private static Drive instance = null;
	
	private double maxFeetPerPeriod; // Period is 20 ms
	
	RobotData data = RobotData.getInstance();
	
	private DecimalFormat df = new DecimalFormat("####0.00");
	
	private double rightPositionReading;
	private double leftPositionReading;

	private DriveSimulator() {
		maxFeetPerPeriod = WHEEL_CIRCUMFERENCE / 12 * MAX_RPM / 60 / 1000;
		zeroPosition();
	}
	
	public static Drive getInstance() {
		if (instance == null) {
			instance = new DriveSimulator();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#zeroPosition()
	 */
	@Override
	public void zeroPosition() {
		rightPositionReading = 0;
		leftPositionReading = 0;
		data.zeroPosition();
	}
	
	public double rightPosition() {
		return rightPositionReading;
	}
	
	public void setMaxMotionMagicSpeed(double percentOfMaxSpeed) {
		if (percentOfMaxSpeed < 0) {
			percentOfMaxSpeed = 0;
		} else if (percentOfMaxSpeed > 1) {
			percentOfMaxSpeed = 1;
		}
		maxFeetPerPeriod = WHEEL_CIRCUMFERENCE / 12 * percentOfMaxSpeed * MAX_RPM / 60 / 1000;
	}
	
	public double leftPosition() {
		return leftPositionReading;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#moveDistance(double, double)
	 */
	@Override
	public boolean moveDistance(double left, double right) {

		if (leftPositionReading == left && rightPositionReading == right) {
			return true; // At destination
		}
		
		if (Math.abs((left - leftPositionReading)) > maxFeetPerPeriod) {
			if (left < 0) {
				leftPositionReading -= maxFeetPerPeriod;
			} else {
				leftPositionReading += maxFeetPerPeriod;
			}
		} else {
			leftPositionReading = left;
		}
		
		if (Math.abs((right - rightPositionReading)) > maxFeetPerPeriod) {
			if (right < 0) {
				rightPositionReading -= maxFeetPerPeriod;
			} else {
				rightPositionReading += maxFeetPerPeriod;
			}
		} else {
			rightPositionReading = right;
		}
		
		System.out.println("Left Target: " + df.format(left) + " Right Target: " + df.format(right));
		System.out.println("Left Move: " + df.format(leftPositionReading) 
			+ " Right Move: " + df.format(rightPositionReading));
		
		data.update(rightPositionReading, leftPositionReading);
		
		return false;
	}
	
	public static void main(String[] args) {		
		Drive drive = DriveSimulator.getInstance();
		drive.zeroPosition();
		RobotData.getInstance().startPosition(20, 1.5);
		
		double left = -1 * Math.toRadians(100);
		double right =     Math.toRadians(100);
		while (drive.moveDistance(left, right) != true);
		
//		drive.moveDistance(0, 0); // Stationary
//		drive.moveDistance(10, 10); // Straight forward
//		drive.moveDistance(-3.14, 3.14); // 180 degrees
//		drive.moveDistance(5.7, 35.7);
//		drive.moveDistance(15.7, -15.7);
//		drive.moveDistance(31.4, -31.4);
//		drive.moveDistance(131.4, 68.6);
	}


}
