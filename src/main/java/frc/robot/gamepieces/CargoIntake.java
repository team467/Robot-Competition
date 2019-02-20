package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class CargoIntake extends GamePieceBase implements GamePiece {
  
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(CargoIntake.class.getName());

  private static CargoIntake instance = null;

  // Actuators
  private CargoIntakeRoller roller;
  private CargoIntakeArm arm;

  //State 
  private CargoIntakeArmState armState;

  public enum CargoIntakeArm {
    OFF,
    UP,
    DOWN;

    private static DoubleSolenoid leftSolenoid;
    private static DoubleSolenoid rightSolenoid;

    private static void initialize() {

      if (!RobotMap.useSimulator && RobotMap.HAS_ROLLER_INTAKE) {
        leftSolenoid = new DoubleSolenoid(RobotMap.ROLLER_PCM_CHANNEL,
            RobotMap.ROLLER_LEFT_ARM_UP_SOLINOID_CHANNEL, 
            RobotMap.ROLLER_LEFT_ARM_DOWN_SOLINOID_CHANNEL);
        leftSolenoid.setName("Telemetry", "RollerArmLeftSolenoid");
        rightSolenoid = new DoubleSolenoid(RobotMap.ROLLER_PCM_CHANNEL,
            RobotMap.ROLLER_RIGHT_ARM_UP_SOLINOID_CHANNEL, 
            RobotMap.ROLLER_RIGHT_ARM_DOWN_SOLINOID_CHANNEL);
        rightSolenoid.setName("Telemetry", "RollerArmRightSolenoid");
      }
    }
  

    /**
     * Moves the arm based on the requested command.
     */
    private void actuate() {
      
      if (RobotMap.useSimulator || !RobotMap.HAS_ROLLER_INTAKE) {
        return;
      }
      LOGGER.error("Actuate cargo intake arm: {}", this);
      switch (this) {
        case DOWN:
          if (RobotMap.HAS_ROLLER_INTAKE) {
            leftSolenoid.set(DoubleSolenoid.Value.kReverse);
            rightSolenoid.set(DoubleSolenoid.Value.kReverse);
          }
          break;
        case UP:
          if (RobotMap.HAS_ROLLER_INTAKE) {
            leftSolenoid.set(DoubleSolenoid.Value.kForward);
            rightSolenoid.set(DoubleSolenoid.Value.kForward);
          }
          break;
        default:
          if (RobotMap.HAS_ROLLER_INTAKE) {
            leftSolenoid.set(DoubleSolenoid.Value.kOff);
            rightSolenoid.set(DoubleSolenoid.Value.kOff);
          }
      }
    }

  }

  public enum CargoIntakeRoller {
    FORWARD,
    STOP,
    REVERSE;

    private static Spark motor;

    private static void initialize() {
      if (RobotMap.HAS_ROLLER_INTAKE) {
        motor = new Spark(RobotMap.ROLLER_MOTOR_CHANNEL);
        motor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
        motor.setName("Telemetry", "CargoIntakeRollerMotor");
      }
    }

    /**
     * Moves the roller forward or backward based on the requested command.
     */
    private void actuate() {
      if (RobotMap.useSimulator) {
        return;
      }

      LOGGER.error("Actuate cargo intake roller: {}", this);
      switch (this) {

        case FORWARD:
          if (RobotMap.HAS_ROLLER_INTAKE) {
            motor.set(1.0);
          }
          break;
        
        case REVERSE:
          if (RobotMap.HAS_ROLLER_INTAKE) {
            motor.set(-1.0);
          }
          break;

        case STOP:
        default:
          if (RobotMap.HAS_ROLLER_INTAKE) {
            motor.set(0.0);
          }
      }
      
    }

  }

  public enum CargoIntakeArmState {
    UP,
    MOVING_UP,
    DOWN,
    MOVING_DOWN,
    UNKNOWN;

    private static DigitalInput rollerSwitchUp;
    private static DigitalInput rollerSwitchDown;
    private static CargoIntakeArmState state;
    private static CargoIntakeArmState previousState;

    private static void initialize() {
  
    if (RobotMap.HAS_ROLLER_INTAKE) {
      rollerSwitchUp = new DigitalInput(0);//TODO:figure out the channels
      rollerSwitchUp.setName("Telemetry", "RollerSwitchUp");
      rollerSwitchDown = new DigitalInput(1);//TODO: Figure out the channels
      rollerSwitchDown.setName("Telemetry", "RollerswitchDown");
    }
    }

    private static boolean rollerSwitchUp() {
      if (RobotMap.HAS_ROLLER_INTAKE) {
        return rollerSwitchUp.get();
      } else {
        return false;
      }
    }

    private static boolean rollerSwitchDown() {
      if (RobotMap.HAS_ROLLER_INTAKE) {
        return rollerSwitchDown.get();
      } else {
        return false;
      }
    }

    private static CargoIntakeArmState read() {
      // If no cargo intake, state will remain as unknown.
      if (rollerSwitchDown()) {
        state = CargoIntakeArmState.DOWN;
      } else if (rollerSwitchUp()) {
        state = CargoIntakeArmState.UP;
      } else if (previousState == CargoIntakeArmState.UP
          || previousState == CargoIntakeArmState.MOVING_DOWN) {
        state = CargoIntakeArmState.MOVING_DOWN;
      } else if (previousState == CargoIntakeArmState.DOWN 
          || previousState == CargoIntakeArmState.MOVING_UP) {
        state = CargoIntakeArmState.MOVING_UP;
      } else {
        state = CargoIntakeArmState.UNKNOWN;
      }
      previousState = state;
      return state;
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

    // Initialize the actuators
    CargoIntakeRoller.initialize();
    CargoIntakeArm.initialize();
    CargoIntakeArmState.initialize();

    roller = CargoIntakeRoller.STOP;
    arm = CargoIntakeArm.UP;
    armState = CargoIntakeArmState.read();

    initSendable(TelemetryBuilder.getInstance());
    LOGGER.trace("Created roller arm game piece.");


  }

  /**
   * Moves the roller arm up or down.
   * 
   * @param command which way to move the arm.
   */
  public void arm(CargoIntakeArm command) {
    arm = command;
  }

  /**
   * Moves the roller arm up or down. The String version sets the 
   * command from the Smart Dashboard.
   * 
   * @param command which way to move the arm.
   */
  public void arm(String command) {
    arm = CargoIntakeArm.valueOf(command);
  }

  /**
   * Gets the current state of the cargo intake arm.
   * 
   * @return the current state.
   */
  public CargoIntakeArmState arm() {
    return armState;
  }

  /**
   * Gets the last command given to the cargo intake arm.
   * 
   * @return the cargo intake command
   */
  public CargoIntakeArm armCommand() {
    return arm;
  }

  /**
   * Sets the roller based on the given command.
   * 
   * @param command the roller command
   */
  public void roller(CargoIntakeRoller command) {
    roller = command;
  }

  /**
   * Sets the roller based on the given command. The String version
   * is used for setting the command from the SmartDashboard.
   * 
   * @param command the roller command
   */
  public void roller(String command) {
    roller = CargoIntakeRoller.valueOf(command);
  }

  /**
   * Returns the current rollar command. There is no external sensor on this motor.
   */
  public CargoIntakeRoller roller() {
    return roller;
  }

  /**
   * Tries to handle commands and update state.
   */
  public void periodic() {
    // Take Actions
    if (true) {
      roller.actuate();
      arm.actuate();
    }
    armState = CargoIntakeArmState.read();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    //TODO : FIX THIS!!!!!
  
    builder.addStringProperty("CargoIntakeRoller", roller::name, (command) -> roller(command));
    builder.addStringProperty("CargoIntakeArm", arm::name, (command) -> arm(command));
    builder.addStringProperty("CargoIntakeArmState", armState::name, null);

    
    if(RobotMap.HAS_ROLLER_INTAKE){

      CargoIntakeRoller.motor.initSendable(builder);
      CargoIntakeArm.leftSolenoid.initSendable(builder);
      CargoIntakeArm.rightSolenoid.initSendable(builder);
      CargoIntakeArmState.rollerSwitchUp.initSendable(builder);
      CargoIntakeArmState.rollerSwitchDown.initSendable(builder);
    }
  }

}