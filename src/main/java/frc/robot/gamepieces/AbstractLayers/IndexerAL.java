package frc.robot.gamepieces.AbstractLayers;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.TOFSensor;

import org.apache.logging.log4j.Logger;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.gamepieces.GamePiece;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class IndexerAL extends GamePieceBase implements GamePiece {

  private static IndexerAL instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(IndexerAL.class.getName());

  private WPI_TalonSRX indexLeader;
  private WPI_TalonSRX indexFollower;

  public boolean override;
  public boolean mouthOverride;
  public boolean chamberOverride;
  public static boolean calledForward;
  public static boolean calledReverse;

  public static IndexerAL getInstance() {
    if (instance == null) {
      instance = new IndexerAL();
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
    calledForward = true;
    IndexerAL.getInstance().setForward();

  }

  public static void callBackwards() {
    calledReverse = true;
    IndexerAL.getInstance().setBackwards();
  }

  public static void callStop() {
    IndexerAL.getInstance().setStop();
  }

  public enum setBelts {
    FORWARD, OFF, REVERSE

  }

  // TODO determine TOF threshold
  public boolean inMouth() {
    if (override) {
      return mouthOverride;
    } else {
      return TOFSensor.getInstance().getMouthDistance() < RobotMap.TOF_THRESHOLD;
    }
  }

  public boolean inChamber() {
    if (override) {
      return chamberOverride;
    } else {
      return TOFSensor.getInstance().getChamberDistance() < RobotMap.TOF_THRESHOLD;
    }
  }

  public void indexerBeltDirection(setBelts direction) {

    switch (direction) {

      case FORWARD:
        setForward();
        break;
  
      case REVERSE:
        setBackwards();
        break;

      case OFF:
          setStop();
          break;

      default:
        setStop();

    }

  }

  private IndexerAL() {
    super("Telemetry", "Indexer");

    if (RobotMap.HAS_INDEXER) {
      instance.indexLeader = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
      instance.indexFollower = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);
    }

  }

  public void periodic() {

    if (enabled) {

    }
  }

  @Override
  public void checkSystem() {
    // TODO Auto-generated method stub

    indexerBeltDirection(setBelts.FORWARD);

  }
}