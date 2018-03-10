package org.usfirst.frc.team467.robot.Autonomous;

public interface AutoDrive {

	void moveFeet(double distance);
	void rotateByAngle(double rotationInDegrees);

	void moveFeet(double leftDistance, double rightDistance);

	boolean isStopped();

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	double absoluteDistanceMoved();

	void zero();
}