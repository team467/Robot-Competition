package frc.robot.gamepieces;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class IndexerController extends GamePieceBase implements GamePiece {

  private static IndexerController instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(IndexerController.class.getName());

  private static WPI_TalonSRX indexLeader;
  private static WPI_TalonSRX indexFollower;

  

  public static IndexerController getInstance() {
    if (instance == null) {
      indexLeader = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
      indexFollower = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);
      instance = new IndexerController(indexLeader, indexFollower);
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
    IndexerController.getInstance().setForward();
  }

  public static void callBackwards() {
    IndexerController.getInstance().setBackwards();
  }

  public static void callStop() {
    IndexerController.getInstance().setStop();
  }
  

  private IndexerController(WPI_TalonSRX indexLeader, WPI_TalonSRX indexFollower) {
    super("Telemetry", "Indexer");

  }

  public void periodic() {

    if (RobotMap.HAS_INDEXER) {
      if (enabled) {

      }
    }
  }
}