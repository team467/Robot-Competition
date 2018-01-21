package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.AnalogInput;

public class ElevatorSensor {
	private static ElevatorSensor instance = null;
	private static final Logger LOGGER = Logger.getLogger(ElevatorSensor.class);

	private static final double TICKS_PER_TURN = 253.0;
	
	public static final double ELEVATOR_INITIAL_TICKS = 956.0;
			
	public static final double ELEVATOR_MAX_HEIGHT_IN_INCHES = 120;
	
	public static final double ELEVATOR_MIN_HEIGHT_IN_INCHES = 0;
	
	public static final double ELEVATOR_MAX_TURNS = 8.0;
	
	private AnalogInput heightSensor;

	private ElevatorSensor() {
		heightSensor = new AnalogInput(0);
	}
	
	/**
	 * Returns a single instance of the ElevatorSensor object.
	 */
	public static ElevatorSensor getInstance() {
		if (instance == null) {
			instance = new ElevatorSensor();
		}
		return instance;
	}

	public boolean isOutOfRange() {
		double height = heightSensor.getAverageValue();
		
		if (height/16.9 >= 176.3 || height/16.9 <= 56.6) {
			return true;
		} else {
			return false;
		}
	}
	//Height/16.9 represents the height in inches. 
	
	public double getHeight() {
		double height = heightSensor.getAverageValue();
		LOGGER.info("Height in inches: " + height/16.9);
		return height;
	}
	
}
