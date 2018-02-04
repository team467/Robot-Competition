package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class Rumbler {
	private static final Logger LOGGER = Logger.getLogger(Rumbler.class);
	static final int ITERATION_TIME_MS = 20;
    private final XBoxJoystick467 controller;
	int durationMS;
	double intensity;
	
	Rumbler(XBoxJoystick467 controller) {
		this.controller = controller;
	}
		
	void rumble(int durationMS, double intensity) {
		this.durationMS = durationMS;
		this.intensity = intensity; 
		LOGGER.info("rumble duration=" + durationMS + "rumble intensity=" + intensity);
	}
	
	public void periodic() {
		if(durationMS > 0){
		    controller.setRumble(RumbleType.kRightRumble, intensity);
		    controller.setRumble(RumbleType.kLeftRumble, intensity);
		    durationMS -= ITERATION_TIME_MS;
		    LOGGER.info("periodic duration=" + durationMS + " intensity=" + intensity);
		    
        } else { 
		   controller.setRumble(RumbleType.kRightRumble, 0);
		   controller.setRumble(RumbleType.kLeftRumble, 0);

		}
	}
	
}