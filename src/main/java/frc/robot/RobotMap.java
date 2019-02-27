package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotMap {

  // Global robot constants
  public static RobotId robotId;

  public static final int ITERATION_TIME_MS = 20;

  // 0 is non-blocking (i.e. it doesn't wait for a response before going to the
  // next statement)
  public static final int TALON_TIMEOUT = 0;

  public static final int PID_SLOT_DRIVE = 0;
  public static final int PID_SLOT_TURN = 1;

  // Turret angle offsets
  public static final double ON_TARGET = 1.0;
  public static final double ANGLE_OFFSET_LEVEL_ONE = 5.0;
  public static final double ANGLE_OFFSET_LEVEL_TWO = 10.0;
  public static final double ANGLE_OFFSET_LEVEL_THREE = 15.0;
  public static final double ANGLE_OFFSET_LEVEL_FOUR = 20.0;
  public static final int PID_SLOT_TURRET = 0; // Slot will need to be determined and set

  // Steering motor ids
  public static boolean HAS_WHEELS;
  public static int DRIVEMOTOR_NUM;
  public static int AUTONOMOUS_DRIVE_TIMEOUT_MS = 200;
  public static int AUTONOMOUS_TURN_TIMEOUT_MS = 300;

  public static int LEFT_LEAD_CHANNEL;
  public static int LEFT_FOLLOWER_1_CHANNEL;
  public static int LEFT_FOLLOWER_2_CHANNEL;
  public static boolean LEFT_DRIVE_SENSOR_IS_INVERTED;
  public static boolean LEFT_DRIVE_MOTOR_IS_INVERTED;

  public static double LEFT_TURN_PID_P;
  public static double LEFT_TURN_PID_I;
  public static double LEFT_TURN_PID_D;
  public static double LEFT_TURN_PID_F;

  public static double LEFT_DRIVE_PID_P;
  public static double LEFT_DRIVE_PID_I;
  public static double LEFT_DRIVE_PID_D;
  public static double LEFT_DRIVE_PID_F;

  public static int RIGHT_LEAD_CHANNEL;
  public static int RIGHT_FOLLOWER_1_CHANNEL;
  public static int RIGHT_FOLLOWER_2_CHANNEL;
  public static boolean RIGHT_DRIVE_SENSOR_IS_INVERTED;
  public static boolean RIGHT_DRIVE_MOTOR_IS_INVERTED;

  public static double RIGHT_TURN_PID_P;
  public static double RIGHT_TURN_PID_I;
  public static double RIGHT_TURN_PID_D;
  public static double RIGHT_TURN_PID_F;

  public static double RIGHT_DRIVE_PID_P;
  public static double RIGHT_DRIVE_PID_I;
  public static double RIGHT_DRIVE_PID_D;
  public static double RIGHT_DRIVE_PID_F;

  public static boolean useSpeedControllers;

  public static double ALLOWED_ERROR_INCHES = 0.5;

  public static double POSITION_ALLOWED_ERROR;

  public static int POSITION_ALLOWABLE_CLOSED_LOOP_ERROR;

  public static final double FAST_MAX_SPEED = 1.0;
  public static final double NORMAL_MAX_SPEED = 0.8;
  public static final double SLOW_MAX_SPEED = 0.5;

  public static final double NORMAL_TURN_MAX_SPEED = 1.0;
  public static final double SLOW_TURN_MAX_SPEED = 0.8;

  public static boolean useSimulator = true;
  public static final double MIN_DRIVE_SPEED = 0.1;

  // How far the sensor speeds can be and still be considered turning in place,
  // in sensor units per 100 ms
  public static final int TURN_IN_PLACE_DETECT_TOLERANCE = 150;

  // Robot Dimensions
  // TODO: Measure robot dimensions
  public static double WHEEL_BASE_LENGTH = 3.33;
  public static double WHEEL_BASE_WIDTH = 1.99;
  public static double BUMPER_LENGTH = 3.33;
  public static double BUMPER_WIDTH = 2.92;

  /**
   * Used to ensure that all Talon SRX outputs are relative to a fixed value. If
   * the available voltage is below the nominal and a value about that is
   * requested, the output will be 100%.
   */
  public static final double NOMINAL_BATTERY_VOLTAGE = 12.0;

  // The circumference of the wheels for use in determining distance in
  // position mode
  public static double WHEEL_CIRCUMFERENCE;

  // The number of encoder ticks per one revolution of the wheel. This is used
  // for correctly determining RPM and position.
  public static final int WHEEL_ENCODER_CODES_PER_REVOLUTION = 1024;

  // Set to true to use LSM9DS1 IMU on Raspberry Pi
  // Set to false to use the local ADIS16448 IMU on the Robo Rio
  public static final boolean useRemoteImu = false;

  public enum RobotId {
    MINIBOT, ROBOT_2018, ROBOT_2019
  }

  /**
   * Initialize robot map.
   * 
   * @param id the robot identifier.
   */
  public static void init(RobotId id) {

    robotId = id;
    useSimulator = false;

    switch (id) {

      case ROBOT_2018:
        NORMAL_DRIVE_SPEED_MULTIPLIER = 0.8;
        SLOW_DRIVE_SPEED_MULTIPLIER = 0.6;

        HAS_WHEELS = true;
        DRIVEMOTOR_NUM = 4;
        WHEEL_CIRCUMFERENCE = 18.50;

        CONTROLS_INVERTED_FB = false;
        CONTROLS_INVERTED_TURN = false;

        USE_VELOCITY_SPEED_CONTROL_FOR_TELOP = true;
        VELOCITY_MULTIPLIER_RIGHT = 900;
        VELOCITY_MULTIPLIER_LEFT = 900;

        LEFT_LEAD_CHANNEL = 1;
        LEFT_FOLLOWER_1_CHANNEL = 2;
        LEFT_DRIVE_SENSOR_IS_INVERTED = true;
        LEFT_DRIVE_MOTOR_IS_INVERTED = false;

        RIGHT_LEAD_CHANNEL = 4;
        RIGHT_FOLLOWER_1_CHANNEL = 5;
        RIGHT_DRIVE_SENSOR_IS_INVERTED = true;
        RIGHT_DRIVE_MOTOR_IS_INVERTED = false;

        // Linear PIDS
        LEFT_DRIVE_PID_P = 0.5;
        LEFT_DRIVE_PID_I = 0.0;
        LEFT_DRIVE_PID_D = 0.0;
        LEFT_DRIVE_PID_F = 1.137;

        RIGHT_DRIVE_PID_P = 0.5;
        RIGHT_DRIVE_PID_I = 0.0;
        RIGHT_DRIVE_PID_D = 0.0;
        RIGHT_DRIVE_PID_F = 1.088;

        // Turn PIDs
        LEFT_TURN_PID_P = 1.0;
        LEFT_TURN_PID_I = 0.0;
        LEFT_TURN_PID_D = 450.0;
        LEFT_TURN_PID_F = 0.0;

        RIGHT_TURN_PID_P = 1.0;
        RIGHT_TURN_PID_I = 0.0;
        RIGHT_TURN_PID_D = 450.0;
        RIGHT_TURN_PID_F = 0.0;

        FORWARD_CAMERA_INDEX = 0;
        BACKWARD_CAMERA_INDEX = 2;
        HATCH_CAMERA_INDEX = 1;
        CARGO_CAMERA_INDEX = 3;
        HAS_CAMERA = true;
        AUTO_CAMERA = true;

        // Game Pieces
        HAS_TURRET = false;
        HAS_ROLLER_INTAKE = false;
        HAS_HATCH_MECHANISM = false;
        HAS_CARGO_MECHANISM = false;

        // Turret
        TURRET_MOTOR_CHANNEL = 5;
        TURRET_MOTOR_INVERTED = false; // TODO
        TURRET_SENSOR_INVERTED = false; // TODO
        TURRET_RIGHT_LIMIT_TICKS = 0; // TODO
        TURRET_LEFT_LIMIT_TICKS = 0; // TODO
        TURRET_RIGHT_LIMIT_DEGREES = 0.0; // TODO
        TURRET_LEFT_LIMIT_DEGREES = 0.0; // TODO
        TURRET_ALLOWABLE_ERROR_TICKS = 10; // TODO
        TURRET_HOME = 0.0;
        // TODO: Set TURRET_OFFSET
        TURRET_P = 1.0; // TODO
        TURRET_I = 0.0; // TODO
        TURRET_D = 0.0; // TODO
        TURRET_F = 0.0; // TODO

        // Hatch Mechanism
        HATCH_LAUNCHER_PCM_CHANNEL = 0;
        // TODO update forward/reverse channel
        HATCH_LAUNCHER_SOL_FORWARD_CHANNEL = 6;
        HATCH_LAUNCHER_SOL_REVERSE_CHANNEL = 7;
        HATCH_MECH_ARM_PCM_CHANNEL = 0;
        // TODO update forward/reverse channel
        HATCH_MECH_ARM_FORWARD_CHANNEL = 4;
        HATCH_MECH_ARM_REVERSE_CHANNEL = 5;
        HATCH_CAMERA_INDEX = 3;

        // Cargo Intake
        ROLLER_PCM_CHANNEL = 0;
        // TODO update up/down channel
        ROLLER_RIGHT_ARM_UP_SOLINOID_CHANNEL = 0;
        ROLLER_RIGHT_ARM_DOWN_SOLINOID_CHANNEL = 1;
        // unused
        ROLLER_LEFT_ARM_UP_SOLINOID_CHANNEL = 2;
        ROLLER_LEFT_ARM_DOWN_SOLINOID_CHANNEL = 3;
        // TODO use two solenoids
        ROLLER_MOTOR_CHANNEL = 1;
        ROLLER_MOTOR_INVERTED = false; // TODO
        // TODO use two sparks

        // Cargo Mechanism
        CARGO_MECH_WRIST_MOTOR_CHANNEL = 6; // TODO
        CARGO_MECH_WRIST_MOTOR_INVERTED = false;
        // CARGO_MECH_ARM_SENSOR_INVERTED = false; // TODO Remove
        CARGO_MECH_WRIST_P = 1.0; // TODO
        CARGO_MECH_WRIST_I = 0.0; // TODO
        CARGO_MECH_WRIST_D = 0.0; // TODO
        CARGO_MECH_WRIST_F = 0.0; // TODO

        CARGO_CAMERA_INDEX = 1;

        CARGO_MECH_WRIST_TOP_TICKS = 979; 
        CARGO_MECH_WRIST_BOTTOM_TICKS = 49;
        CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS = 10; // TODO
        CARGO_MECH_WRIST_SENSOR_INVERTED = false;

        CARGO_WRIST_UP_LIMIT_TICKS = 0; // TODO
        CARGO_WRIST_DOWN_LIMIT_TICKS = 0; // TODO

        // Ticks
        CARGO_MECH_CARGO_BIN_PROPORTION = 0.02; // TODO
        CARGO_MECH_LOW_ROCKET_PROPORTION = 0.55; // TODO
        CARGO_MECH_CARGO_SHIP_PROPORTION = 0.67; // TODO
        CARGO_MECH_SAFE_TURRET_PROPORTION = 0.55; // TODO

        CARGO_MECH_CLAW_LEFT_MOTOR_CHANNEL = 2;
        CARGO_MECH_CLAW_LEFT_MOTOR_INVERTED = true;
        CARGO_MECH_CLAW_RIGHT_MOTOR_CHANNEL = 3;
        CARGO_MECH_CLAW_RIGHT_MOTOR_INVERTED = true;

        break;

      case ROBOT_2019:
      default:
        
        HAS_WHEELS = true;
        DRIVEMOTOR_NUM = 4;
        WHEEL_CIRCUMFERENCE = 18.50;

        CONTROLS_INVERTED_FB = true;
        CONTROLS_INVERTED_TURN = false;

        USE_VELOCITY_SPEED_CONTROL_FOR_TELOP = true;
        VELOCITY_MULTIPLIER_RIGHT = 1300;
        VELOCITY_MULTIPLIER_LEFT = 1300;
        NORMAL_DRIVE_SPEED_MULTIPLIER = 0.8;
        SLOW_DRIVE_SPEED_MULTIPLIER = 0.6;

        LEFT_LEAD_CHANNEL = 1;
        LEFT_FOLLOWER_1_CHANNEL = 2;
        LEFT_DRIVE_SENSOR_IS_INVERTED = true;
        LEFT_DRIVE_MOTOR_IS_INVERTED = false;

        RIGHT_LEAD_CHANNEL = 3;
        RIGHT_FOLLOWER_1_CHANNEL = 4;
        RIGHT_DRIVE_SENSOR_IS_INVERTED = true;
        RIGHT_DRIVE_MOTOR_IS_INVERTED = false;

        // Linear PIDS
        LEFT_DRIVE_PID_P = 0.5;
        LEFT_DRIVE_PID_I = 0.0;
        LEFT_DRIVE_PID_D = 0.0;
        LEFT_DRIVE_PID_F = 0.682;

        RIGHT_DRIVE_PID_P = 0.5;
        RIGHT_DRIVE_PID_I = 0.0;
        RIGHT_DRIVE_PID_D = 0.0;
        RIGHT_DRIVE_PID_F = 0.781;

        // Turn PIDs
        LEFT_TURN_PID_P = 0.5;
        LEFT_TURN_PID_I = 0.0;
        LEFT_TURN_PID_D = 450.0;
        LEFT_TURN_PID_F = 0.0;

        RIGHT_TURN_PID_P = 0.5;
        RIGHT_TURN_PID_I = 0.0;
        RIGHT_TURN_PID_D = 450.0;
        RIGHT_TURN_PID_F = 0.0;

        // Cameras
        FORWARD_CAMERA_INDEX = 0;
        BACKWARD_CAMERA_INDEX = 1;
        HAS_CAMERA = false;
        AUTO_CAMERA = false;

        // Game Pieces
        HAS_TURRET = true;
        HAS_ROLLER_INTAKE = true;
        HAS_HATCH_MECHANISM = true;
        HAS_ROLLER_INTAKE = true;
        HAS_CARGO_MECHANISM = true;

        // Turret
        TURRET_MOTOR_CHANNEL = 5;
        TURRET_MOTOR_INVERTED = false;
        TURRET_SENSOR_INVERTED = false;
        TURRET_RIGHT_LIMIT_TICKS = 462;
        TURRET_LEFT_LIMIT_TICKS = 546;
        TURRET_RIGHT_LIMIT_DEGREES = -95.0;
        TURRET_LEFT_LIMIT_DEGREES = 95.0;
        TURRET_ALLOWABLE_ERROR_TICKS = 10;
        TURRET_HOME = 0.0;
        INVERT_TURRET_FOR_HATCHMODE = -1;
        TURRET_SQR_INP = true;

        
        // TODO: Set TURRET_OFFSET
        TURRET_P = 5.0; // TODO
        TURRET_I = 0.0; // TODO
        TURRET_D = 0.0; // TODO
        TURRET_F = 0.0; // TODO

        // Hatch Mechanism
        HATCH_LAUNCHER_PCM_CHANNEL = 8;
        // TODO update forward/reverse channel
        HATCH_LAUNCHER_SOL_FORWARD_CHANNEL = 0;
        HATCH_LAUNCHER_SOL_REVERSE_CHANNEL = 1;
        HATCH_MECH_ARM_PCM_CHANNEL = 9;
        // TODO update forward/reverse channel
        HATCH_MECH_ARM_FORWARD_CHANNEL = 4;
        HATCH_MECH_ARM_REVERSE_CHANNEL = 5;
        HATCH_CAMERA_INDEX = 3;

        // Cargo Intake
        ROLLER_PCM_CHANNEL = 9;
        // TODO update up/down channel
        ROLLER_RIGHT_ARM_UP_SOLINOID_CHANNEL = 0;
        ROLLER_RIGHT_ARM_DOWN_SOLINOID_CHANNEL = 1;
        // unused
        ROLLER_LEFT_ARM_UP_SOLINOID_CHANNEL = 2;
        ROLLER_LEFT_ARM_DOWN_SOLINOID_CHANNEL = 3;
        // TODO use two solenoids
        ROLLER_MOTOR_CHANNEL = 1;
        ROLLER_MOTOR_INVERTED = false; // TODO
        // TODO use two sparks

        // Cargo Mechanism
        CARGO_MECH_WRIST_MOTOR_CHANNEL = 6; // TODO
        CARGO_MECH_WRIST_MOTOR_INVERTED = false;
        // CARGO_MECH_ARM_SENSOR_INVERTED = false; // TODO Remove
        CARGO_MECH_WRIST_P = 1.0; // TODO
        CARGO_MECH_WRIST_I = 0.0; // TODO
        CARGO_MECH_WRIST_D = 0.0; // TODO
        CARGO_MECH_WRIST_F = 0.0; // TODO
        WRIST_SQR_INP = true;

        CARGO_CAMERA_INDEX = 1;

        CARGO_MECH_WRIST_TOP_TICKS = 619; 
        CARGO_MECH_WRIST_BOTTOM_TICKS = 601;
        CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS = 1; // TODO
        CARGO_MECH_WRIST_SENSOR_INVERTED = false;

        CARGO_WRIST_UP_LIMIT_TICKS = 0; // TODO
        CARGO_WRIST_DOWN_LIMIT_TICKS = 0; // TODO

        // Ticks
        CARGO_MECH_CARGO_BIN_PROPORTION = 0.02; // TODO
        CARGO_MECH_LOW_ROCKET_PROPORTION = 0.55; // TODO
        CARGO_MECH_CARGO_SHIP_PROPORTION = 0.67; // TODO
        CARGO_MECH_SAFE_TURRET_PROPORTION = 0.55; // TODO

        CARGO_MECH_CLAW_LEFT_MOTOR_CHANNEL = 2;
        CARGO_MECH_CLAW_LEFT_MOTOR_INVERTED = true;
        CARGO_MECH_CLAW_RIGHT_MOTOR_CHANNEL = 3;
        CARGO_MECH_CLAW_RIGHT_MOTOR_INVERTED = true;

        break;

      case MINIBOT:
        HAS_WHEELS = true;
        DRIVEMOTOR_NUM = 2;
        WHEEL_CIRCUMFERENCE = 18.50;

        LEFT_LEAD_CHANNEL = 1;
        LEFT_DRIVE_SENSOR_IS_INVERTED = false;
        LEFT_DRIVE_MOTOR_IS_INVERTED = false;

        RIGHT_LEAD_CHANNEL = 4;
        RIGHT_DRIVE_SENSOR_IS_INVERTED = false;
        RIGHT_DRIVE_MOTOR_IS_INVERTED = false;

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

        break;

    }

    // These calculations can be made after the robot-specific constants are set.
    POSITION_ALLOWED_ERROR = ALLOWED_ERROR_INCHES / RobotMap.WHEEL_CIRCUMFERENCE;

    // This is in encoder ticks
    POSITION_ALLOWABLE_CLOSED_LOOP_ERROR 
        = (int) (POSITION_ALLOWED_ERROR * WHEEL_ENCODER_CODES_PER_REVOLUTION * 0.95);
  }

  /**
   * Overrides normal values with values required for simulator.
   */
  static void setSimulator() {

    useSimulator = true;

    // Linear PIDS
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

  //Speed Controls
  public static double NORMAL_DRIVE_SPEED_MULTIPLIER;
  public static double SLOW_DRIVE_SPEED_MULTIPLIER;
  public static boolean USE_VELOCITY_SPEED_CONTROL_FOR_TELOP = false;

  // Driver Cameras
  public static int FORWARD_CAMERA_INDEX;
  public static int BACKWARD_CAMERA_INDEX;
  public static boolean HAS_CAMERA = false;
  public static boolean AUTO_CAMERA = false;

  // Telemetry Enables
  public static boolean ENABLE_TELEMETRY = true;
  public static boolean ENABLE_DRIVER_STATION_TELEMETRY = true;
  public static int TELEMETRY_TIMER_MS = 20;

  // Game Pieces

  // Turret
  public static boolean HAS_TURRET = false;
  public static int TURRET_MOTOR_CHANNEL;
  public static boolean TURRET_MOTOR_INVERTED;
  public static int TURRET_SENSOR_CHANNEL;
  public static boolean TURRET_SENSOR_INVERTED;
  public static int TURRET_RIGHT_LIMIT_TICKS;
  public static int TURRET_LEFT_LIMIT_TICKS;
  public static double TURRET_RIGHT_LIMIT_DEGREES;
  public static double TURRET_LEFT_LIMIT_DEGREES;
  public static int TURRET_ALLOWABLE_ERROR_TICKS = 10;
  public static double TURRET_P;
  public static double TURRET_I;
  public static double TURRET_D;
  public static double TURRET_F;
  public static double TURRET_HOME;
  public static int TURRET_OFFSET;

  // Hatch Mechanism
  public static boolean HAS_HATCH_MECHANISM;
  public static int HATCH_CAMERA_INDEX;
  public static int HATCH_LAUNCHER_SOL_FORWARD_CHANNEL;
  public static int HATCH_LAUNCHER_SOL_REVERSE_CHANNEL;
  public static int HATCH_LAUNCHER_S2_FORWARD_CHANNEL;
  public static int HATCH_LAUNCHER_S2_REVERSE_CHANNEL;
  public static int HATCH_LAUNCHER_S3_FORWARD_CHANNEL;
  public static int HATCH_LAUNCHER_S3_REVERSE_CHANNEL;
  public static int HATCH_MECH_ARM_FORWARD_CHANNEL;
  public static int HATCH_MECH_ARM_REVERSE_CHANNEL;
  public static int HATCH_LAUNCHER_PCM_CHANNEL = 0;
  public static int HATCH_MECH_ARM_PCM_CHANNEL = 0;

  // Cargo Intake aka Roller
  public static boolean HAS_ROLLER_INTAKE = false;
  public static int ROLLER_RIGHT_ARM_UP_SOLINOID_CHANNEL;
  public static int ROLLER_RIGHT_ARM_DOWN_SOLINOID_CHANNEL;
  public static int ROLLER_LEFT_ARM_UP_SOLINOID_CHANNEL;
  public static int ROLLER_LEFT_ARM_DOWN_SOLINOID_CHANNEL;
  public static int ROLLER_MOTOR_CHANNEL;
  public static boolean ROLLER_MOTOR_INVERTED;
  public static int ROLLER_PCM_CHANNEL;

  // Cargo Mechanism
  public static boolean HAS_CARGO_MECHANISM = false;
  public static int CARGO_CAMERA_INDEX;
  public static int CARGO_MECH_WRIST_MOTOR_CHANNEL;
  public static boolean CARGO_MECH_WRIST_MOTOR_INVERTED;
  public static boolean CARGO_MECH_WRIST_SENSOR_INVERTED;
  public static double CARGO_MECH_WRIST_P;
  public static double CARGO_MECH_WRIST_I;
  public static double CARGO_MECH_WRIST_D;
  public static double CARGO_MECH_WRIST_F;

  public static int CARGO_MECH_WRIST_TOP_TICKS;
  public static int CARGO_MECH_WRIST_BOTTOM_TICKS;
  public static int CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS = 10;

  public static int CARGO_WRIST_UP_LIMIT_TICKS;
  public static int CARGO_WRIST_DOWN_LIMIT_TICKS;

  // Relative heights
  public static double CARGO_MECH_CARGO_BIN_PROPORTION;
  public static double CARGO_MECH_LOW_ROCKET_PROPORTION;
  public static double CARGO_MECH_CARGO_SHIP_PROPORTION;
  public static double CARGO_MECH_SAFE_TURRET_PROPORTION;

  public static int CARGO_MECH_CLAW_LEFT_MOTOR_CHANNEL;
  public static boolean CARGO_MECH_CLAW_LEFT_MOTOR_INVERTED;
  public static int CARGO_MECH_CLAW_RIGHT_MOTOR_CHANNEL;
  public static boolean CARGO_MECH_CLAW_RIGHT_MOTOR_INVERTED;

  public static boolean CONTROLS_INVERTED_FB;
  public static boolean CONTROLS_INVERTED_TURN;
  public static int INVERT_TURRET_FOR_HATCHMODE; //1 or -1
  public static double VELOCITY_MULTIPLIER_RIGHT;
  public static double VELOCITY_MULTIPLIER_LEFT;
  public static boolean WRIST_SQR_INP;
  public static boolean TURRET_SQR_INP;
}