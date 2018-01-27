package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.AnalogInput;

public class ElevatorHeightSensor {
	
	AnalogInput sensor;
	static ElevatorHeightSensor instance;
	public double rotations;
	public double height;
	public double inchConvert = 16.9;  // number of ticks per inch
	public double feetConvert = 202.8; //number of ticks per foot
	public double rotationConvert = 253; //number of ticks per rotation
	
	//16.9 ticks = 1 inch
	// x 1 rotation = 1 foot (loosely approximate)
	//203.4 ticks = 1 foot
	//1 rotation = 253 ticks
	
	//channel = 0;
	
	private ElevatorHeightSensor() {
	AnalogInput sensor = new AnalogInput(0);
	}
	
	
	public double getRotations() {
		rotations = sensor.getValue()/rotationConvert;
		return rotations;
	}
	
	public static ElevatorHeightSensor getInstance() {
		if(instance == null) {
			instance = new ElevatorHeightSensor();
		}
		
		return instance;
	}
	
	public boolean atMaxHeight() {
		if(height > 8) {
			return true;
		}
		
		else return false;
	}
	
	public double getHeight() {
		height = rotations/feetConvert;
		return height;
	}
	
	public double getInchHeight() {
		return rotations/inchConvert;
	}
	
	
	

}
