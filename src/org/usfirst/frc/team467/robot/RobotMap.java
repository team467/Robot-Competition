package org.usfirst.frc.team467.robot;

/**
 *
 */
public class RobotMap {
	public enum RobotID {
		// TODO: Enumerate robot names
		SOME_NAME;
	};

	// Steering motor ids
	// TODO: Enumerate steering motor IDS

	// Initialize robot map. 
	public static void init(RobotID id) {

		// TODO: Initialize robot map based on robot ID; throw an error on a bad robot id


	}

	// Global robot constants

	public static RobotID robotID;

	public static boolean useSpeedControllers;

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

}