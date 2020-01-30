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

    private static WPI_TalonSRX magazineFeedMotorLeader;
    private static WPI_TalonSRX magazineFeedMotorFollower;

    private static void Initialize() {

      magazineFeedMotorLeader = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
      magazineFeedMotorLeader.setInverted(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_INVERTED);

      magazineFeedMotorFollower = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);
      magazineFeedMotorFollower.setInverted(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_INVERTED);

    }

    // all conveyor belts will turn on/off simutanously
    public void actuate() {
      if (RobotMap.HAS_INDEXER) {
        switch (this) {
        case STOP:
        default:
          magazineFeedMotorLeader.set(0.0);
          magazineFeedMotorFollower.set(0.0);
          LOGGER.debug("Indexer is stopping");
          break;
        case FEED:
          magazineFeedMotorLeader.set(-1.0);
          magazineFeedMotorFollower.set(-1.0);
          LOGGER.debug("Indexer going backwards");
          break;
        case EJECT:
          magazineFeedMotorLeader.set(1.0);
          magazineFeedMotorFollower.set(0.0);
          LOGGER.debug("Indexer going forward");
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