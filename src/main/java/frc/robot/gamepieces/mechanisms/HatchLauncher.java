package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.RobotMap;
import frc.robot.gamepieces.states.IntakeStates;
import frc.robot.gamepieces.states.IntakeStates.HatchLauncherStates;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class HatchLauncher extends GamePieceBase implements GamePiece {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(HatchLauncher.class.getName());

  private static HatchLauncher instance = null;

  private IntakeStates.HatchLauncherStates hatchLauncherStates = HatchLauncherStates.RESET;

  // Actuators
  private HatchArm arm;
 
    private static DoubleSolenoid launcher;

    private HatchLauncher() {
      super("Telemetry", "Hatch Mechanism");

      if (RobotMap.HAS_HATCH_MECHANISM) {
        launcher = new DoubleSolenoid(
          RobotMap.HATCH_LAUNCHER_PCM_CHANNEL, 
          RobotMap.HATCH_LAUNCHER_SOL_FORWARD_CHANNEL,
          RobotMap.HATCH_LAUNCHER_SOL_REVERSE_CHANNEL);
        launcher.setName("Telemetry", "HatchLauncherSolenoid1");
      }
    }
      /**
   * Returns a singleton instance of the hatch mechanism.
   * 
   * @return HatchMechanism the singleton instance
   */
  public static HatchLauncher getInstance() {
    if (instance == null) {
      instance = new HatchLauncher();
    }
    return instance;
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

  public HatchArm arm() {
    LOGGER.debug("Current hatch mechanism arm command is {}.", arm);
    return arm;
  }


  /**
   * Tries to handle commands and update state.
   */
  @Override
  public void periodic() {

  }

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    // telemetry.addStringMetric("Hatch Launcher Command", this::launcherCommandString);
    // telemetry.addStringMetric("Hatch Arm Command", this::armCommandString);
    // if (RobotMap.HAS_HATCH_MECHANISM){
    //   telemetry.addStringMetric("Hatch Launcher Solinoid", this::launcherSolinoidString);
    //   telemetry.addStringMetric("Hatch Arm Solinoid", this::armSolinoidString);
    // } 
  }
  

  @Override
  public boolean systemCheck() {
    return false;
  }

  @Override
  public void read() {
    //nothing to read
  }

  @Override
  public void actuate() {
    switch (hatchLauncherStates) {
      case FIRE:
        fire();
        break;
      case RESET:
        reset();
        break;
      default:
        LOGGER.debug("No movement was done with the Hatch Mechanism");
        break;
    }

  }
}
