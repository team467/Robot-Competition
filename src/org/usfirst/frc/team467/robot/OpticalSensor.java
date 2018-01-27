package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.DigitalInput;

public class OpticalSensor {
private final static int OPTICAL_CHANNEL = 3; //-> place-holder
private DigitalInput di;
private static OpticalSensor instance;
public boolean gotCube = false;
	
	public OpticalSensor() {
		di = new DigitalInput(OPTICAL_CHANNEL);
	}
	
	public static OpticalSensor getInstance() {
		if(instance == null) {
			instance = new OpticalSensor();
		}
		
		return instance;
	}
	
	public boolean hasCube() {
		if(di.get()) {
			gotCube = true;
		}
		else {
			gotCube = false;
		}
		
		return gotCube;
	}
	
}
