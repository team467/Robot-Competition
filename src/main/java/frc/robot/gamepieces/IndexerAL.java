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
      instance = new IndexerAL(indexLeader, indexFollower);
    }
      return instance;
  }

  private void setForward() {
    indexLeader.set(1.0);
    indexFollower.set(1.0);
  }

  private void setBackwards() {
    indexLeader.set(-1.0);
    indexFollower.set(-1.0);
  }

  private void setStop() {
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
  

  private IndexerAL(WPI_TalonSRX indexLeader, WPI_TalonSRX indexFollower) {
    super("Telemetry", "Indexer");

  }

  public void periodic() {

    if (RobotMap.HAS_INDEXER) {
      if (enabled) {

      }
    }
  }
}