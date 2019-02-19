package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class HatchMechanism extends GamePieceBase implements GamePiece {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(HatchMechanism.class.getName());

  private static HatchMechanism instance = null;

  // Actuators
  private HatchArm arm;
  private HatchLauncher launcher;

  // HatchArm
  public enum HatchArm {
    IN, OUT;

    private static DoubleSolenoid arm;

    private static void initialize() {
      if (RobotMap.HAS_HATCH_MECHANISM) {
        arm = new DoubleSolenoid(RobotMap.HATCH_MECH_ARM_PCM_CHANNEL, RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL, RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL);
        //LOGGER.error("Hatch forward: {} Hatch Reverse: {}",RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL, RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL);
      }
    }

    private void actuate() {
      //LOGGER.error("Calling Actuate state: {}", this);
      switch (this) {
      case IN:
        if (RobotMap.HAS_HATCH_MECHANISM) {
          arm.set(DoubleSolenoid.Value.kReverse);
          //LOGGER.info("Hatch arm going IN.");
        }
        break;
      case OUT:
        if (RobotMap.HAS_HATCH_MECHANISM) {
          arm.set(DoubleSolenoid.Value.kForward);
         // LOGGER.info("Hatch arm is OUT.");
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
      if (RobotMap.HAS_HATCH_MECHANISM) {
        launcher = new DoubleSolenoid( RobotMap.HATCH_LAUNCHER_PCM_CHANNEL, RobotMap.HATCH_LAUNCHER_SOL_FORWARD_CHANNEL,
            RobotMap.HATCH_LAUNCHER_SOL_REVERSE_CHANNEL);
        launcher.setName("Telemetry", "HatchLauncherSolenoid1");
      }
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
    super("Telemetry", "HatchMechanism");

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
    arm = command;
  }

  /**
   * Moves the arm in or out. The String version sets the command from the Smart
   * Dashboard.
   * 
   * @param command which way to move the arm.
   */
  public void arm(String command) {
    arm = HatchArm.valueOf(command);
  }

  public HatchArm arm() {
    return arm;
  }

  /**
   * Moves the arm in or out.
   * 
   * @param command to move the arm in or out.
   */
  public void launcher(HatchLauncher command) {
    launcher = command;
  }

  /**
   * Moves the arm in or out. The String version sets the command from the Smart
   * Dashboard.
   * 
   * @param command which way to move the arm.
   */
  public void launcher(String command) {
    launcher = HatchLauncher.valueOf(command);
  }

  public HatchLauncher launcher() {
    return launcher;
  }

  /**
   * Tries to handle commands and update state.
   */
  @Override
  public void periodic() {
    // Take Actions
    //LOGGER.warn("periodic called");
    if (true) {
      arm.actuate();
      launcher.actuate();
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("HatchLauncher", launcher::name, (command) -> launcher(command));
    builder.addStringProperty("HatchArm", arm::name, (command) -> arm(command));

    if (RobotMap.HAS_HATCH_MECHANISM) {
      HatchLauncher.launcher.initSendable(builder);
      HatchArm.arm.initSendable(builder);
    }
  }

}
