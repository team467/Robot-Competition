package org.usfirst.frc.team467.robot;

public class RobotMap {
	public static final int TALON_TIMEOUT = 10; // 10 ms is the recommended timeout

	public enum RobotID {
		PreseasonBot, Board, Competition_1, Competition_2
	};

	public static final int FRONT_LEFT = 0;
	public static final int FRONT_RIGHT = 1;
	public static final int BACK_LEFT = 2;
	public static final int BACK_RIGHT = 3;

	public static boolean HAS_WHEELS;
	public static int LEFT_LEAD_CHANNEL;
	public static int LEFT_FOLLOWER_1_CHANNEL;
	public static int LEFT_FOLLOWER_2_CHANNEL;

	public static int RIGHT_LEAD_CHANNEL;
	public static int RIGHT_FOLLOWER_1_CHANNEL;
	public static int RIGHT_FOLLOWER_2_CHANNEL;

	// Initialize robot map. 
	public static void init(RobotID id) {
		robotID = id;
		switch (id) {
		case PreseasonBot:
			HAS_WHEELS = true;
			WHEEL_CIRCUMFERENCE = 19.74;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 256;
			useSpeedControllers = true;
			POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEEL_CIRCUMFERENCE); // 1/2 inch

			LEFT_LEAD_CHANNEL = 1;
			LEFT_FOLLOWER_1_CHANNEL = 2;
			LEFT_FOLLOWER_2_CHANNEL = 3;

			RIGHT_LEAD_CHANNEL = 4;
			RIGHT_FOLLOWER_1_CHANNEL = 5;
			RIGHT_FOLLOWER_2_CHANNEL = 6;

			HAS_ELEVATOR = false;
			HAS_GRABBER = false;
			HAS_RAMPS = false;

			// TODO Assign values to the game piece variables, and make more as appropriate
			EVEVATOR_MOTOR_CHANNEL = 0;
			RAMP_SOLENOID_CHANNEL = 0;

			isDriveMotorInverted = new boolean[] { false, true, false, true };
			break;
		case Board:
			HAS_WHEELS = false;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 256;

			HAS_ELEVATOR = false;
			HAS_RAMPS = false;

			HAS_GRABBER = true;
			GRABBER_L_CHANNEL = 0;
			GRABBER_R_CHANNEL = 1;
			OPTICAL_CHANNEL = 5;

			// TODO Assign values to the game piece variables, and make more as appropriate
			EVEVATOR_MOTOR_CHANNEL = 0;
			RAMP_SOLENOID_CHANNEL = 0;

			isDriveMotorInverted = new boolean[] { false, true, false, true };
			break;
		case Competition_1:
			HAS_WHEELS = true;
			WHEEL_CIRCUMFERENCE = 19.74;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 256;
			useSpeedControllers = true;
			POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEEL_CIRCUMFERENCE); // 1/2 inch

			LEFT_LEAD_CHANNEL = 1;
			LEFT_FOLLOWER_1_CHANNEL = 2;
			LEFT_FOLLOWER_2_CHANNEL = 3;

			RIGHT_LEAD_CHANNEL = 4;
			RIGHT_FOLLOWER_1_CHANNEL = 5;
			RIGHT_FOLLOWER_2_CHANNEL = 6;

			HAS_ELEVATOR = false;
			HAS_RAMPS = false;

			HAS_GRABBER = true;
			GRABBER_L_CHANNEL = 1; 
			GRABBER_R_CHANNEL = 2;
			OPTICAL_CHANNEL = 5;

			// TODO Assign values to the game piece variables, and make more as appropriate
			EVEVATOR_MOTOR_CHANNEL = 0;
			RAMP_SOLENOID_CHANNEL = 0;
			break;
		case Competition_2:
			HAS_WHEELS = true;
			WHEEL_CIRCUMFERENCE = 19.74;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 256;
			useSpeedControllers = true;
			POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEEL_CIRCUMFERENCE); // 1/2 inch

			LEFT_LEAD_CHANNEL = 1;
			LEFT_FOLLOWER_1_CHANNEL = 2;
			LEFT_FOLLOWER_2_CHANNEL = 3;

			RIGHT_LEAD_CHANNEL = 4;
			RIGHT_FOLLOWER_1_CHANNEL = 5;
			RIGHT_FOLLOWER_2_CHANNEL = 6;

			HAS_ELEVATOR = true;
			HAS_RAMPS = true;

			HAS_GRABBER = true;
			GRABBER_L_CHANNEL = 1; 
			GRABBER_R_CHANNEL = 2;
			OPTICAL_CHANNEL = 5;

			// TODO Assign values to the game piece variables, and make more as appropriate
			EVEVATOR_MOTOR_CHANNEL = 0;
			RAMP_SOLENOID_CHANNEL = 0;
			break;
		}
	}

	// Global robot constants
	public static RobotID robotID;

	public static boolean[] isDriveMotorInverted;
	public static boolean useSpeedControllers;
	public static final int VELOCITY_PID_PROFILE = 0;
	public static final int POSITION_PID_PROFILE = 1;
	public static double POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEEL_CIRCUMFERENCE); // 1/2 inch
	public static int VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR = 50; 	// This is in encoder ticks
	public static int POSITION_ALLOWABLE_CLOSED_LOOP_ERROR = (int) (POSITION_ALLOWED_ERROR * 1024 * 0.95); 	// This is in encoder ticks

	public static final double FAST_MAX_SPEED = 1.0;
	public static final double NORMAL_MAX_SPEED = 0.6;
	public static final double SLOW_MAX_SPEED = 0.35;

	/**
	 * The maximum revolutions per minute (RPM) of a wheel when in speed control mode.
	 * Also for motion magic?
	 */
	public static double MAX_SPEED;

	public static boolean useSimulator = false;

	public static final double MIN_DRIVE_SPEED = 0.1;

	// Robot Dimensions
	public static double WHEEL_BASE_LENGTH = 3.33;
	public static double WHEEL_BASE_WIDTH = 2.92; // TODO: MEASURE TRUE WHEEL BASE WIDTH

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
	public static double WHEEL_CIRCUMFERENCE;

	// The number of encoder ticks per one revolution of the wheel. This is used
	// for correctly determining RPM and position.
	public static int WHEEL_ENCODER_CODES_PER_REVOLUTION = 256;

	// Set to true to use LSM9DS1 IMU on Raspberry Pi
	// Set to false to use the local ADIS16448 IMU on the Robo Rio
	public static final boolean useRemoteImu = false;

	// Game Pieces
	public static boolean HAS_ELEVATOR;
	public static int EVEVATOR_MOTOR_CHANNEL;

	public static boolean HAS_GRABBER;
	public static double MAX_GRAB_SPEED = 1.0;
	public static double MIN_GRAB_SPEED = 0.1;
	public static double RELEASE_SPEED = -1.0;
	public static int GRABBER_L_CHANNEL; 
	public static int GRABBER_R_CHANNEL;
	public static int OPTICAL_CHANNEL;

	public static boolean HAS_RAMPS;
	public static int RAMP_SOLENOID_CHANNEL;
}