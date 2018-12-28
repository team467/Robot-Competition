package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotMap {

  // 0 is non-blocking (i.e. it doesn't wait for a response before going to the next statement)
  public static final int TALON_TIMEOUT = 0; 

  public static final int PID_SLOT_DRIVE = 0;
  public static final int PID_SLOT_TURN = 1;
  
  /* The lowest value is 196.0, the maximum value is 3741.0. The middle is 1968.5
   * New max: 2980, new min:956.5
   * 16.9 ticks = 1 inch
   * 1 rotation=253 ticks
   */

  // Steering motor ids
  // TODO: Enumerate steering motor IDS
  public static boolean HAS_WHEELS;
  public static int LEFT_LEAD_CHANNEL;
  public static int LEFT_FOLLOWER_1_CHANNEL;
  public static int LEFT_FOLLOWER_2_CHANNEL;

  public static int RIGHT_LEAD_CHANNEL;
  public static int RIGHT_FOLLOWER_1_CHANNEL;
  public static int RIGHT_FOLLOWER_2_CHANNEL;
  
  public static int FORWARD_PANIC_ANGLE;
  public static int BACKWARD_PANIC_ANGLE;

  public static int ALLOWED_GRABBER_ERROR = 2; // in degrees

  public static int AUTONOMOUS_DRIVE_TIMEOUT_MS;
  public static int AUTONOMOUS_TURN_TIMEOUT_MS;

  public static boolean RIGHT_DRIVE_SENSOR_IS_INVERTED;
  public static boolean LEFT_DRIVE_SENSOR_IS_INVERTED;
  public static boolean RIGHT_DRIVE_MOTOR_IS_INVERTED;
  public static boolean LEFT_DRIVE_MOTOR_IS_INVERTED;
  public static int DRIVEMOTOR_NUM;

  public static double RIGHT_TURN_PID_P;
  public static double RIGHT_TURN_PID_I;
  public static double RIGHT_TURN_PID_D;
  public static double RIGHT_TURN_PID_F;

  public static double RIGHT_DRIVE_PID_P;
  public static double RIGHT_DRIVE_PID_I;
  public static double RIGHT_DRIVE_PID_D;
  public static double RIGHT_DRIVE_PID_F;

  public static double LEFT_TURN_PID_P;
  public static double LEFT_TURN_PID_I;
  public static double LEFT_TURN_PID_D;
  public static double LEFT_TURN_PID_F;

  public static double LEFT_DRIVE_PID_P;
  public static double LEFT_DRIVE_PID_I;
  public static double LEFT_DRIVE_PID_D;
  public static double LEFT_DRIVE_PID_F;

  public enum RobotId {
    Board, Competition_1, Competition_2
  }

  // Initialize robot map. 
  public static void init(RobotId id) {
    robotId = id;
    switch (id) {
      
      case Competition_1:
        HAS_WHEELS = true;
        DRIVEMOTOR_NUM = 4;
        WHEEL_CIRCUMFERENCE = 18.50;
        WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;
        useSpeedControllers = true;

        FORWARD_PANIC_ANGLE = 60;
        BACKWARD_PANIC_ANGLE = -60;

        LEFT_LEAD_CHANNEL = 1;
        LEFT_FOLLOWER_1_CHANNEL = 2;
        LEFT_FOLLOWER_2_CHANNEL = 3;
        LEFT_DRIVE_SENSOR_IS_INVERTED = true;
        LEFT_DRIVE_MOTOR_IS_INVERTED = false;

        RIGHT_LEAD_CHANNEL = 4;
        RIGHT_FOLLOWER_1_CHANNEL = 5;
        RIGHT_FOLLOWER_2_CHANNEL = 6;
        RIGHT_DRIVE_SENSOR_IS_INVERTED = true;
        RIGHT_DRIVE_MOTOR_IS_INVERTED = true;
        
        RIGHT_GRABBER_SOLENOID_EXISTS = true;
        LEFT_GRABBER_SOLENOID_EXISTS = true;
        
        CLIMB_MOTOR_CONTROLLER_LEADER = 11;
        CLIMB_MOTOR_CONTROLLER_FOLLOWER1 = 12;

        //Linear PIDS
        LEFT_DRIVE_PID_P = 2.025;
        LEFT_DRIVE_PID_I = 0.0;
        LEFT_DRIVE_PID_D = 400.0;
        LEFT_DRIVE_PID_F = 0.0;

        RIGHT_DRIVE_PID_P = 2.025;
        RIGHT_DRIVE_PID_I = 0.0;
        RIGHT_DRIVE_PID_D = 512.0;
        RIGHT_DRIVE_PID_F = 0.0;

        // Turn PIDs
        LEFT_TURN_PID_P = 1.75;
        LEFT_TURN_PID_I = 0.0;
        LEFT_TURN_PID_D = 180.0;
        LEFT_TURN_PID_F = 0.0;

        RIGHT_TURN_PID_P = 1.75;
        RIGHT_TURN_PID_I = 0.0;
        RIGHT_TURN_PID_D = 180.0;
        RIGHT_TURN_PID_F = 0.0;

        HAS_CLIMBER = true;
        CLIMB_MOTOR_CONTROLLER_LEADER = 11;
        CLIMB_MOTOR_CONTROLLER_FOLLOWER1 = 12;

        useSimulator = false;
        USE_FAKE_GAME_DATA = true;

        HAS_GRABBER = true;
        GRABBER_INVERT = false;
        HAS_CAMERA = false;

        GRABBER_L_CHANNEL = 0;
        GRABBER_R_CHANNEL = 1;
        OPTICAL_CHANNEL = 5;

        // TODO Assign values to the game piece variables, and make more as appropriate
        HAS_ELEVATOR = true;
        ELEVATOR_MOTOR_CHANNEL = 7;

        ELEVATOR_BOTTOM_TICKS = 881;
        ELEVATOR_TOP_TICKS = 557;

        ELEVATOR_FLOOR = 0.042;
        ELEVATOR_SWITCH = 0.315;
        ELEVATOR_LOW_SCALE = 0.729;

        HAS_LEFT_RAMP = false;
        RAMP_LEFT_FORWARD_CHANNEL = 1;
        RAMP_LEFT_REVERSE_CHANNEL = 4;

        HAS_RIGHT_RAMP = false;
        RAMP_RIGHT_FORWARD_CHANNEL = 2;
        RAMP_RIGHT_REVERSE_CHANNEL = 5;

        RAMP_RELEASE_FORWARD_CHANNEL = 0;
        RAMP_RELEASE_REVERSE_CHANNEL = 3;

        AUTONOMOUS_DRIVE_TIMEOUT_MS = 500;
        AUTONOMOUS_TURN_TIMEOUT_MS = 1000;

        CLIMBER_RAMP_TIME = 0.5;
        break;

      case Competition_2:
        HAS_WHEELS = true;
        DRIVEMOTOR_NUM = 4;
        WHEEL_CIRCUMFERENCE = 18.00; //19.74;
        WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;
        useSpeedControllers = true;

        FORWARD_PANIC_ANGLE = 60;
        BACKWARD_PANIC_ANGLE = -60;

        LEFT_LEAD_CHANNEL = 1;
        LEFT_FOLLOWER_1_CHANNEL = 2;
        LEFT_FOLLOWER_2_CHANNEL = 3;
        LEFT_DRIVE_SENSOR_IS_INVERTED = true;
        LEFT_DRIVE_MOTOR_IS_INVERTED = false;

        RIGHT_LEAD_CHANNEL = 4;
        RIGHT_FOLLOWER_1_CHANNEL = 5;
        RIGHT_FOLLOWER_2_CHANNEL = 6;
        RIGHT_DRIVE_SENSOR_IS_INVERTED = true;
        RIGHT_DRIVE_MOTOR_IS_INVERTED = true;
        
        HAS_CLIMBER = false;
        CLIMB_MOTOR_CONTROLLER_LEADER = 11;
        CLIMB_MOTOR_CONTROLLER_FOLLOWER1 = 12;
        CLIMBER_RAMP_TIME = 0.5;
        
        RIGHT_GRABBER_SOLENOID_EXISTS = true;
        LEFT_GRABBER_SOLENOID_EXISTS = true;

        // Linear PIDS
        LEFT_DRIVE_PID_P = 1.0;
        LEFT_DRIVE_PID_I = 0.0;
        LEFT_DRIVE_PID_D = 450.0;
        LEFT_DRIVE_PID_F = 0.0;

        RIGHT_DRIVE_PID_P = 1.0;
        RIGHT_DRIVE_PID_I = 0.0;
        RIGHT_DRIVE_PID_D = 450.0;
        RIGHT_DRIVE_PID_F = 0.0;

        // Turn PIDs
        LEFT_TURN_PID_P = 1.0;
        LEFT_TURN_PID_I = 0.0;
        LEFT_TURN_PID_D = 450.0;
        LEFT_TURN_PID_F = 0.0;

        RIGHT_TURN_PID_P = 1.0;
        RIGHT_TURN_PID_I = 0.0;
        RIGHT_TURN_PID_D = 450.0;
        RIGHT_TURN_PID_F = 0.0;

        HAS_ELEVATOR = true;
        ELEVATOR_MOTOR_CHANNEL = 7;

        ELEVATOR_BOTTOM_TICKS = 630;
        ELEVATOR_TOP_TICKS = 195;

        ELEVATOR_FLOOR = 0.040;
        ELEVATOR_SWITCH = 0.300;
        ELEVATOR_LOW_SCALE = 0.693;

        HAS_GRABBER = true;
        GRABBER_INVERT = true;
        GRABBER_L_CHANNEL = 0; 
        GRABBER_R_CHANNEL = 1;
        OPTICAL_CHANNEL = 5;

        HAS_CAMERA = false;

        useSimulator = false;
        USE_FAKE_GAME_DATA = false;

        HAS_LEFT_RAMP = false;
        RAMP_LEFT_FORWARD_CHANNEL = 1;
        RAMP_LEFT_REVERSE_CHANNEL = 4;

        HAS_RIGHT_RAMP = false;
        RAMP_RIGHT_FORWARD_CHANNEL = 2;
        RAMP_RIGHT_REVERSE_CHANNEL = 5;

        RAMP_RELEASE_FORWARD_CHANNEL = 0;
        RAMP_RELEASE_REVERSE_CHANNEL = 3;

        AUTONOMOUS_DRIVE_TIMEOUT_MS = 200;
        AUTONOMOUS_TURN_TIMEOUT_MS = 300;
        break;

      case Board:
      default:
        HAS_WHEELS = false;
        DRIVEMOTOR_NUM = 0;
        WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;

        FORWARD_PANIC_ANGLE = 45;
        BACKWARD_PANIC_ANGLE = -45;

        HAS_GRABBER = false;
        GRABBER_L_CHANNEL = 0;
        GRABBER_R_CHANNEL = 1;
        OPTICAL_CHANNEL = 5;

        HAS_CLIMBER = true;
        CLIMB_MOTOR_CONTROLLER_LEADER = 1;

        HAS_ELEVATOR = false;
        //ELEVATOR_MOTOR_CHANNEL = 1;
        HAS_CAMERA = false;
        HAS_LEFT_RAMP = false;
        HAS_RIGHT_RAMP = false;

        
        isDriveMotorInverted = new boolean[] { false, true, false, true };
        break;
    }
    //These calculations can be made after the robot-specific constants are set. 
    POSITION_ALLOWED_ERROR = ALLOWED_ERROR_INCHES / RobotMap.WHEEL_CIRCUMFERENCE;
    POSITION_ALLOWABLE_CLOSED_LOOP_ERROR 
        = (int) (POSITION_ALLOWED_ERROR * 1024 * 0.95);// This is in encoder ticks
  }

  /**
   * Overrides normal values with values required for simulator.
   */
  static void setSimulator() {

    RobotMap.useSimulator = true;
    RobotMap.USE_FAKE_GAME_DATA = true;  

    //Linear PIDS
    LEFT_DRIVE_PID_P = 0.00033;
    LEFT_DRIVE_PID_I = 0.0;
    LEFT_DRIVE_PID_D = 0.0;
    LEFT_DRIVE_PID_F = 0.0;

    RIGHT_DRIVE_PID_P = 0.00033;
    RIGHT_DRIVE_PID_I = 0.0;
    RIGHT_DRIVE_PID_D = 0.0;
    RIGHT_DRIVE_PID_F = 0.0;

    // Turn PIDs
    LEFT_TURN_PID_P = 0.00051;
    LEFT_TURN_PID_I = 0.0;
    LEFT_TURN_PID_D = 0.0;
    LEFT_TURN_PID_F = 0.0;

    RIGHT_TURN_PID_P = 0.00051;
    RIGHT_TURN_PID_I = 0.0;
    RIGHT_TURN_PID_D = 0.0;
    RIGHT_TURN_PID_F = 0.0;  
  }

  /**
   * Used to load Robot Map PID values onto the Smart Dashboard for tuning.
   * 
   * @param pidSlot 0 for drive, 1 for turn PIDs
   */
  public static void loadPidsOntoSmartDashboard(int pidSlot) {
    if (pidSlot == 0) {
      SmartDashboard.putString("DB/String 0", "10.0"); // Tune Distance
      SmartDashboard.putString("DB/String 1", String.valueOf(LEFT_DRIVE_PID_P)); // P Left
      SmartDashboard.putString("DB/String 2", String.valueOf(LEFT_DRIVE_PID_I)); // I Left
      SmartDashboard.putString("DB/String 3", String.valueOf(LEFT_DRIVE_PID_D)); // D Left
      SmartDashboard.putString("DB/String 4", String.valueOf(LEFT_DRIVE_PID_F)); // F Left
      SmartDashboard.putString("DB/String 5", "0"); // PID Slot
      SmartDashboard.putString("DB/String 6", String.valueOf(RIGHT_DRIVE_PID_P)); // P Right
      SmartDashboard.putString("DB/String 7", String.valueOf(RIGHT_DRIVE_PID_I)); // I Right
      SmartDashboard.putString("DB/String 8", String.valueOf(RIGHT_DRIVE_PID_D)); // D Right
      SmartDashboard.putString("DB/String 9", String.valueOf(RIGHT_DRIVE_PID_F)); // F Right
    } else {
      SmartDashboard.putString("DB/String 0", "90.0"); // Tune Distance
      SmartDashboard.putString("DB/String 1", String.valueOf(LEFT_TURN_PID_P)); // P Left
      SmartDashboard.putString("DB/String 2", String.valueOf(LEFT_TURN_PID_I)); // I Left
      SmartDashboard.putString("DB/String 3", String.valueOf(LEFT_TURN_PID_D)); // D Left
      SmartDashboard.putString("DB/String 4", String.valueOf(LEFT_TURN_PID_F)); // F Left
      SmartDashboard.putString("DB/String 5", "1"); // PID Slot
      SmartDashboard.putString("DB/String 6", String.valueOf(RIGHT_TURN_PID_P)); // P Right
      SmartDashboard.putString("DB/String 7", String.valueOf(RIGHT_TURN_PID_I)); // I Right
      SmartDashboard.putString("DB/String 8", String.valueOf(RIGHT_TURN_PID_D)); // D Right
      SmartDashboard.putString("DB/String 9", String.valueOf(RIGHT_TURN_PID_F)); // F Right
    }
  }

  // Global robot constants
  public static RobotId robotId;

  public static final int ITERATION_TIME_MS = 20;

  public static boolean[] isDriveMotorInverted;

  public static boolean useSpeedControllers;

  public static final int VELOCITY_PID_PROFILE = 0;

  public static final int POSITION_PID_PROFILE = 1;

  public static double ALLOWED_ERROR_INCHES = 0.5;

  public static double POSITION_ALLOWED_ERROR;

  public static int POSITION_ALLOWABLE_CLOSED_LOOP_ERROR;

  public static final double FAST_MAX_SPEED = 1.0;
  public static final double NORMAL_MAX_SPEED = 0.8;
  public static final double SLOW_MAX_SPEED = 0.5;

  public static final double ELEVATOR_HIGH_DRIVE_RAMP_TIME = 2.5;
  public static final double ELEVATOR_LOW_DRIVE_RAMP_TIME = 0.0;

  // TODO These values need to be tested on the robot and possibly adjusted.
  public static final double NORMAL_TURN_MAX_SPEED = 1.0;
  public static final double SLOW_TURN_MAX_SPEED = 0.8;
  public static final double MAX_CARROT_LENGTH = 4.0;

  public static boolean useSimulator = true;
  public static boolean USE_FAKE_GAME_DATA = false;
  public static final double MIN_DRIVE_SPEED = 0.1;
  public static final double CLIMB_MIN_DRIVE_SPEED = 0.3;

  // How far the sensor speeds can be and still be considered turning in place,
  // in sensor units per 100 ms
  public static final int TURN_IN_PLACE_DETECT_TOLERANCE = 150;

  // Robot Dimensions
  public static double WHEEL_BASE_LENGTH = 3.33;
  public static double WHEEL_BASE_WIDTH = 1.99; // TODO: MEASURE TRUE WHEEL BASE WIDTH
  public static double BUMPER_LENGTH = 3.33;
  public static double BUMPER_WIDTH = 2.92;

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

  public static boolean HAS_CLIMBER;
  public static int CLIMB_MOTOR_CONTROLLER_LEADER;
  public static int CLIMB_MOTOR_CONTROLLER_FOLLOWER1;
  public static double CLIMBER_RAMP_TIME;
  public static double CLIMBER_SPEED = 1.0;

  public static boolean HAS_ELEVATOR;
  public static int ELEVATOR_MOTOR_CHANNEL;
  public static double MIN_LIFT_SPEED = 0.1;
  public static final double ELEVATOR_HEIGHT_RANGE_INCHES = 94.5;
  public static final double ELEVATOR_ERROR_TOLERANCE_INCHES = 1.0;

  public static int ELEVATOR_BOTTOM_TICKS;
  public static int ELEVATOR_TOP_TICKS;

  public static double ELEVATOR_BOTTOM = 0.0;
  public static double ELEVATOR_FLOOR;
  public static double ELEVATOR_SWITCH;
  public static double ELEVATOR_LOW_SCALE;
  public static double ELEVATOR_TOP = 1.0;

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
  
  public static boolean RIGHT_GRABBER_SOLENOID_EXISTS;
  public static boolean LEFT_GRABBER_SOLENOID_EXISTS;
  public static int RIGHT_GRABBER_FORWARD_CHANNEL = 1; //redo
  public static int RIGHT_GRABBER_REVERSE_CHANNEL = 0;
  public static int LEFT_GRABBER_FORWARD_CHANNEL = 2;
  public static int LEFT_GRABBER_REVERSE_CHANNEL = 3;
  
}