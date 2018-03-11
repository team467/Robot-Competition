package org.usfirst.frc.team467.robot.Autonomous;

/**
 * A collection of methods that a drive class must implement to run our autonomous modes
 */
public interface AutoDrive {

	void moveLinearFeet(double distance);
	void rotateByAngle(double rotationInDegrees);

	/**
	 * Move each side independently. Distances must be equal or opposite.
	 */
	void moveFeet(double leftDistance, double rightDistance);

	boolean isStopped();

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	double absoluteDistanceMoved();

	/**
	 * Resets the current sensor position to zero.
	 */
	void zero();
}