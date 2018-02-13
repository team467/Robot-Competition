package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

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
		if (Math.abs(Gyrometer.getInstance().getPitchDegrees()) > 45) {
			LOGGER.info("Pitch=" + Gyrometer.getInstance().getPitchDegrees());
			Elevator.getInstance().moveToHeight(Elevator.Stops.floor);

		}
	}
}