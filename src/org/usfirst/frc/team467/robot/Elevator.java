package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.AnalogInput;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator {
	private static Elevator instance;
	private static final Logger LOGGER = Logger.getLogger(Elevator.class);
	
	private AnalogInput heightSensor;
	private WPI_TalonSRX heightController;
	private double feetPerTick;
	private double previousHeight;
	
	public enum Stops {
		min(RobotMap.ELEVATOR_MIN_HEIGHT_IN_FEET),
		fieldSwitch(2),
		lowScale(6),
		highScale(8);
	
		public final double height;
		
		Stops(double height) {
			this.height = height;
		}
	}

	private Elevator() {
		heightSensor = new AnalogInput(RobotMap.ELEVATOR_HEIGHT_SENSOR_ID);
		heightController = new WPI_TalonSRX(RobotMap.TALON_HEIGHT_CONTROLLER_ID);
		feetPerTick = (RobotMap.GEAR_CIRCUMFERENCE_IN_INCHES / 12) / RobotMap.TICKS_PER_TURN;
		previousHeight = getHeight();
	}
	
	/**
	 * The lowest value is 196.0, the maximum value is 3741.0. The middle is 1968.5
	 * New max: 2980, new min:956.5
	 * 16.9 ticks = 1 inch
	 * 1 rotation=253 ticks
	 * 
	 * @return a single instance of the ElevatorSensor object.
	 */
	public static Elevator getInstance() {
		if (instance == null) {
			instance = new Elevator();
		}
		return instance;
	}

	/**
	 * Moves based on the Xbox controller analog input
	 * 
	 * @param speed The velocity. Shall be a value between -1 and 1.
	 */
	public void manualMove(double speed) {
		if (isOutOfRange()) {
			heightController.set(0);
			DriverStation.getInstance().setDriverRumble(0.5);
			return; // Don't bother with any more logic here.
		}
		
		double currentHeight = getHeight();
		for (Stops stop : Stops.values()) {
			if ((previousHeight < stop.height && currentHeight >= stop.height)
				|| (previousHeight > stop.height && currentHeight <= stop.height)) {
				
				DriverStation.getInstance().setDriverRumble(0.5);
			} else {
				DriverStation.getInstance().setDriverRumble(0.0);
			}
		}
		LOGGER.debug("Current Height: " + currentHeight);
		heightController.set(speed);
		previousHeight = currentHeight;
		LOGGER.debug("Previous Height: " + previousHeight);
	}

	public boolean isOutOfRange() {
		return (getHeight()  > RobotMap.ELEVATOR_MAX_HEIGHT_IN_FEET || getHeight() < RobotMap.ELEVATOR_MIN_HEIGHT_IN_FEET);
	}

	public double getHeight() {
		double height = (heightSensor.getValue() - RobotMap.ELEVATOR_INITIAL_TICKS) * feetPerTick;
		LOGGER.debug("Height in feet: " + height);
		return height;
	}
	
}