package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class CargoIntake extends GamePieceBase implements GamePieceInterface {
  
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

    private static DoubleSolenoid solenoid;

    private static void initialize() {
      solenoid = new DoubleSolenoid(
          RobotMap.ROLLER_ARM_UP_SOLINOID_CHANNEL, 
          RobotMap.ROLLER_ARM_DOWN_SOLINOID_CHANNEL);
      solenoid.setName("Telemetry", "CargoIntakeArmSolenoid");
    }

    /**
     * Moves the arm based on the requested command.
     */
    private void actuate() {
      LOGGER.debug("Actuate cargo intake arm: {}", name());
      if (RobotMap.useSimulator) {
        return;
      }
      switch (this) {
        case DOWN:
          solenoid.set(DoubleSolenoid.Value.kReverse);
          break;
        case UP:
          solenoid.set(DoubleSolenoid.Value.kForward);
          break;
        default:
          solenoid.set(DoubleSolenoid.Value.kOff);
      }
    }

  }

  public enum CargoIntakeRoller {
    FORWARD,
    STOP,
    REVERSE;

    private static Spark motor;

    private static void initialize() {
      // Create the roller object. No sensors
      motor = new Spark(RobotMap.ROLLER_MOTOR_CHANNEL);
      motor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
      motor.setName("Telemetry", "CargoIntakeRollerMotor");
    }

    /**
     * Moves the roller forward or backward based on the requested command.
     */
    private void actuate() {
      switch (this) {

        case FORWARD:
          motor.set(1.0);
          break;
        
        case REVERSE:
          motor.set(-1.0);
          break;

        case STOP:
        default:
          motor.set(0.0);
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
   * Gets the last command given to the cargo intake arm.
   * 
   * @return the cargo intake command
   */
  public CargoIntakeArm arm() {
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
    if (enabled) {
      roller.actuate();
      arm.actuate();
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    builder.addStringProperty("CargoIntakeRoller", roller::name, (command) -> roller(command));
    builder.addStringProperty("CargoIntakeArm", arm::name, (command) -> arm(command));
    CargoIntakeRoller.motor.initSendable(builder);
    CargoIntakeArm.solenoid.initSendable(builder);
  }

}