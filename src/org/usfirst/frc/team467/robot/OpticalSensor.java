package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.DigitalInput;

public class OpticalSensor {
private final static int OPTICAL_CHANNEL = 5;
public DigitalInput di;
private static OpticalSensor instance;
	
	private OpticalSensor() {
		di = new DigitalInput(OPTICAL_CHANNEL);
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
