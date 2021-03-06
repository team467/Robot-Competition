package frc.robot.gamepieces.AbstractLayers;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.gamepieces.GamePiece;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;

public class IndexerAL extends GamePieceBase implements GamePiece {

  private static IndexerAL instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(IndexerAL.class.getName());

  private static WPI_TalonSRX indexLeader;
  private static WPI_TalonSRX indexFollower;
  private static TalonSpeedControllerGroup indexer;

  private static Rev2mDistanceSensor onboardTOF;
  private static NetworkTableEntry networkTableTOF;

  public enum SensorTestMode {
    FORCE_TRUE, FORCE_FALSE, USE_SENSOR
  }

  SensorTestMode forceMouthSensor = SensorTestMode.USE_SENSOR;
  SensorTestMode forceChamberSensor = SensorTestMode.USE_SENSOR;

  public static IndexerAL getInstance() {
    if (instance == null) {
      if (RobotMap.HAS_INDEXER) {
        LOGGER.debug("lead created");
        indexLeader = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
        indexFollower = null;

        if (RobotMap.INDEX_FOLLOWER_MOTOR){
          LOGGER.debug("follower created");
          indexFollower = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);
        }

        indexer = new TalonSpeedControllerGroup("Indexer", ControlMode.PercentOutput, RobotMap.INDEXER_SENSOR_INVERTED,
            RobotMap.INDEXER_MOTOR_INVERTED, indexLeader, indexFollower);
        LOGGER.debug("Talon group:" + indexer.toString());

      } else {
        indexer = new TalonSpeedControllerGroup();
      }

      if (RobotMap.HAS_INDEXER_TOF_SENSORS) {
        onboardTOF = new Rev2mDistanceSensor(Port.kOnboard);
        onboardTOF.setAutomaticMode(true);
        NetworkTable table = NetworkTableInstance.getDefault().getTable("sensors");
        networkTableTOF = table.getEntry("tof");
      }

      instance = new IndexerAL(indexer);
    }
    return instance;
  }

  public void stopIndexer() {
    if (indexer != null && RobotMap.HAS_INDEXER) {
      indexer.set(0.0);
    }
  }

  public void setIndexerSpeed(double speed) {
    if (indexer != null && RobotMap.HAS_INDEXER) {
      double output = Math.max(-1.0, Math.min(1.0, speed));
      indexer.set(output);
    }
  }

  private double getMouthDistance() {
    double distance = 0;
    if (onboardTOF != null && RobotMap.HAS_INDEXER_TOF_SENSORS) {
      if (onboardTOF.isRangeValid()) {
        distance = onboardTOF.getRange();
      }
    }

    return distance;
  }

  private double getChamberDistance() {
    double distance = 0;
    if (networkTableTOF != null && RobotMap.HAS_INDEXER_TOF_SENSORS) {
      distance = networkTableTOF.getDouble(0);
    }

    return distance;
  }

  public void setForceBallInMouth(SensorTestMode mode) {
    forceMouthSensor = mode;
  }

  public void setForceBallInChamber(SensorTestMode mode) {
    forceChamberSensor = mode;
  }

  

  public boolean isBallInMouth() {
   
    // Tuners may force a result, bypassing the sensor.
    if (forceMouthSensor == SensorTestMode.FORCE_TRUE) {
      LOGGER.debug("ball is present in mouth");
      return true;
    } else {
      if (forceMouthSensor == SensorTestMode.FORCE_FALSE)
      return false;
    }

    boolean result = false; // TODO make this false when have indexer
    if (onboardTOF != null && RobotMap.HAS_INDEXER_TOF_SENSORS) {
      double distance = getMouthDistance();
      double threshold = RobotMap.INDEXER_TOF_THRESHOLD;

      if (distance <= threshold) {
        result = true;
      }
    }

    return result;
  }

  public boolean isBallInChamber() {
   
    if (forceChamberSensor == SensorTestMode.FORCE_TRUE) {
      LOGGER.debug("ball is present in chamber");
      return true;
    } else {
      if (forceChamberSensor == SensorTestMode.FORCE_FALSE)
      return false;
    }
    boolean result = false; // TODO make this false when have indexer
    if (networkTableTOF != null && RobotMap.HAS_INDEXER_TOF_SENSORS) {
      double distance = getChamberDistance();
      double threshold = RobotMap.INDEXER_TOF_THRESHOLD;

      if (distance <= threshold) {
        result = true;
      }
    }

    return result;
  }

  private void setForward() {
    LOGGER.debug("Indexer going forward");
    indexer.set(RobotMap.INDEXER_FORWARD_SPEED);
  }

  private void setBackwards() {
    LOGGER.debug("Indexer going backwards");
    indexer.set(RobotMap.INDEXER_INVERSE_SPEED);
  }

  private void setStop() {
    LOGGER.debug("Indexer stopped");
    indexer.set(0.0);
  }

  public static void moveBallsTowardIntake() {
    LOGGER.debug("moving toward intake");
    IndexerAL.getInstance().setForward();

  }

  public static void advanceBallsToShooter() {
    LOGGER.debug("advancing toward shooter");
    IndexerAL.getInstance().setBackwards();
  }

  public static void callStop() {
    IndexerAL.getInstance().setStop();
  }

  public enum SetBelts {
    FORWARD, OFF, REVERSE

  }

  public void setDirection(SetBelts direction) {

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
  
  @Override
  public void checkSystem() {
    // TODO Auto-generated method stub

  }
}