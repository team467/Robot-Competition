package frc.robot.gamepieces;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class CargoMech extends GamePieceBase implements GamePiece {

  private static CargoMech instance = null; // set to null

  private static final Logger LOGGER = RobotLogManager.getMainLogger(CargoMech.class.getName());

  // Actuators
  private CargoMechClaw claw;
  private CargoMechWrist wrist; // stores desired height

  // State
  private CargoMechWristState wristState;
  private static boolean onManualControl = true;

  private static final int TALON_SENSOR_ID = 0;
  private static final int TALON_PID_SLOT_ID = 0;

  public enum CargoMechWrist {

    // height values measured empirically
    CARGO_BIN(RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION), 
    LOW_ROCKET(RobotMap.CARGO_MECH_LOW_ROCKET_PROPORTION),
    CARGO_SHIP(RobotMap.CARGO_MECH_CARGO_SHIP_PROPORTION), 
    SAFE_TURRET(RobotMap.CARGO_MECH_SAFE_TURRET_PROPORTION);

    // Height in sensor units
    public double height;
    private static WpiTalonSrxInterface talon;

    private CargoMechWrist(double heightProportion) {
      height = heightTicksFromProportion(heightProportion);
      LOGGER.debug("Setting wrist height for {} to {} ticks", this, height);
    }

    private static void initialize() {
      if (RobotMap.HAS_CARGO_MECHANISM) {
        talon = TalonProxy.create(RobotMap.CARGO_MECH_WRIST_MOTOR_CHANNEL);
        talon.setName("Telemetry", "Cargo Wrist Motor");
        talon.setInverted(RobotMap.CARGO_MECH_WRIST_MOTOR_INVERTED);
        talon.setSensorPhase(RobotMap.CARGO_MECH_WRIST_SENSOR_INVERTED);
        talon.selectProfileSlot(TALON_PID_SLOT_ID, TALON_SENSOR_ID);
        talon.config_kP(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_P, RobotMap.TALON_TIMEOUT);
        talon.config_kI(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_I, RobotMap.TALON_TIMEOUT);
        talon.config_kD(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_D, RobotMap.TALON_TIMEOUT);
        talon.config_kF(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_F, RobotMap.TALON_TIMEOUT);
        // talon.configForwardSoftLimitThreshold(
        //     RobotMap.CARGO_WRIST_UP_LIMIT_TICKS, RobotMap.TALON_TIMEOUT);
        // talon.configReverseSoftLimitThreshold(
        //     RobotMap.CARGO_WRIST_DOWN_LIMIT_TICKS, RobotMap.TALON_TIMEOUT);
        talon.configForwardSoftLimitEnable(false, RobotMap.TALON_TIMEOUT);
        talon.configReverseSoftLimitEnable(false, RobotMap.TALON_TIMEOUT);
        talon.configAllowableClosedloopError(TALON_PID_SLOT_ID,
            RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS, RobotMap.TALON_TIMEOUT);
      } else {
        talon = null;
      }
    }

    // 0.0 is the bottom, 1.0 is the top (proportion)
    // takes proportion and converts it to ticks
    static double heightTicksFromProportion(double proportion) {
      return ((proportion) * RobotMap.CARGO_MECH_WRIST_TOP_TICKS
          + (1.0 - proportion) * RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS);
    }

    private static void manual(double speed) {
      if (RobotMap.HAS_CARGO_MECHANISM) {
        onManualControl = true;
        talon.set(ControlMode.PercentOutput, speed);
        LOGGER.debug("Manual override cargo mech wrist Speed: {}, Channel: {}, talon speed = {}, Control mode: {}",
            speed, talon.getDeviceID(), talon.getMotorOutputPercent(), talon.getControlMode());
      }
    }

    public static void configPid(double kP, double kI, double kD) {
      talon.config_kP(TALON_PID_SLOT_ID, kP, RobotMap.TALON_TIMEOUT);
      talon.config_kI(TALON_PID_SLOT_ID, kI, RobotMap.TALON_TIMEOUT);
      talon.config_kD(TALON_PID_SLOT_ID, kD, RobotMap.TALON_TIMEOUT);
    }

    public static int cargoMechWristTickValueIn() {
      return talon.getSensorCollection().getAnalogIn();
    }

    public static int cargoMechWristTickValueInRaw() {
      return talon.getSensorCollection().getAnalogInRaw();
    }

    public static void tuneMove(double heightProportion) {
      if (heightProportion > 1.0 || heightProportion < 0.0) {
        LOGGER.warn("Tune move needs a number between 0.0 to 1.0");
        return;
      }

      talon.set(ControlMode.Position, heightTicksFromProportion(heightProportion));

      LOGGER.debug(" Height proportion: {}, Height set on wrist: {}, Sensor: {}, Error: {}",
          heightProportion, heightTicksFromProportion(heightProportion), 
          talon.getSensorCollection().getAnalogIn(), talon.getClosedLoopError(0));
    }

    /**
     * Moves the wrist based on the requested command.
     */
    private void actuate() {
      LOGGER.debug("Actuating cargo mechanism wrist: {}", this);
      if (RobotMap.HAS_CARGO_MECHANISM) {
        if (!onManualControl) {
          talon.set(ControlMode.Position, height);
          if (!RobotMap.useSimulator) { // No sensor collection capability in the talon simulator
            LOGGER.debug("Height set on wrist: {}, Sensor: {}, Error: {}",
                height, talon.getSensorCollection().getAnalogIn(), talon.getClosedLoopError(0));
          }
        }
      } 
    }
  }

  public enum CargoMechWristState {
    CARGO_BIN, 
    MOVING_DOWN_TO_CARGO_BIN, 
    MOVING_UP_TO_LOW_ROCKET, 
    LOW_ROCKET,
    MOVING_DOWN_TO_LOW_ROCKET,
    MOVING_UP_TO_CARGO_SHIP, 
    CARGO_SHIP,
    ABOVE_CARGO_SHIP,
    UNKNOWN;

    private static CargoMechWristState previousState = UNKNOWN;
    private static double simulatedReading = 0.0;
    private static double height = 0.0;

    private static CargoMechWristState read() {
      height = simulatedReading;
      if (!RobotMap.useSimulator && RobotMap.HAS_CARGO_MECHANISM) {
        height = CargoMechWrist.talon.getSensorCollection().getAnalogInRaw();
      }
      height *= (RobotMap.CARGO_MECH_WRIST_SENSOR_INVERTED) ? -1.0 : 1.0;
      LOGGER.debug("Read cargo wrist height as {}.", height);

      CargoMechWristState state;

      if (Math.abs(height - CargoMechWrist.CARGO_BIN.height) 
          <= RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS) {
        state = CARGO_BIN;
      } else if (Math.abs(height - CargoMechWrist.LOW_ROCKET.height) 
          <= RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS) {
        state = LOW_ROCKET;
      } else if (Math.abs(height - CargoMechWrist.CARGO_SHIP.height) 
          <= RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS) {
        state = CARGO_SHIP;
      } else if (height > (CargoMechWrist.CARGO_SHIP.height 
          + RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS)) {
        state = ABOVE_CARGO_SHIP;
      } else if (height > CargoMechWrist.CARGO_BIN.height
          && height < (CargoMechWrist.LOW_ROCKET.height 
            - RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS)) {
        if (previousState == CARGO_BIN || previousState == MOVING_UP_TO_LOW_ROCKET) {
          state = MOVING_UP_TO_LOW_ROCKET;
        } else {
          state = MOVING_DOWN_TO_CARGO_BIN;
        }
      } else if (height > CargoMechWrist.LOW_ROCKET.height
          && height < (CargoMechWrist.CARGO_SHIP.height 
            - RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS)) {
        if (previousState == LOW_ROCKET || previousState == MOVING_UP_TO_CARGO_SHIP) {
          state = MOVING_UP_TO_CARGO_SHIP;
        } else {
          state = MOVING_DOWN_TO_LOW_ROCKET;
        }
      } else {
        state = UNKNOWN;
      }
      LOGGER.debug("Current state: {} at height in ticks {}", state.name(), height);
      previousState = state;
      return state;
    }
  }

  /**
   * Forward means the roller is spinning inwards, essentially pulling balls in.
   * Reverse means the roller is spinning outwards not letting the ball in.
   * 
   */

  public enum CargoMechClaw {
    FIRE, INTAKE, STOP;

    private static Spark motorLeader;
    private static Spark motorFollower;

    private static void initialize() {
      // Create the roller object. No sensors
      LOGGER.trace("Initializing Claw");
      if (RobotMap.HAS_CARGO_MECHANISM) {
        motorLeader = new Spark(RobotMap.CARGO_MECH_CLAW_LEFT_MOTOR_CHANNEL);
        motorLeader.setInverted(RobotMap.CARGO_MECH_CLAW_LEFT_MOTOR_INVERTED);
        
        motorFollower = new Spark(RobotMap.CARGO_MECH_CLAW_RIGHT_MOTOR_CHANNEL);
        motorFollower.setInverted(RobotMap.CARGO_MECH_CLAW_RIGHT_MOTOR_INVERTED);
        LOGGER.debug("Spark channels: {}, {}", 
            motorLeader.getChannel(), motorFollower.getChannel());
      }

    }

    /**
     * Moves the belts of the claw forward or backward based on the requested
     * command.
     */
    private void actuate() {
      LOGGER.debug("Actuating cargo mech claw");
      
      LOGGER.debug("Calling Claw Actuate state: {}", this);
      if (RobotMap.HAS_CARGO_MECHANISM) {
        switch (this) {
          case FIRE:
              motorLeader.set(1.0);
              motorFollower.set(1.0);
            LOGGER.debug("Claw going forward");
            break;

          case INTAKE:
              motorLeader.set(-1.0);
              motorFollower.set(-1.0);
            LOGGER.debug("Claw going backward");
            break;

          case STOP:
          default:
              motorLeader.set(0.0);
              motorFollower.set(0.0);
            LOGGER.debug("Claw is stopping");
        }
      }
    }

  }

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */
  public static CargoMech getInstance() {
    if (instance == null) {
      instance = new CargoMech();
    }
    return instance;
  }

  private CargoMech() {
    super("Telemetry", "CargoMech");
    // Initialize the sensors and actuators
    CargoMechWrist.initialize();
    CargoMechClaw.initialize();

    claw = CargoMechClaw.STOP;
    wrist = CargoMechWrist.CARGO_BIN;
    wristState = CargoMechWristState.read();

    initSendable(TelemetryBuilder.getInstance());
    LOGGER.trace("Created Ball Mech game piece.");
  }

  /**
   * Checks to see if the cargo mechanism wrist at or above a safe distance to turn
   * the turret.
   * 
   * @return boolean true if safe to turn.
   */
  public boolean isSafeToMoveTurret() {
    LOGGER.debug("Wrist is at {}, vs safe turret height of {}",
        CargoMechWristState.height, CargoMechWrist.SAFE_TURRET.height);
    if (CargoMechWristState.height >= CargoMechWrist.SAFE_TURRET.height) {
      LOGGER.debug("Wrist in safe location to move turret.");
      return true;
    } else {
      LOGGER.debug("Wrist NOT in safe location to move turret.");
      return false;
    }
  }

  /**
   * Moves the claw wrist up or down.
   * 
   * @param command which way to move the wrist.
   */
  public void wrist(CargoMechWrist command) {
    LOGGER.debug("Moving cargo mechanism wrist to {}", command);
    onManualControl = false;
    wrist = command;
  }

  /**
   * Moves the claw wrist up or down. The String version sets the command from the
   * Smart Dashboard.
   * 
   * @param command which way to move the wrist.
   */
  private void wrist(String command) {
    LOGGER.debug("Moving cargo mechanism wrist to {} using string interface.", command);
    onManualControl = false;
    wrist = CargoMechWrist.valueOf(command);
  }

  /**
   * Reads the wrist state from the sensors.
   * 
   * @return the state of the wrist, including if unknown or moving.
   */
  public CargoMechWristState wrist() {
    LOGGER.debug("Cargo wrist state is {}", wristState);
    return wristState;
  }

  /**
   * Sets the belt based on the given command.
   * 
   * @param command the belt command
   */
  public void claw(CargoMechClaw command) {
    LOGGER.debug("Cargo mechanism claw command is {}", command);
    claw = command;
  }

  /**
   * Sets the belt based on the given command. The String version is used for
   * setting the command from the SmartDashboard.
   * 
   * @param command the belt command
   */
  private void claw(String command) {
    LOGGER.debug("Cargo mechanism claw command is {} using string interface", command);
    claw = CargoMechClaw.valueOf(command);
  }

  /**
   * Returns the current belt command. There is no external sensor on this motor.
   */
  public CargoMechClaw claw() {
    LOGGER.debug("Current cargo mechanism claw command is {}", claw);
    return claw;
  }

  public void manualWristMove(double speed) {
    onManualControl = true;
    CargoMechWrist.manual(speed);
  }

  public void configWristPid(double kP, double kI, double kD) {
     CargoMechWrist.configPid(kP, kI, kD);
  }

  public void tuneMove(double heightProportion) {
    CargoMechWrist.tuneMove(heightProportion);
  }
  /**
   * Tries to handle commands and update state.
   */
  public void periodic() { // In progress
    // Take Actions
    if (enabled) {
      claw.actuate();
      if (!onManualControl) {
        wrist.actuate();
      }
    } else {
      LOGGER.debug("Hatch mechanism is disabled.");
    }

    // Update state
    wristState = CargoMechWristState.read();
  }

  static void simulatedSensorData(double reading) {
    CargoMechWristState.simulatedReading = reading;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("Cargo Claw Command", 
        this::clawCommandString, (command) -> claw(command));
    builder.addStringProperty("Cargo Wrist Command", 
        this::wristCommandString, (command) -> wrist(command));
    if (RobotMap.HAS_CARGO_MECHANISM) {
      builder.addDoubleProperty("Cargo Claw Lead Motor Output", this::clawLeaderMotorOutput, null);
      builder.addDoubleProperty("Cargo Claw Follower Motor Output", 
          this::clawFollowerMotorOutput, null);
      builder.addStringProperty("Cargo Wrist State", this::wristStateString, null);
      builder.addDoubleProperty("Cargo Wrist Height Proportion", this::heightProportion, null);
    }
  }

  private double heightProportion() {
    return (wrist.height - RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS)
        / (RobotMap.CARGO_MECH_WRIST_TOP_TICKS - RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS);
  }

  private String wristStateString() {
    return wristState.toString();
  }

  private String wristCommandString() {
    return wrist.toString();
  }

  private String clawCommandString() {
    return claw.toString();
  }

  private double clawLeaderMotorOutput() {
    return CargoMechClaw.motorLeader.get();
  }

  private double clawFollowerMotorOutput() {
    return CargoMechClaw.motorFollower.get();
  }


}