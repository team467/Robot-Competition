package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;
/**
 * TiltMonitor monitors the tilting of the robot using the 
 * gyrometer and will retract the elevator if the robot tips too much.
*/
public class TiltMonitor {
	private static TiltMonitor instance;
	private static final Logger LOGGER = Logger.getLogger(TiltMonitor.class);
	
	public static TiltMonitor getInstance() {
		if (instance == null) {
			instance = new TiltMonitor();
		}
		return instance;
	}
	public void periodic() {
		if (Gyrometer.getInstance().getPitchDegrees() > RobotMap.FORWARD_PANIC_ANGLE || 
			Gyrometer.getInstance().getPitchDegrees() < RobotMap.BACKWARD_PANIC_ANGLE) {
			LOGGER.debug("Pitch=" + Gyrometer.getInstance().getPitchDegrees());
			Elevator.getInstance().moveToHeight(Elevator.Stops.floor);
		}
	}
}