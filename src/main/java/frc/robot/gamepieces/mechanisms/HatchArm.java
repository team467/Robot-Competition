package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.RobotMap;
import frc.robot.gamepieces.states.IntakeStates;
import frc.robot.gamepieces.states.IntakeStates.HatchArmStates;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class HatchArm extends GamePieceBase implements GamePiece {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(HatchArm.class.getName());
    
    private static HatchArm instance = null;

    private IntakeStates.HatchArmStates hatchArmState = HatchArmStates.IN;

    private DoubleSolenoid arm;

    private HatchArm(){
        super("Telemetry","hatchArm");
        if (RobotMap.HAS_HATCH_MECHANISM) {
            arm = new DoubleSolenoid(
              RobotMap.HATCH_MECH_ARM_PCM_CHANNEL, 
              RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL, 
              RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL);
          LOGGER.debug("Hatch channels forward: {} Hatch Reverse: {}",
              box(RobotMap.HATCH_MECH_ARM_FORWARD_CHANNEL), 
              box(RobotMap.HATCH_MECH_ARM_REVERSE_CHANNEL));
          }
    }

    public static HatchArm getInstance(){
        if(instance == null) {
            instance = new HatchArm();
        }
        return instance;
    }

    @Override
    public void read() {
        //nothing to read
    }

    @Override
    public void actuate() {
        LOGGER.debug("Calling Actuate state: {}", this);
        if (RobotMap.HAS_HATCH_MECHANISM) {
          switch (hatchArmState) {
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

    @Override
    public boolean systemCheck() {
        return false;
    }

    @Override
    public void periodic() {

    }
}