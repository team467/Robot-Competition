package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;

/**
 *
 */
public class RobotMap {
	public static final int TALON_TIMEOUT = 10; // 10 ms is the recommended timeout
	public enum RobotID {
		// TODO: Enumerate robot names
		YES_467, NO_467
	};
	public static final int FRONT_LEFT = 0;
	public static final int FRONT_RIGHT = 1;
	public static final int BACK_LEFT = 2;
	public static final int BACK_RIGHT = 3;
	
	public static final Logger LOGGER = Logger.getLogger(ElevatorSensor.class);

	public static final double TICKS_PER_TURN = 253.0;
	
	public static final double GEAR_CIRCUMFERENCE_IN_INCHES = 10;
	
	public static final double ELEVATOR_MAX_HEIGHT_IN_FEET = 10;
	
	public static final double ELEVATOR_MIN_HEIGHT_IN_FEET = 0;

	public static final double ELEVATOR_INITIAL_TICKS = 196;
	
	public static final int ELEVATOR_HEIGHT_SENSOR_ID = 0;
	
	public static final int TALON_HEIGHT_CONTROLLER_ID = 1;
	
		
		
	// Steering motor ids
	// TODO: Enumerate steering motor IDS

	// Initialize robot map. 
	public static void init(RobotID id) {
		switch (id) {
		case YES_467:
			isDriveMotorInverted = new boolean[] { false, true, false, true };
		case NO_467:
			isDriveMotorInverted = new boolean[] { false, true, false, true };
			
		}
		
	
		
	

		// TODO: Initialize robot map based on robot ID; throw an error on a bad robot id


	}

	// Global robot constants
	public static RobotID robotID;

	public static boolean[] isDriveMotorInverted;
	public static boolean useSpeedControllers;
	public static final int VELOCITY_PID_PROFILE = 0;
	public static final int POSITION_PID_PROFILE = 1;
	public static final double POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEELPOD_CIRCUMFERENCE); // 1/2 inch
	public static final int VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR = 50; 	// This is in encoder ticks
	public static final int POSITION_ALLOWABLE_CLOSED_LOOP_ERROR = (int) (POSITION_ALLOWED_ERROR * 1024 * 0.95); 	// This is in encoder ticks

	public static final double FAST_MAX_SPEED = 1.0;
	public static final double NORMAL_MAX_SPEED = 0.6;
	public static final double SLOW_MAX_SPEED = 0.35;

	// The maximum revolutions per minute (RPM) of a wheel when in speed control mode.
	public static double MAX_SPEED;

	public static final double MIN_DRIVE_SPEED = 0.1;
	// Robot Dimensions
	public static double WHEEL_BASE_LENGTH;
	public static double WHEEL_BASE_WIDTH;
	public static double CamToCenterWidthInches;
	public static double CamToCenterLengthInches;	

	/**
	 * Used to ensure that all Talon SRX outputs are relative to a fixed value.
	 * If the available voltage is below the nominal and a value about that is
	 * requested, the output will be 100%.
	 */
	public static final double NOMINAL_BATTERY_VOLTAGE = 12.0;

	// The circumference of the wheels for use in determining distance in
	// position mode
	public static final double WHEELPOD_CIRCUMFERENCE = 19.74; //20.5139; //21.43

	// The number of encoder ticks per one revolution of the wheel. This is used
	// for correctly determining RPM and position.
	public static final int WHEELPOD_ENCODER_CODES_PER_REVOLUTION = 256;

	// Set to true to use LSM9DS1 IMU on Raspberry Pi
	// Set to false to use the local ADIS16448 IMU on the Robo Rio
	public static final boolean useRemoteImu = false;

	// Game Pieces
	// TODO: Game pieces motor channels
	
	public static final double TICKS_PER_TURN = 253.0;
	public static final double GEAR_CIRCUMFERENCE_IN_INCHES = 10;	
	public static final double ELEVATOR_MAX_HEIGHT_IN_FEET = 10;
	public static final double ELEVATOR_MIN_HEIGHT_IN_FEET = 0;
	public static final double ELEVATOR_INITIAL_TICKS = 196;
	public static final int ELEVATOR_HEIGHT_SENSOR_ID = 0;
	public static final int TALON_HEIGHT_CONTROLLER_ID = 1;
}