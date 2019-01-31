package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DigitalGlitchFilter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class CargoIntake extends GamePieceBase implements GamePieceInterface {
  
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(CargoIntake.class.getName());

  private static CargoIntake instance = null;

  // Actuators
  private Roller roller;
  private RollerArm arm;

  // State
  private RollerArmState armState;

  //TODO: Move to Robot Map
  public static int ROLLER_ARM_UP_SENSOR_CHANNEL = 0;
  public static int ROLLER_ARM_UP_SOLINOID_CHANNEL = 0;
  public static int ROLLER_ARM_DOWN_SENSOR_CHANNEL = 0;
  public static int ROLLER_ARM_DOWN_SOLINOID_CHANNEL = 0;
  public static int ROLLER_MOTOR_CHANNEL = 0;
  public static boolean ROLLER_MOTOR_INVERTED = false;

  public enum RollerArm {
    OFF,
    UP,
    DOWN;

    private static DoubleSolenoid arm;

    private static void initialize() {
      arm = new DoubleSolenoid(ROLLER_ARM_UP_SOLINOID_CHANNEL, ROLLER_ARM_DOWN_SOLINOID_CHANNEL);
    }

    /**
     * Moves the arm based on the requested command.
     */
    private void actuate() {
      switch (this) {
        case DOWN:
          arm.set(DoubleSolenoid.Value.kReverse);
          break;
        case UP:
          arm.set(DoubleSolenoid.Value.kForward);
          break;
        default:
          arm.set(DoubleSolenoid.Value.kOff);
      }
    }

  }

  public enum RollerArmState {
    UP,
    MOVING_DOWN,
    DOWN,
    MOVING_UP,
    UNKNOWN;
    
    private static DigitalInput armUp;
    private static DigitalInput armDown;
    private static DigitalGlitchFilter glitchFilter;
    private static RollerArmState previousState;
  
    private static void initialize() {
      // Config arm sensors
      armUp = new DigitalInput(ROLLER_ARM_UP_SENSOR_CHANNEL);
      armUp.setName("Telemetry", "RollerArmUp");
      armDown = new DigitalInput(ROLLER_ARM_DOWN_SENSOR_CHANNEL);
      armDown.setName("Telemetry", "RollerArmDown");

      // Set the time the input must remain steady for a valid state change
      glitchFilter = new DigitalGlitchFilter();
      glitchFilter.setPeriodNanoSeconds(10);
      glitchFilter.add(armUp);
      glitchFilter.add(armDown);
    }

    private static RollerArmState read() {
      RollerArmState state;
      if (armUp.get()) {
        state = UP;
      } else if (armDown.get()) {
        state = DOWN;
      } else if (previousState == UP || previousState == MOVING_DOWN) {
        state = MOVING_DOWN;
      } else if (previousState == DOWN || previousState == MOVING_UP) {
        state = MOVING_UP;
      } else {
        state = UNKNOWN;
      }
      previousState = state;
      return state;
    }

  }

  public enum Roller {
    FORWARD,
    STOP,
    REVERSE;

    private static SpeedController roller;

    private static void initialize() {
      // Create the roller object. No sensors
      roller = new Spark(ROLLER_MOTOR_CHANNEL);
      roller.setInverted(ROLLER_MOTOR_INVERTED);
    }

    /**
     * Moves the roller forward or backward based on the requested command.
     */
    private void actuate() {
      switch (this) {

        case FORWARD:
          roller.set(1.0);
          break;
        
        case REVERSE:
          roller.set(-1.0);
          break;

        case STOP:
        default:
          roller.set(0.0);
      }
      
    }

  }

  /**
  * Returns a singleton instance of the telemery builder.
  * 
  * @return TelemetryBuilder the telemetry builder instance
  */
  public static CargoIntake getInstance() {
    if (instance == null) {
      instance = new CargoIntake();
    }
    return instance;
  }

  private CargoIntake() {
    super("Telemetry", "CargoIntake");

    // Initialize the sensors and actuators
    Roller.initialize();
    RollerArm.initialize();
    RollerArmState.initialize();

    roller = Roller.STOP;
    arm = RollerArm.UP;
    armState = RollerArmState.read();
    
    LOGGER.trace("Created roller arm game piece.");
  }

  /**
   * Moves the roller arm up or down.
   * 
   * @param command which way to move the arm.
   */
  public void arm(RollerArm command) {
    arm = command;
  }

  /**
   * Moves the roller arm up or down. The String version sets the 
   * command from the Smart Dashboard.
   * 
   * @param command which way to move the arm.
   */
  public void arm(String command) {
    arm = RollerArm.valueOf(command);
  }

  /**
   * Reads the arm state from the sensors.
   * 
   * @return the state of the arm, including if unknown or moving.
   */
  public RollerArmState arm() {
    return armState;
  }

  /**
   * Sets the roller based on the given command.
   * 
   * @param command the roller command
   */
  public void roller(Roller command) {
    roller = command;
  }

  /**
   * Sets the roller based on the given command. The String version
   * is used for setting the command from the SmartDashboard.
   * 
   * @param command the roller command
   */
  public void roller(String command) {
    roller = Roller.valueOf(command);
  }

  /**
   * Returns the current rollar command. There is no external sensor on this motor.
   */
  public Roller roller() {
    return roller;
  }

  /**
   * Tries to handle commands and update state.
   */
  public void periodic() {
    // Take Actions
    if (enabled) {
      roller.actuate();
      arm.actuate();
    }
    // Update state
    armState = RollerArmState.read();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("Roller", roller::name, (command) -> roller(command));
    builder.addStringProperty("RollerArm", arm::name, (command) -> arm(command));
    builder.addStringProperty("RollerArmState", armState::name, null);
  }

}