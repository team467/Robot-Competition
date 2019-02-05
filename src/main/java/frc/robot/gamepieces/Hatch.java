package frc.robot.gamepieces;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

public class Hatch {
  public enum HatchState {
    OPEN,
    CLOSE,
    NEUTRAL
  }

  private HatchState state = HatchState.NEUTRAL;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Grabber.class.getName());

  private static Hatch instance;
  private DoubleSolenoid doubleSolenoid1;    
  private DoubleSolenoid doubleSolenoid2;    
  private DoubleSolenoid doubleSolenoid3;

  private Hatch() {
    if (RobotMap.HAS_HATCH) {
      this.doubleSolenoid1 = new DoubleSolenoid(RobotMap.HATCH_S1_FORWARD_CHANNEL, RobotMap.HATCH_S1_REVERSE_CHANNEL);
      this.doubleSolenoid2 = new DoubleSolenoid(RobotMap.HATCH_S2_FORWARD_CHANNEL, RobotMap.HATCH_S2_REVERSE_CHANNEL);
      this.doubleSolenoid3 = new DoubleSolenoid(RobotMap.HATCH_S3_FORWARD_CHANNEL, RobotMap.HATCH_S3_REVERSE_CHANNEL);
    }
  }
    
  public static Hatch getInstance() {
    if (instance == null) {
      instance = new Hatch();
    }
    return instance;
  }

  public void open() {
    if (!RobotMap.useSimulator) {
      if (this.state != HatchState.OPEN) {
        this.state = HatchState.OPEN;
        this.doubleSolenoid1.set(Value.kForward);
        this.doubleSolenoid2.set(Value.kForward);
        this.doubleSolenoid3.set(Value.kForward);
      }
    }
  }

  public void close() {
    if (!RobotMap.useSimulator) {
      if (this.state != HatchState.CLOSE) {
        this.state = HatchState.CLOSE;
        this.doubleSolenoid1.set(Value.kReverse);
        this.doubleSolenoid2.set(Value.kReverse);
        this.doubleSolenoid3.set(Value.kReverse);
      }
    }
  }
}