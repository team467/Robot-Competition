package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class HatchMech extends GamePieceBase implements GamePiece {
  
  private static final Logger LOGGER = RobotLogManager.getMainLogger(HatchMech.class.getName());

  public enum HatchLauncher {
    FIRE,
    RESET,
    OFF;

    private static DoubleSolenoid solenoid1;
    private static DoubleSolenoid solenoid2;    
    private static DoubleSolenoid solenoid3;

    private static void initialize() {
      if (RobotMap.HAS_HATCH_MECHANISM) {
        solenoid1 = new DoubleSolenoid(
            RobotMap.HATCH_LAUNCHER_S1_FORWARD_CHANNEL, 
            RobotMap.HATCH_LAUNCHER_S1_REVERSE_CHANNEL);
        solenoid1.setName("Telemetry", "HatchLauncherSolenoid1");
        solenoid2 = new DoubleSolenoid(
            RobotMap.HATCH_LAUNCHER_S2_FORWARD_CHANNEL, 
            RobotMap.HATCH_LAUNCHER_S2_REVERSE_CHANNEL);
        solenoid2.setName("Telemetry", "HatchLauncherSolenoid2");
        solenoid3 = new DoubleSolenoid(
          RobotMap.HATCH_LAUNCHER_S3_FORWARD_CHANNEL, 
          RobotMap.HATCH_LAUNCHER_S3_REVERSE_CHANNEL);
        solenoid3.setName("Telemetry", "HatchLauncherSolenoid3");
        }

    }

    private void actuate() {
      LOGGER.debug("");
      if (RobotMap.useSimulator || !RobotMap.HAS_HATCH_MECHANISM) {
        return;
      }
      switch (this) {

        case FIRE:
          solenoid1.set(Value.kForward);
          solenoid2.set(Value.kForward);
          solenoid3.set(Value.kForward);
          break;
        
        case RESET:
          solenoid1.set(Value.kReverse);
          solenoid2.set(Value.kReverse);
          solenoid3.set(Value.kReverse);
          break;

        case OFF:
        default:
          solenoid1.set(Value.kOff);
          solenoid2.set(Value.kOff);
          solenoid3.set(Value.kOff);
          break;
      }
    }

  }

  public enum HatchMechArm {
    EXTEND,
    RETRACT,
    OFF;

    private static DoubleSolenoid solenoid;

    private static void initialize() {
      if (RobotMap.HAS_HATCH_MECHANISM) {
        solenoid = new DoubleSolenoid(
            RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL, 
            RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL);
        solenoid.setName("Telemetry", "HatchMechArmSolenoid");
      }  
    }

    private void actuate() {
      LOGGER.debug("Actuate hatch mechanism: {}", name());
      if (RobotMap.useSimulator || !RobotMap.HAS_HATCH_MECHANISM) {
        return;
      }
      switch (this) {

        case EXTEND:
          solenoid.set(Value.kForward);
          break;
        
        case RETRACT:
          solenoid.set(Value.kReverse);
          break;

        case OFF:
        default:
          solenoid.set(Value.kOff);
          break;
      }
    }

  }

  private HatchLauncher launcher;
  private HatchMechArm arm;

  private static HatchMech instance = null;

  /**
  * Returns a singleton instance of the hatch mechanism.
  * 
  * @return HatchMechanism the singleton instance
  */
  public static HatchMech getInstance() {
    if (instance == null) {
      instance = new HatchMech();
    }
    return instance;
  }

  private HatchMech() {
    super("Telemetry", "HatchMech");

    // Initialize the  actuators
    HatchLauncher.initialize();
    HatchMechArm.initialize();

    launcher = HatchLauncher.OFF;
    arm = HatchMechArm.OFF;

    initSendable(TelemetryBuilder.getInstance());
  }

  /**
   * Sets the hatch based on the given command.
   * 
   * @param command the hatch command
   */
  public void launcher(HatchLauncher command) {
    launcher = command;
  }

  /**
   * Sets the hatch based on the given command. The String version
   * is used for setting the command from the SmartDashboard.
   * 
   * @param command the hatch command
   */
  public void launcher(String command) {
    launcher = HatchLauncher.valueOf(command);
  }

  /**
   * Returns the current hatch command. There is no external sensor on this solenoid.
   */
  public HatchLauncher launcher() {
    return launcher;
  }

  /**
   * Sets the hatch arm based on the given command.
   * 
   * @param command the hatch arm command
   */
  public void arm(HatchMechArm command) {
    arm = command;
  }

  /**
   * Sets the hatch arm based on the given command. The String version
   * is used for setting the command from the SmartDashboard.
   * 
   * @param command the hatch arm command
   */
  public void arm(String command) {
    arm = HatchMechArm.valueOf(command);
  }

  /**
   * Returns the current hatch arm command. There is no external sensor on this solenoid.
   */
  public HatchMechArm arm() {
    return arm;
  }

  /**
   * Tries to handle commands and update state.
   */
  public void periodic() {
    // Take Actions
    if (enabled) {
      arm.actuate();
      launcher.actuate();
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty("HatchLauncher", launcher::name, (command) -> launcher(command));
    builder.addStringProperty("HatchMechArm", arm::name, (command) -> arm(command));
    HatchLauncher.solenoid1.initSendable(builder);
    HatchLauncher.solenoid2.initSendable(builder);
    HatchLauncher.solenoid3.initSendable(builder);
    HatchMechArm.solenoid.initSendable(builder);
  }

}