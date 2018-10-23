/**
 * 
 */
package frc.robot.simulator;

import java.text.DecimalFormat;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.RobotMap;
import frc.robot.Autonomous.AutoDrive;
import frc.robot.RobotMap.RobotID;
import frc.robot.simulator.communications.RobotData;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Simulates the motors driving. Will be replaced by a simulated motor eventually.
 */
public class DriveSimulator implements AutoDrive {

	public static final double MAX_RPM = 821;

	private static DriveSimulator instance = null;

	private double maxFeetPerPeriod; // Period is 20 ms

	RobotData data = RobotData.getInstance();

	Logger LOGGER = LogManager.getLogger(DriveSimulator.class);

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

	@Override
	public void zero() {
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

	@Override
	public void moveLinearFeet(double distance) {
		moveFeet(distance, distance);
	}

	@Override
	public void moveFeet(double leftDistance, double rightDistance) {

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

		LOGGER.debug("Left Target: {} Right Target: {}", df.format(leftDistance), df.format(rightDistance));
		LOGGER.debug("Left Move: {}", df.format(leftPositionReading) 
				+ " Right Move: {}", df.format(rightPositionReading));

		data.updateDrivePosition(rightPosition(), leftPosition());

	}

	@Override
	public boolean isStopped() {
		return !isMoving;
	}

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

	@Override
	public void rotateByAngle(double rotation) {
		double turnDistanceInFeet = degreesToFeet(rotation);
		moveFeet(turnDistanceInFeet, -turnDistanceInFeet);
	}

	/**
	 * Convert angle in degrees to wheel distance in feet (arc length).
	 */
	public static double degreesToFeet(double degrees) {

		// Convert the turn to a distance based on the circumference of the robot wheel base.
		double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
		double angleInRadians = Math.toRadians(degrees);
		double distanceInFeet = radius * angleInRadians; // This is the distance we want to turn.

		return distanceInFeet;
	}
}
