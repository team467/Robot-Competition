package org.eigusfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.AnalogInput;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class ElevatorSensor {
	private static ElevatorSensor instance = null;
	private static final Logger LOGGER = Logger.getLogger(ElevatorSensor.class);

	private static final double TICKS_PER_TURN = 253.0;
	
	private static final double GEAR_CIRCUMFERENCE_IN_INCHES = 10;
			
	public static final double ELEVATOR_MAX_HEIGHT_IN_FEET = 10;
	
	public static final double ELEVATOR_MIN_HEIGHT_IN_FEET = 0;

	public static final double ELEVATOR_INITIAL_TICKS = 196;
	
	public static final int ELEVATOR_HEIGHT_SENSOR_ID = 0;
	
	public static final int TALON_HEIGHT_CONTROLLER_ID = 1;
	
	
	public enum Stops {
		min(ELEVATOR_MIN_HEIGHT_IN_FEET),
		fieldSwitch(2),
		lowScale(6),
		highScale(8);

		public final double height;
		
		Stops(double height) {
			this.height = height;
		}
	}
	
	private AnalogInput heightSensor;
	
	private WPI_TalonSRX heightController;

	private double feetPerTick;
	
	private double previousNumber;
	
	private ElevatorSensor() {
		heightSensor = new AnalogInput(ELEVATOR_HEIGHT_SENSOR_ID);
		heightController = new WPI_TalonSRX(TALON_HEIGHT_CONTROLLER_ID);
		feetPerTick = GEAR_CIRCUMFERENCE_IN_INCHES / TICKS_PER_TURN / 12;
	}
	
	/**
	 * Returns a single instance of the ElevatorSensor object.
	 */
	public static ElevatorSensor getInstance() {
		if (instance == null) {
			instance = new ElevatorSensor();
		}
		return instance;
		//The lowest value is 196.0, the maximum value is 3741.0. The middle is 1968.5
		//New max: 2980, new min:956.5
		//16.9 ticks = 1 inch
		//1 rotation=253 ticks
	}

	/**
	 * Moves based on the Xbox controller analog input
	 * 
	 * @param speed the speed and direction. Shall be a value between -1 and 1.
	 */
	public void manualMove(double speed) {
		if (isOutOfRange()) {
			speed = 0;
			// TODO: Rumble here
		}
		double example = Stops.fieldSwitch.height;
		for (Stops stop : Stops.values()) {
			System.out.println(stop.height);
		}
		heightController.set(speed);
	}

	public boolean isOutOfRange() {
		double height = (heightSensor.getAverageValue() - ELEVATOR_INITIAL_TICKS) * feetPerTick;
		
		if (getHeight()  > ELEVATOR_MAX_HEIGHT_IN_FEET || getHeight() < ELEVATOR_MIN_HEIGHT_IN_FEET) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean garbageCan() {
		if()		
	}
	
	public double getHeight() {
		double height = (heightSensor.getValue() - ELEVATOR_INITIAL_TICKS) * feetPerTick;
		LOGGER.info("Height in feet: " + height);
		return height;
	}
}
	

//TalonSRX ID is 1