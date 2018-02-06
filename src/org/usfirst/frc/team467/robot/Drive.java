package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.simulator.DriveSimulator;

public interface Drive {
	
	public static Drive getInstance() {
		if (RobotMap.useSimulator) {
			return DriveSimulator.getInstance();
		} else {
			return DriveActual.getInstance();
		}
	}

	public void zeroPosition();

	public void moveDistance(double distance);
	
	public void moveDistance(double distance, double rotation);
	
	public void arcadeDrive(double speed, double rotation, boolean squaredInputs);
	
	public void tankDrive(double left, double right);
	
	public void tankDrive(double left, double right, boolean squaredInputs);
	
	public boolean isStopped();
	
	public double absoluteDistanceMoved();


}