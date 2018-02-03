package org.usfirst.frc.team467.robot.simulator;

public interface Drive {

	void zeroPosition();

	boolean moveDistance(double left, double right);

}