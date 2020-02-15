package frc.robot.gamepieces.AbstractLayers;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
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

  public static TalonSpeedControllerGroup indexer;
  public boolean override;
  public boolean mouthOverride;
  public boolean chamberOverride;
  public static boolean calledForward;
  public static boolean calledReverse;

  public static IndexerAL getInstance() {
    if (instance == null) {
      if (RobotMap.HAS_INDEXER) {
        indexLeader = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
        indexFollower = null;
       
        if (RobotMap.INDEXER_FOLLOWER) {
          indexFollower = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);
        }

        indexer = new  new TalonSpeedControllerGroup("Shooter", ControlMode.Velocity, RobotMap.INDEXER_SENSOR_INVERTED,
        RobotMap.INDEXER_MOTOR_INVERTED, indexLeader, indexFollower);

      } else {
        indexer = new TalonSpeedControllerGroup();
      }

      instance = new IndexerAL(indexer);
    }
    return instance;
  }

  private void setForward() {
    LOGGER.debug("Indexer going forward");
    indexer.set(1.0);
  }

  private void setBackwards() {
    LOGGER.debug("Indexer going backwards");
    indexer.set(-1.0);
  }

  private void setStop() {
    LOGGER.debug("Indexer stopped");
    indexer.set(0.0);
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

  // TODO determine TOF threshold
  public double mouthSensorValue() {
    return TOFSensor.getInstance().getMouthDistance();
  }

  public double chamberSensorValue() {
    return TOFSensor.getInstance().getChamberDistance();
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

  private IndexerAL(TalonSpeedControllerGroup indexer) {
    super("Telemetry", "Indexer");
  }

  public void periodic() {

    if (enabled) {

    }
  }

  @Override
  public void checkSystem() {
    // TODO Auto-generated method stub

  }
}