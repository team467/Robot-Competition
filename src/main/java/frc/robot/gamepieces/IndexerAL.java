package frc.robot.gamepieces;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class IndexerAL extends GamePieceBase implements GamePiece {

  private static IndexerAL instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(IndexerAL.class.getName());

  private static WPI_TalonSRX indexLeader;
  private static WPI_TalonSRX indexFollower;

  public static IndexerAL getInstance() {
    if (instance == null) {
      indexLeader = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
      indexFollower = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);

      if (RobotMap.HAS_INDEXER) {
        instance = new IndexerAL();
      }
    }
    return instance;
  }

  private void setForward() {
    LOGGER.debug("Indexer going forward");
    indexLeader.set(1.0);
    indexFollower.set(1.0);
  }

  private void setBackwards() {
    LOGGER.debug("Indexer going backwards");
    indexLeader.set(-1.0);
    indexFollower.set(-1.0);
  }

  private void setStop() {
    LOGGER.debug("Indexer stopped");
    indexLeader.set(0.0);
    indexFollower.set(0.0);
  }

  public static void callForward() {
    IndexerAL.getInstance().setForward();
  }

  public static void callBackwards() {
    IndexerAL.getInstance().setBackwards();
  }

  public static void callStop() {
    IndexerAL.getInstance().setStop();
  }

  public enum setBelts {
    FORWARD, OFF, REVERSE

  }

  // TODO: implement TOF sensor in boolean.
  public boolean inMouth() {
    
    return false;
  }

  // TODO: implement TOF sensor in boolean.
  public boolean inChamber() {

    return false;
  }

  public void indexerBeltDirection(setBelts direction) {

    switch (direction) {

    default:

    case FORWARD:
      setForward();
      break;

    case OFF:
      setStop();
      break;

    case REVERSE:
      setBackwards();
      break;

    }

  }

  private IndexerAL() {
    super("Telemetry", "Indexer");

  }

  public void periodic() {

    if (RobotMap.HAS_INDEXER) {
      if (enabled) {

      }
    }
  }
}