package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.DigitalInput;

public class OpticalSensor {
	private static OpticalSensor instance;
	private DigitalInput di;

	private OpticalSensor() {
		di = new DigitalInput(RobotMap.OPTICAL_CHANNEL);
	}

	public static OpticalSensor getInstance() {
		if (instance == null) {
			instance = new OpticalSensor();
		}

		return instance;
	}

	public boolean detectedTarget() {
		return di.get();
	}

}
