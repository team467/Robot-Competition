package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class BallMech extends GamePieceBase implements GamePieceInterface {

    private static final Logger LOGGER 
    = RobotLogManager.getMainLogger(BallMech.class.getName());

    private static BallMech instance = null;

    // Actuators
    private Claw belt;
    private ClawArm arm;

    // State
    private ClawArmState armState;

    //TODO: Move to Robot Map
    public static int CLAW_ARM_UP_SENSOR_CHANNEL = 0;
    public static int CLAW_ARM_UP_SOLINOID_CHANNEL = 0;
    public static int CLAW_ARM_DOWN_SENSOR_CHANNEL = 0;
    public static int CLAW_ARM_DOWN_SOLINOID_CHANNEL = 0;
    public static int CLAW_MOTOR_CHANNEL = 0;
    public static boolean CLAW_MOTOR_INVERTED = false;

    // Booleans
    private boolean hasBall = false; // sees if BaM mechanism has ball in grasp
    private boolean isUp = false; // sees if BaM is up after picking ball or not
    
    public enum ClawArm {
        UP,
        DOWN,
        OFF;

        private static DoubleSolenoid arm;

        private static void initialize() {
          arm = new DoubleSolenoid(CLAW_ARM_UP_SOLINOID_CHANNEL, CLAW_ARM_DOWN_SOLINOID_CHANNEL);
    }

    /**
     * Moves the arm based on the requested command.
     */
    private void actuate() {
        switch (this) {
          case UP:
            arm.set(DoubleSolenoid.Value.kForward);
            break;
          case DOWN:
            arm.set(DoubleSolenoid.Value.kReverse);
            break;
          default:
            arm.set(DoubleSolenoid.Value.kOff);
        }
      }

    }

    public enum ClawArmState {
        UP,
        MOVING_DOWN,
        DOWN,
        MOVING_UP,
        UNKNOWN;   
        
    private static DigitalInput armUp;
    private static DigitalInput armDown;
    private static ClawArmState previousState;

    private static void initialize() {
        // Config arm sensors
      armUp = new DigitalInput(CLAW_ARM_UP_SENSOR_CHANNEL);
      armUp.setName("Telemetry", "ClawArmUp");
      armDown = new DigitalInput(CLAW_ARM_DOWN_SENSOR_CHANNEL);
      armDown.setName("Telemetry", "ClawArmDown");
    }

    private static ClawArmState read() {
        ClawArmState state;
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

    public enum Claw {
        FORWARD,
        REVERSE,
        STOP;

        private static SpeedController claw;

        private static void initialize() {
            // Create the roller object. No sensors
            claw = new Spark(CLAW_MOTOR_CHANNEL);
            claw.setInverted(CLAW_MOTOR_INVERTED);
          }

    /**
     * Moves the belts of the claw forward or backward based on the requested command.
     */
    private void actuate() {
        switch (this) {
  
          case FORWARD:
            claw.set(1.0);
            break;
          
          case REVERSE:
            claw.set(-1.0);
            break;
  
          case STOP:
          default:
            claw.set(0.0);
        }
        
      }
        
    }
    
    public enum heightPos { // have to work on- in progress
        bottom,
        rocketShip,
        cargoShip,
    }

    /**
  * Returns a singleton instance of the telemery builder.
  * 
  * @return TelemetryBuilder the telemetry builder instance
  */
    public static BallMech getInstance()  {
        if (instance == null) {
            instance = new BallMech();
        }
        return instance;
    }

    private BallMech() {
        super("Telemetry" , "BallMech");

        // Initialize the sensors and actuators
        Claw.initialize();
        ClawArm.initialize();
        ClawArmState.initialize();

        belt = Claw.STOP;
        arm = ClawArm.UP;
        armState = ClawArmState.read();

        LOGGER.trace("Created Ball Mech game piece.");
    }

    /**
     * Moves the claw arm up or down.
     * 
     * @param command which way to move the arm.
     */
    public void arm(ClawArm command) {
        arm = command;
    }

    /**
     * Moves the claw arm up or down. The String version sets the 
     * command from the Smart Dashboard.
     * 
     * @param command which way to move the arm.
     */
    public void arm(String command) {
        arm = ClawArm.valueOf(command);
    }

    /**
     * Reads the arm state from the sensors.
     * 
     * @return the state of the arm, including if unknown or moving.
     */
    public ClawArmState arm() {
        return armState;
    }

    /**
     * Sets the belt based on the given command.
     * 
     * @param command the belt command
     */
    public void belt(Claw command) {
        belt = command;
    }

  /**
   * Sets the belt based on the given command. The String version
   * is used for setting the command from the SmartDashboard.
   * 
   * @param command the belt command
   */
    public void belt(String command) {
        belt = Claw.valueOf(command);
    }

  /**
   * Returns the current belt command. There is no external sensor on this motor.
   */
    public Claw belt() {
        return belt;
    }

  /**
   * Tries to handle commands and update state.
   */
  public void periodic() { // In progress
    // Take Actions
    if (enabled) {
      belt.actuate();
      // arm.actuate(); Probaly won't be needed here
    }
    // Update state
    armState = ClawArmState.read();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("Claw", belt::name, (command) -> belt(command));
    builder.addStringProperty("ClawArm", arm::name, (command) -> arm(command));
    builder.addStringProperty("ClawArmState", armState::name, null);
  }

}