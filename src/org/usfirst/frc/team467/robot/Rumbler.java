package org.usfirst.frc.team467.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rumbler {
	private static final Logger LOGGER = LogManager.getLogger(Rumbler.class);
	static final int ITERATION_TIME_MS = 20;
	private final XBoxJoystick467 controller;
	int durationMS;
	double intensity;

	Rumbler(XBoxJoystick467 controller) {
		this.controller = controller;
	}

	/**
	 * @param durationMS Rumble duration in milliseconds
	 * @param intensity Squared input
	 */
	void rumble(int durationMS, double intensity) {
		this.durationMS = durationMS;
		this.intensity = intensity*intensity;
		LOGGER.debug("rumble duration= {} rumble intensity= {}", durationMS, intensity);
	}

	public void periodic() {
		if(durationMS > 0){
			controller.setRumble(intensity);
			durationMS -= ITERATION_TIME_MS;
			LOGGER.debug("periodic duration= {} intensity= {}", durationMS, intensity);  
		} else { 
			controller.setRumble(0);
			intensity = 0;
		}
	}
}