package frc.robot.gamepieces;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Indexer extends GamePieceBase implements GamePiece {

  private static Indexer instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Indexer.class.getName());

  private IndexBelt stage1;

  // change the motor names
  public enum IndexBelt {
    STOP, FEED, EJECT;

    private static WPI_TalonSRX magazineFeedMotor1;
    private static WPI_TalonSRX magazineFeedMotor2;
    private static WPI_TalonSRX magazineFeedMotor3;

    private static void Initialize() {
      magazineFeedMotor1 = new WPI_TalonSRX(RobotMap.MAGAZINE_FEED_MOTOR_CHANNEL);
      magazineFeedMotor1.setInverted(RobotMap.STAGE_FEED_MOTOR_INVERTED);

      magazineFeedMotor2 = new WPI_TalonSRX(RobotMap.MAGAZINE_FEED_MOTOR_CHANNEL);
      magazineFeedMotor2.setInverted(RobotMap.STAGE_FEED_MOTOR_INVERTED);

      magazineFeedMotor3 = new WPI_TalonSRX(RobotMap.MAGAZINE_FEED_MOTOR_CHANNEL);
      magazineFeedMotor3.setInverted(RobotMap.STAGE_FEED_MOTOR_INVERTED);
    }

    // all conveyor belts will turn on/off simutanously
    public void actuate() {
      if (RobotMap.HAS_INDEXER) {
        switch (this) {
        case STOP:
        default:
          magazineFeedMotor1.set(0.0);
          magazineFeedMotor2.set(0.0);
          magazineFeedMotor3.set(0.0);
          break;
        case FEED:
          magazineFeedMotor1.set(1.0);
          magazineFeedMotor2.set(1.0);
          magazineFeedMotor3.set(1.0);
          break;
        case EJECT:
          magazineFeedMotor1.set(-1.0);
          magazineFeedMotor2.set(-1.0);
          magazineFeedMotor3.set(-1.0);
          break;
        }
      }
    }
  }

  public static Indexer getInstance() {
    if (instance == null) {
      instance = new Indexer();
    }
    return instance;
  }

  private Indexer() {
    super("Telemetry", "Indexer");

    // Initialize
    IndexBelt.Initialize();

    stage1 = IndexBelt.STOP;

  }

  public IndexBelt stage1() {
    return stage1;
  }

  public void periodic() {

    if (RobotMap.HAS_INDEXER) {
      if (enabled) {
        stage1.actuate();
      }
    }

  }
}
