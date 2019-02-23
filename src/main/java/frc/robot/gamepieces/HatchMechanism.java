package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class HatchMechanism extends GamePieceBase implements GamePiece {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(HatchMechanism.class.getName());

  private static HatchMechanism instance = null;

  // Actuators
  private HatchArm arm;
  private HatchLauncher launcher;

  // HatchArm
  public enum HatchArm {
    IN, OUT;

    private static DoubleSolenoid arm;

    private static void initialize() {
      arm = new DoubleSolenoid(
          RobotMap.HATCH_MECH_ARM_PCM_CHANNEL, 
          RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL, 
          RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL);
      LOGGER.debug("Hatch channels forward: {} Hatch Reverse: {}",
          RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL, 
          RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL);
    }

    private void actuate() {
      LOGGER.debug("Calling Actuate state: {}", this);
      switch (this) {
        case IN:
          if (RobotMap.HAS_HATCH_MECHANISM) {
            arm.set(DoubleSolenoid.Value.kReverse);
          }
          break;
        case OUT:
          if (RobotMap.HAS_HATCH_MECHANISM) {
            arm.set(DoubleSolenoid.Value.kForward);
          }
          break;
        default:
          if (RobotMap.HAS_HATCH_MECHANISM) {
            arm.set(DoubleSolenoid.Value.kOff);
          }
      }
    }
  }

  public enum HatchLauncher {
    FIRE, RESET;

    private static DoubleSolenoid launcher;

    private static void initialize() {
      launcher = new DoubleSolenoid(
          RobotMap.HATCH_LAUNCHER_PCM_CHANNEL, 
          RobotMap.HATCH_LAUNCHER_SOL_FORWARD_CHANNEL,
          RobotMap.HATCH_LAUNCHER_SOL_REVERSE_CHANNEL);
      launcher.setName("Telemetry", "HatchLauncherSolenoid1");
    }

    /**
     * Fires the three firing solenoids forwards and then retracts them.
     */
    private void fire() {
      // Fire solenoids forward
      if (RobotMap.HAS_HATCH_MECHANISM) {
        launcher.set(DoubleSolenoid.Value.kForward);
      }
    }

    private void reset() {
      // Retract solenoids after firing
      if (RobotMap.HAS_HATCH_MECHANISM) {
        launcher.set(DoubleSolenoid.Value.kReverse);
      }
    }

    private void actuate() {
      switch (this) {
        case FIRE:
          fire();
          break;
        case RESET:
          reset();
          break;
        default:
          LOGGER.info("No movement was done with the Hatch Mechanism");
          break;
      }
    }
  }

  /**
   * Returns a singleton instance of the hatch mechanism.
   * 
   * @return HatchMechanism the singleton instance
   */
  public static HatchMechanism getInstance() {
    if (instance == null) {
      instance = new HatchMechanism();
    }
    return instance;
  }

  // Constructor
  private HatchMechanism() {
    super("Telemetry", "Hatch Mechanism");

    // Initialize sensors and actuators
    HatchArm.initialize();
    HatchLauncher.initialize();

    arm = HatchArm.IN;
    launcher = HatchLauncher.RESET;

    initSendable(TelemetryBuilder.getInstance());
  }

  /**
   * Moves the arm in or out.
   * 
   * @param command to move the arm in or out.
   */
  public void arm(HatchArm command) {
    LOGGER.debug("Hatch mechanism arm command is {}.", command);
    arm = command;
  }

  /**
   * Moves the arm in or out. The String version sets the command from the Smart
   * Dashboard.
   * 
   * @param command which way to move the arm.
   */
  private void arm(String command) {
    LOGGER.debug("Hatch mechanism arm command is {} using string interface.", command);
    arm = HatchArm.valueOf(command);
  }

  public HatchArm arm() {
    LOGGER.debug("Current hatch mechanism arm command is {}.", arm);
    return arm;
  }

  /**
   * Moves the arm in or out.
   * 
   * @param command to move the arm in or out.
   */
  public void launcher(HatchLauncher command) {
    LOGGER.debug("Hatch launcher command is {}.", command);
    launcher = command;
  }

  /**
   * Moves the arm in or out. The String version sets the command from the Smart
   * Dashboard.
   * 
   * @param command which way to move the arm.
   */
  private void launcher(String command) {
    LOGGER.debug("Hatch launcher command is {} using string interface.", command);
    launcher = HatchLauncher.valueOf(command);
  }

  public HatchLauncher launcher() {
    LOGGER.debug("Current hatch launcher command is {}.", launcher);
    return launcher;
  }

  /**
   * Tries to handle commands and update state.
   */
  @Override
  public void periodic() {
    // Take Actions
    if (enabled) {
      arm.actuate();
      launcher.actuate();
    } else {
      LOGGER.debug("Hatch mechanism is disabled.");
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("Hatch Launcher Command", 
        this::launcherCommandString, (command) -> this.launcher(command));
    builder.addStringProperty("Hatch Launcher Solinoid", 
        this::launcherSolinoidString, null);
    builder.addStringProperty("Hatch Arm Command", 
        this::armCommandString, (command) -> this.arm(command));
    builder.addStringProperty("Hatch Arm Solinoid", 
        this::armSolinoidString, null);
  }

  private String launcherCommandString() {
    return launcher.toString();
  }

  private String armCommandString() {
    return arm.toString();
  }

  private String armSolinoidString() {
    return HatchArm.arm.get().toString();
  }

  private String launcherSolinoidString() {
    return HatchLauncher.launcher.get().toString();
  }

}
