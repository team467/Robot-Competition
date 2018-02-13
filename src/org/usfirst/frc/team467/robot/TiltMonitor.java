package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;
/*Class description: Tilt monitor monitors the tilting of the robot using the 
 * gyrometer and will retract the elevator at a certain point
*/
public class TiltMonitor {
	private static TiltMonitor instance;
	private static final Logger LOGGER = Logger.getLogger(TiltMonitor.class);
	
	public static final int FORWARD_PANIC_ANGLE = 45;
	public static final int BACKWARD_PANIC_ANGLE = -45;
	
	public static TiltMonitor getInstance() {
		if (instance == null) {
			instance = new TiltMonitor();
		}
		return instance;
	}
	public void periodic() {
		if (Gyrometer.getInstance().getPitchDegrees() > FORWARD_PANIC_ANGLE || 
			Gyrometer.getInstance().getPitchDegrees() < BACKWARD_PANIC_ANGLE) {
			LOGGER.debug("Pitch=" + Gyrometer.getInstance().getPitchDegrees());
			Elevator.getInstance().moveToHeight(Elevator.Stops.floor);
		}
	}
}