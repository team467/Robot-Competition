package org.usfirst.frc.team467.robot;
import com.sun.media.jfxmedia.logging.Logger;

import edu.wpi.first.wpilibj.DigitalInput;

public class OpticalSensor {
	private static OpticalSensor instance;
	private DigitalInput di;

	private OpticalSensor() {
		System.out.println(RobotMap.OPTICAL_CHANNEL);
		di = new DigitalInput(RobotMap.OPTICAL_CHANNEL);
	}

	public static OpticalSensor getInstance() {
		if (instance == null) {
			instance = new OpticalSensor();
		}

		return instance;
	}

	public boolean detectedTarget() {
//		if (di.get()) {
//			System.out.println("NO CUBE !!!");
//		} else {
//			System.out.println("SEE CUBE !!!!!!");
//		}
		return !di.get();
	}

}
