package frc.robot.gamepieces;

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

  public enum CargoIntakeArm {
    OFF,
    UP,
    DOWN;

    private static DoubleSolenoid leftSolenoid;
    private static DoubleSolenoid rightSolenoid;

    private static void initialize() {
      if (RobotMap.HAS_ROLLER_INTAKE) {
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
      
      LOGGER.debug("Actuate cargo intake arm: {}", this);
      if (RobotMap.HAS_ROLLER_INTAKE) {
          switch (this) {
          case DOWN:
            leftSolenoid.set(DoubleSolenoid.Value.kReverse);
            rightSolenoid.set(DoubleSolenoid.Value.kReverse);
            break;
          case UP:
            leftSolenoid.set(DoubleSolenoid.Value.kForward);
            rightSolenoid.set(DoubleSolenoid.Value.kForward);
            break;
          default:
            leftSolenoid.set(DoubleSolenoid.Value.kOff);
            rightSolenoid.set(DoubleSolenoid.Value.kOff);
        }
      }
    }

  }

  public enum CargoIntakeRoller {
    REJECT,
    STOP,
    INTAKE;

    private static Spark motor;

    private static void initialize() {
      motor = new Spark(RobotMap.ROLLER_MOTOR_CHANNEL);
      motor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
      motor.setName("Telemetry", "CargoIntakeRollerMotor");
    }

    /**
     * Moves the roller forward or backward based on the requested command.
     */
    private void actuate() {
      LOGGER.debug("Actuate cargo intake roller: {}", this);
      switch (this) {

        case REJECT:
          if (RobotMap.HAS_ROLLER_INTAKE && !RobotMap.useSimulator) {
            motor.set(1.0);
          }
          break;
        
        case INTAKE:
          if (RobotMap.HAS_ROLLER_INTAKE && !RobotMap.useSimulator) {
            motor.set(-1.0);
          }
          break;

        case STOP:
        default:
          if (RobotMap.HAS_ROLLER_INTAKE && !RobotMap.useSimulator) {
            motor.set(0.0);
          }
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

    // Initialize the actuators
    CargoIntakeRoller.initialize();
    CargoIntakeArm.initialize();

    roller = CargoIntakeRoller.STOP;
    arm = CargoIntakeArm.UP;

    initSendable(TelemetryBuilder.getInstance());
    LOGGER.trace("Created roller arm game piece.");


  }

  /**
   * Moves the roller arm up or down.
   * 
   * @param command which way to move the arm.
   */
  public void arm(CargoIntakeArm command) {
    LOGGER.debug("Setting intake arm position to {}.", command);
    arm = command;
  }

  /**
   * Moves the roller arm up or down. The String version sets the 
   * command from the Smart Dashboard.
   * 
   * @param command which way to move the arm.
   */
  private void arm(String command) {
    LOGGER.debug("Setting intake arm position to {} using string interface.", command);
    arm = CargoIntakeArm.valueOf(command);
  }

  /**
   * Gets the last command given to the cargo intake arm.
   * 
   * @return the cargo intake command
   */
  public CargoIntakeArm arm() {
    LOGGER.debug("Current cargo intake arm command is {}.", arm);
    return arm;
  }

  /**
   * Sets the roller based on the given command.
   * 
   * @param command the roller command
   */
  public void roller(CargoIntakeRoller command) {
    LOGGER.debug("Setting cargo intake roller command to {}.", command);
    roller = command;
  }

  /**
   * Sets the roller based on the given command. The String version
   * is used for setting the command from the SmartDashboard.
   * 
   * @param command the roller command
   */
  public void roller(String command) {
    LOGGER.debug("Setting cargo intake roller command to {} using string interface.", command);
    roller = CargoIntakeRoller.valueOf(command);
  }

  /**
   * Returns the current rollar command. There is no external sensor on this motor.
   */
  public CargoIntakeRoller roller() {
    LOGGER.debug("Current cargo intake roller command is {}.", roller);
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
    } else {
      LOGGER.debug("Cargo intake mechanism is disabled.");
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("Intake Roller Command", 
        this::rollerCommandString, (command) -> roller(command));
    builder.addStringProperty("Intake Arm Command",
        this::armCommandString, (command) -> arm(command));
    if (RobotMap.HAS_ROLLER_INTAKE) {
      builder.addStringProperty("Intake Left Arm Solenoid",
          this::armLeftSolinoidString, null);
      builder.addStringProperty("Intake Right Arm Solenoid",
          this::armRightSolinoidString, null);
      builder.addDoubleProperty("Intake Roller Motor Output",
          this::rollerMotorOutput, null);
    }
  }

  private String rollerCommandString() {
    return roller.toString();
  }

  private double rollerMotorOutput() {
    return CargoIntakeRoller.motor.get();
  }

  private String armLeftSolinoidString() {
    return CargoIntakeArm.leftSolenoid.get().toString();
  }

  private String armRightSolinoidString() {
    return CargoIntakeArm.rightSolenoid.get().toString();
  }

  private String armCommandString() {
    return arm.toString();
  }


}