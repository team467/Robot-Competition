package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.Spark;
import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoWrist extends GamePieceBase implements GamePiece {

  private static CargoWrist instance = null; // set to null

  private static final Logger LOGGER = RobotLogManager.getMainLogger(CargoWrist.class.getName());

  // Actuators
  private CargoWristControlStates wrist; // stores desired height

  // State
  private CargoMechWristState wristState;
  private static boolean onManualControl = true;

  private static final int TALON_SENSOR_ID = 0;
  private static final int TALON_PID_SLOT_ID = 0;
  private static CargoMechWristState previousState;
  private static double simulatedReading = 0.0;
  private static double height = 0.0;
  private static WpiTalonSrxInterface talon;
  
  private CargoWrist() {
    super("Telemetry","CargoWrist");
    if (RobotMap.HAS_CARGO_MECHANISM) {
      talon = TalonProxy.create(RobotMap.CARGO_MECH_WRIST_MOTOR_CHANNEL);
      talon.setName("Telemetry", "Cargo Wrist Motor");
      talon.setInverted(RobotMap.CARGO_MECH_WRIST_MOTOR_INVERTED);
      talon.setSensorPhase(RobotMap.CARGO_MECH_WRIST_SENSOR_INVERTED);
      talon.configSelectedFeedbackSensor(FeedbackDevice.Analog, TALON_PID_SLOT_ID, 
      RobotMap.TALON_TIMEOUT);
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
      talon.configAllowableClosedloopError(TALON_PID_SLOT_ID, RobotMap.CARGO_MECH_WRIST_ALLOWABLE_ERROR_TICKS, RobotMap.TALON_TIMEOUT);
      talon.config_kP(TALON_PID_SLOT_ID, kP, RobotMap.TALON_TIMEOUT);
      talon.config_kI(TALON_PID_SLOT_ID, kI, RobotMap.TALON_TIMEOUT);
      talon.config_kD(TALON_PID_SLOT_ID, kD, RobotMap.TALON_TIMEOUT);
    } else {
      talon = null;
    }
  }

  public enum CargoWristControlStates {

    // height values measured empirically
    CARGO_BIN(RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION), 
    LOW_ROCKET(RobotMap.CARGO_MECH_LOW_ROCKET_PROPORTION),
    CARGO_SHIP(RobotMap.CARGO_MECH_CARGO_SHIP_PROPORTION), 
    SAFE_TURRET(RobotMap.CARGO_MECH_SAFE_TURRET_PROPORTION);

    private static void manual(double speed) {
      if (RobotMap.HAS_CARGO_MECHANISM) {
        onManualControl = true;
        talon.set(ControlMode.PercentOutput, speed);
        LOGGER.debug("Manual override cargo mech wrist Speed: {}, Channel: {}, talon speed = {}, Control mode: {}",
            box(speed), box(talon.getDeviceID()), box(talon.getMotorOutputPercent()), talon.getControlMode());
      }
    }


    public static int cargoMechWristTickValueIn() {
      return talon.getSensorCollection().getAnalogIn();
    }

    public static int cargoMechWristTickValueInRaw() {
      return talon.getSensorCollection().getAnalogInRaw();
    }

    // 0.0 is the bottom, 1.0 is the top (proportion)
    // takes proportion and converts it to ticks
    public static void tuneMove(double heightProportion) {
      if (heightProportion > 1.0 || heightProportion < 0.0) {
        LOGGER.warn("Tune move needs a number between 0.0 to 1.0");
        return;
      }

      talon.set(ControlMode.Position, heightTicksFromProportion(heightProportion));

      LOGGER.debug(" Height proportion: {}, Height set on wrist: {}, Sensor: {}, Error: {}",
          box(heightProportion), box(heightTicksFromProportion(heightProportion)), 
          box(talon.getSensorCollection().getAnalogIn()), box(talon.getClosedLoopError(0)));
    }

    /**
     * Moves the wrist based on the requested command.
     */
  }

  private double proportionalheight(double heightProportion) {
    double height;
    height =  ((heightProportion) * RobotMap.CARGO_MECH_WRIST_TOP_TICKS + (1.0 - heightProportion) * RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS);
    LOGGER.debug("Setting wrist height for {} to {} ticks", this, box(height));
    return height;
  }

  private void actuate() {
    LOGGER.debug("Actuating cargo mechanism wrist: {}", this);
    if (RobotMap.HAS_CARGO_MECHANISM) {
      if (!onManualControl) {
        talon.set(ControlMode.Position, height);
        if (!RobotMap.useSimulator) { // No sensor collection capability in the talon simulator
          LOGGER.debug("Height set on wrist: {}, Sensor: {}, Error: {}",
              box(height), box(talon.getSensorCollection().getAnalogIn()), box(talon.getClosedLoopError(0)));
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
            box(motorLeader.getChannel()), box(motorFollower.getChannel()));
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
  public static CargoWrist getInstance() {
    if (instance == null) {
      instance = new CargoWrist();
    }
    return instance;
  }

  // private CargoWrist() {
  //   super("Telemetry", "CargoMech");
  //   // Initialize the sensors and actuators
  //   CargoMechWrist.initialize();
  //   CargoMechClaw.initialize();

  //   claw = CargoMechClaw.STOP;
  //   wrist = CargoMechWrist.CARGO_BIN;
  //   wristState = CargoMechWristState.read();

  //   registerMetrics();
  //   LOGGER.trace("Created Ball Mech game piece.");
  // }

  /**
   * Checks to see if the cargo mechanism wrist at or above a safe distance to turn
   * the turret.
   * 
   * @return boolean true if safe to turn.
   */
  public boolean isSafeToMoveTurret() {
    LOGGER.debug("Wrist is at {}, vs safe turret height of {}",
        box(CargoMechWristState.height), box(CargoWristControlStates.SAFE_TURRET.height));
    if (CargoMechWristState.height >= CargoWristControlStates.SAFE_TURRET.height) {
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
  public void wrist(CargoWristControlStates command) {
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
    wrist = CargoWristControlStates.valueOf(command);
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
    CargoWristControlStates.manual(speed);
  }

  public void configWristPid(double kP, double kI, double kD) {
     CargoWristControlStates.configPid(kP, kI, kD);
  }

  public void tuneMove(double heightProportion) {
    CargoWristControlStates.tuneMove(heightProportion);
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

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addStringMetric("Cargo Claw Command", this::clawCommandString);
    telemetry.addStringMetric("Cargo Wrist Command", this::wristCommandString);
    if (RobotMap.HAS_CARGO_MECHANISM) {
      telemetry.addDoubleMetric("Cargo Claw Lead Motor Output", this::clawLeaderMotorOutput);
      telemetry.addDoubleMetric("Cargo Claw Follower Motor Output", this::clawFollowerMotorOutput);
      telemetry.addStringMetric("Cargo Wrist State", this::wristStateString);
      telemetry.addDoubleMetric("Cargo Wrist Height Proportion", this::heightProportion);
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

  @Override
  public boolean checksystem() {
    return false;
  }


}