package org.usfirst.frc.team467.robot.Autonomous;

import com.ctre.phoenix.motorcontrol.ControlMode;

public interface AutoDrive {

	void moveFeet(double distance);

	void moveFeet(double distance, double rotation, ControlMode mode);

	boolean isStopped();

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	double absoluteDistanceMoved();

	void rotateByAngle(double rotation);

	void zero();
}