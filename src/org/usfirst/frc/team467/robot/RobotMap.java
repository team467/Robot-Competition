package org.usfirst.frc.team467.robot;

/**
 *
 */
public class RobotMap {
	public static final int TALON_TIMEOUT = 10; // 10 ms is the recommended timeout

	public enum RobotID {
		Board, Competition_1, Competition_2
	};

	public static final int FRONT_LEFT = 0;
	public static final int FRONT_RIGHT = 1;
	public static final int BACK_LEFT = 2;
	public static final int BACK_RIGHT = 3;

	/*	* The lowest value is 196.0, the maximum value is 3741.0. The middle is 1968.5
	 * New max: 2980, new min:956.5
	 * 16.9 ticks = 1 inch
	 * 1 rotation=253 ticks */

	// Steering motor ids
	// TODO: Enumerate steering motor IDS
	public static boolean HAS_WHEELS;
	public static int LEFT_LEAD_CHANNEL;
	public static int LEFT_FOLLOWER_1_CHANNEL;
	public static int LEFT_FOLLOWER_2_CHANNEL;

	public static int FORWARD_PANIC_ANGLE;
	public static int BACKWARD_PANIC_ANGLE;

	public static int RIGHT_LEAD_CHANNEL;
	public static int RIGHT_FOLLOWER_1_CHANNEL;
	public static int RIGHT_FOLLOWER_2_CHANNEL;

	public static int ALLOWED_GRABBER_ERROR = 2; // in degrees

	public static int AUTONOMOUS_DRIVE_TIMEOUT_MS;

	public static boolean RIGHT_DRIVE_SENSOR_IS_INVERTED;
	public static boolean LEFT_DRIVE_SENSOR_IS_INVERTED;
	public static int DRIVEMOTOR_NUM;
	
	public static double RIGHT_DRIVE_PID_P;
	public static double RIGHT_DRIVE_PID_I;
	public static double RIGHT_DRIVE_PID_D;
	public static double RIGHT_DRIVE_PID_F;

	public static double LEFT_DRIVE_PID_P;
	public static double LEFT_DRIVE_PID_I;
	public static double LEFT_DRIVE_PID_D;
	public static double LEFT_DRIVE_PID_F;
	
	// Initialize robot map. 
	public static void init(RobotID id) {
		robotID = id;
		switch (id) {
		case Board:
			HAS_WHEELS = false;
			DRIVEMOTOR_NUM = 0;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;

			FORWARD_PANIC_ANGLE = 45;
			BACKWARD_PANIC_ANGLE = -45;

			HAS_GRABBER = true;
			GRABBER_L_CHANNEL = 0;
			GRABBER_R_CHANNEL = 1;
			OPTICAL_CHANNEL = 5;

			HAS_ELEVATOR = true;
			ELEVATOR_MOTOR_CHANNEL = 1;
			HAS_CAMERA = true;
			HAS_LEFT_RAMP = false;
			HAS_RIGHT_RAMP = false;

			isDriveMotorInverted = new boolean[] { false, true, false, true };
			break;
		case Competition_1:
			HAS_WHEELS = true;
			DRIVEMOTOR_NUM = 4;
			WHEEL_CIRCUMFERENCE = 18.50;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;
			useSpeedControllers = true;
			POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEEL_CIRCUMFERENCE); // 1/2 inch

			FORWARD_PANIC_ANGLE = 60;
			BACKWARD_PANIC_ANGLE = -60;

			LEFT_LEAD_CHANNEL = 1;
			LEFT_FOLLOWER_1_CHANNEL = 2;
			LEFT_FOLLOWER_2_CHANNEL = 3;
			LEFT_DRIVE_SENSOR_IS_INVERTED = true;
			LEFT_DRIVE_PID_P = 1.6;
			LEFT_DRIVE_PID_I = 0.0;
			LEFT_DRIVE_PID_D = 140;
			LEFT_DRIVE_PID_F = 1.11;

			RIGHT_LEAD_CHANNEL = 4;
			RIGHT_FOLLOWER_1_CHANNEL = 5;
			RIGHT_FOLLOWER_2_CHANNEL = 6;
			RIGHT_DRIVE_SENSOR_IS_INVERTED = true;
			RIGHT_DRIVE_PID_P = 1.7;
			RIGHT_DRIVE_PID_I = 0.0;
			RIGHT_DRIVE_PID_D = 175;
			RIGHT_DRIVE_PID_F = 1.14;

			HAS_ELEVATOR = true;

			HAS_GRABBER = true;
			GRABBER_INVERT = false;
			HAS_CAMERA = false;

			GRABBER_L_CHANNEL = 0;
			GRABBER_R_CHANNEL = 1;
			OPTICAL_CHANNEL = 5;

			// TODO Assign values to the game piece variables, and make more as appropriate
			ELEVATOR_MOTOR_CHANNEL = 7;
			ELEVATOR_BOTTOM_TICKS = 764;
			ELEVATOR_FLOOR_HEIGHT = 747;
			ELEVATOR_SWITCH_HEIGHT = 636;
			ELEVATOR_LOW_SCALE_HEIGHT = 468;
			ELEVATOR_TOP_TICKS = 357;

			HAS_LEFT_RAMP = true;
			RAMP_LEFT_FORWARD_CHANNEL = 1;
			RAMP_LEFT_REVERSE_CHANNEL = 4;

			HAS_RIGHT_RAMP = true;
			RAMP_RIGHT_FORWARD_CHANNEL = 2;
			RAMP_RIGHT_REVERSE_CHANNEL = 5;

			RAMP_RELEASE_FORWARD_CHANNEL = 0;
			RAMP_RELEASE_REVERSE_CHANNEL = 3;

			AUTONOMOUS_DRIVE_TIMEOUT_MS = 500;
			break;
		case Competition_2:
			HAS_WHEELS = true;
			DRIVEMOTOR_NUM = 4;
			WHEEL_CIRCUMFERENCE = 19.74;
			WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;
			useSpeedControllers = true;
			POSITION_ALLOWED_ERROR = (0.5 / RobotMap.WHEEL_CIRCUMFERENCE); // 1/2 inch

			FORWARD_PANIC_ANGLE = 60;
			BACKWARD_PANIC_ANGLE = -60;

			LEFT_LEAD_CHANNEL = 1;
			LEFT_FOLLOWER_1_CHANNEL = 2;
			LEFT_FOLLOWER_2_CHANNEL = 3;
			LEFT_DRIVE_PID_P = 1.6;
			LEFT_DRIVE_PID_I = 0.0;
			LEFT_DRIVE_PID_D = 140;
			LEFT_DRIVE_PID_F = 1.11;

			RIGHT_LEAD_CHANNEL = 4;
			RIGHT_FOLLOWER_1_CHANNEL = 5;
			RIGHT_FOLLOWER_2_CHANNEL = 6;
			RIGHT_DRIVE_PID_P = 1.7;
			RIGHT_DRIVE_PID_I = 0.0;
			RIGHT_DRIVE_PID_D = 175;
			RIGHT_DRIVE_PID_F = 1.14;

			HAS_ELEVATOR = true;
			ELEVATOR_MOTOR_CHANNEL = 7;

			// TODO Replace with empirical measured values
			ELEVATOR_BOTTOM_TICKS = 764;
			ELEVATOR_FLOOR_HEIGHT = 747;
			ELEVATOR_SWITCH_HEIGHT = 636;
			ELEVATOR_LOW_SCALE_HEIGHT = 468;
			ELEVATOR_TOP_TICKS = 357;

			HAS_GRABBER = true;
			GRABBER_INVERT = true;
			GRABBER_L_CHANNEL = 0; 
			GRABBER_R_CHANNEL = 1;
			OPTICAL_CHANNEL = 5;

			HAS_CAMERA = false;

			HAS_LEFT_RAMP = true;
			RAMP_LEFT_FORWARD_CHANNEL = 1;
			RAMP_LEFT_REVERSE_CHANNEL = 4;

			HAS_RIGHT_RAMP = true;
			RAMP_RIGHT_FORWARD_CHANNEL = 2;
			RAMP_RIGHT_REVERSE_CHANNEL = 5;

			RAMP_RELEASE_FORWARD_CHANNEL = 0;
			RAMP_RELEASE_REVERSE_CHANNEL = 3;

			AUTONOMOUS_DRIVE_TIMEOUT_MS = 500;
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

	public static final double ELEVATOR_HIGH_DRIVE_RAMP_TIME = 2.5;
	public static final double ELEVATOR_LOW_DRIVE_RAMP_TIME = 0.0;

	// TODO These values need to be tested on the robot and possibly adjusted.
	public static final double NORMAL_TURN_MAX_SPEED = 0.8;
	public static final double SLOW_TURN_MAX_SPEED = 0.6;

	public static boolean useSimulator = false;
	public static boolean USE_FAKE_GAME_DATA = false;

	public static final double MIN_DRIVE_SPEED = 0.1;

	// Robot Dimensions
	public static double WHEEL_BASE_LENGTH = 3.33;
	public static double WHEEL_BASE_WIDTH = 1.99; // TODO: MEASURE TRUE WHEEL BASE WIDTH

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
	public static int WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;

	// Set to true to use LSM9DS1 IMU on Raspberry Pi
	// Set to false to use the local ADIS16448 IMU on the Robo Rio
	public static final boolean useRemoteImu = false;

	// Game Pieces

	// Elevator Constants
	public static final double ELEVATOR_GEAR_CIRCUMFERENCE_IN_INCHES = 20; // TODO: SET
	public static int ELEVATOR_HEIGHT_SENSOR_ID = 7; // TODO: SET
	public static int ELEVATOR_INITIAL_TICKS = 123; // TODO: SET
	public static double ELEVATOR_MIN_HEIGHT_IN_FEET = 0.0; // TODO: SET
	public static double ELEVATOR_MAX_HEIGHT_IN_FEET = 8.0; // TODO: SET
	public static int ELEVATOR_TICKS_PER_TURN = 20; // TODO: SET
	public static int MAX_ELEVATOR_RPM = 20; // TODO: SET

	public static boolean HAS_ELEVATOR;
	public static int ELEVATOR_MOTOR_CHANNEL;
	public static double MIN_LIFT_SPEED = 0.1;
	public static final double ELEVATOR_HEIGHT_RANGE_INCHES = 94.5;
	public static final double ELEVATOR_ERROR_TOLERANCE_INCHES = 1.0;

	public static int ELEVATOR_BOTTOM_TICKS;
	public static int ELEVATOR_FLOOR_HEIGHT;
	public static int ELEVATOR_SWITCH_HEIGHT;
	public static int ELEVATOR_LOW_SCALE_HEIGHT;
	public static int ELEVATOR_TOP_TICKS;

	// Ticks per inch is based on empirical measurements on the robot. Approximately 4.740...
	public static final double ELEVATOR_TICKS_PER_INCH = (ELEVATOR_BOTTOM_TICKS - ELEVATOR_TOP_TICKS) / ELEVATOR_HEIGHT_RANGE_INCHES;

	public static boolean HAS_GRABBER;
	public static double MAX_GRAB_SPEED = 1.0;
	public static double MIN_GRAB_SPEED = 0.1;
	public static double RELEASE_SPEED = -1.0;
	public static boolean GRABBER_INVERT;
	public static int GRABBER_L_CHANNEL;
	public static int GRABBER_R_CHANNEL;
	public static int OPTICAL_CHANNEL;

	public static boolean HAS_CAMERA;

	public static boolean HAS_LEFT_RAMP;
	public static int RAMP_LEFT_FORWARD_CHANNEL;
	public static int RAMP_LEFT_REVERSE_CHANNEL;

	public static boolean HAS_RIGHT_RAMP;
	public static int RAMP_RIGHT_FORWARD_CHANNEL;
	public static int RAMP_RIGHT_REVERSE_CHANNEL;

	public static int RAMP_RELEASE_FORWARD_CHANNEL;
	public static int RAMP_RELEASE_REVERSE_CHANNEL;
}