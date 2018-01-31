package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.AnalogInput;

public class ElevatorHeightSensor {
	
	AnalogInput sensor;
	static ElevatorHeightSensor instance;
	public double rotations;
	public double ticksPerInch = 16.9;  // number of ticks per inch
	public double ticksPerFoot = 202.8; //number of ticks per foot
	public double ticksPerRotation = 253; //number of ticks per rotation
	
	//16.9 ticks = 1 inch
	// x 1 rotation = 1 foot (loosely approximate)
	//203.4 ticks = 1 foot
	//1 rotation = 253 ticks
	
	//channel = 0;
	
	private ElevatorHeightSensor() {
	 sensor = new AnalogInput(0);
	}
	
	public double getRotations() {
		 return sensor.getAverageValue()/ticksPerRotation;
	}
	
	public static ElevatorHeightSensor getInstance() {
		if(instance == null) {
			instance = new ElevatorHeightSensor();
		}
		
		return instance;
	}
	
	public boolean atMaxHeight() {
		if(getHeightFeet() > 8) {
			return true;
		}
		
		else return false;
	}
	
	public double getHeightFeet() {
		return rotations/ticksPerFoot;
	}
	
	public double getHeightInches() {
		return rotations/ticksPerInch;
	}
	
	
	

}
