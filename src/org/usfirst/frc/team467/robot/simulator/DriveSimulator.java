/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.Autonomous.AutoDrive;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Simulates the motors driving. Will be replaced by a simulated motor eventually.
 */
public class DriveSimulator implements AutoDrive {

	public static final double MAX_RPM = 821;

	private static DriveSimulator instance = null;

	private double maxFeetPerPeriod; // Period is 20 ms

	RobotData data = RobotData.getInstance();

	Logger LOGGER = Logger.getLogger(DriveSimulator.class);

	private DecimalFormat df = new DecimalFormat("####0.00");

	private double rightPositionReading;
	private double leftPositionReading;

	private boolean isMoving = false;

	private DriveSimulator() {
		maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * MAX_RPM / 60 / 5000; // actually 60/500
		zero();
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
	@Override
	public void zero() {
		rightPositionReading = 0;
		leftPositionReading = 0;
		isMoving = false;
		data.zero();
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#rightPosition()
	 */
	public double rightPosition() {
		return rightPositionReading;//absoluteRightPositionReadingOffset + rightPositionReading;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#leftPosition()
	 */
	public double leftPosition() {
		return leftPositionReading;//absoluteLeftPositionReadingOffset + leftPositionReading;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#setMaxMotionMagicSpeed(double)
	 */
	public void setMaxMotionMagicSpeed(double percentOfMaxSpeed) {
		if (percentOfMaxSpeed < 0) {
			percentOfMaxSpeed = 0;
		} else if (percentOfMaxSpeed > 1) {
			percentOfMaxSpeed = 1;
		}
		maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * percentOfMaxSpeed * MAX_RPM / 60 / 1000;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#moveFeet(double)
	 */
	@Override
	public void moveFeet(double distance) {
		moveFeet(distance, 0, null);
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#moveFeet(double, double)
	 */
	@Override
	public void moveFeet(double distance, double rotation, ControlMode mode) {

		double rotationInRadians = Math.toRadians(rotation);
		double leftDistance = distance + rotationInRadians * (RobotMap.WHEEL_BASE_WIDTH / 2);
		double rightDistance = distance - rotationInRadians * (RobotMap.WHEEL_BASE_WIDTH / 2);

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

		data.updateDrivePosition(rightPosition(), leftPosition());

	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#isStopped()
	 */
	@Override
	public boolean isStopped() {
		return !isMoving;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#absoluteDistanceMoved()
	 */
	@Override
	public double absoluteDistanceMoved() {
		double absoluteLeftDistance =  Math.abs(leftPositionReading);
		double absoluteRightDistance = Math.abs(rightPositionReading);
		if (absoluteLeftDistance < absoluteRightDistance) {
			return absoluteRightDistance;
		} else {
			return absoluteLeftDistance;
		}
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#rotateByAngle(double)
	 */
	@Override
	public void rotateByAngle(double rotation) {
		moveFeet(0, rotation, null);

	}

	public static void main(String[] args) {
		RobotMap.init(RobotID.Competition_1);
		DriveSimulator drive = DriveSimulator.getInstance();
		drive.zero();
		RobotData.getInstance().startingLocation(20, 0);

		do {
			drive.rotateByAngle(360+90);
		} while (!drive.isStopped());	

	}

}
