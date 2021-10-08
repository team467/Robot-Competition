package frc.robot.gamepieces.AbstractLayers;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.gamepieces.GamePiece;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;

public class IndexerAL extends GamePieceBase implements GamePiece {

  private static IndexerAL instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(IndexerAL.class.getName());

  private static WPI_TalonSRX indexFirstMotor;
  private static TalonSpeedControllerGroup indexFirstStage;

  private static WPI_TalonSRX indexSecondMotor;
  private static TalonSpeedControllerGroup indexSecondStage;

  private static DigitalInput mouthLimit;
  private static DigitalInput chamberLimit;

  public enum SensorTestMode {
    FORCE_TRUE, FORCE_FALSE, USE_SENSOR
  }

  SensorTestMode forceMouthSensor = SensorTestMode.USE_SENSOR;
  SensorTestMode forceChamberSensor = SensorTestMode.USE_SENSOR;

  private boolean ballLoaded = false;

  public static IndexerAL getInstance() {
    if (instance == null) {
      if (RobotMap.HAS_INDEXER) {
        LOGGER.debug("lead created");
        indexFirstMotor = new WPI_TalonSRX(RobotMap.FIRST_MAGAZINE_FEED_MOTOR_CHANNEL);
        indexSecondMotor = new WPI_TalonSRX(RobotMap.SECOND_MAGAZINE_FEED_MOTOR_CHANNEL);

        indexFirstStage = new TalonSpeedControllerGroup("Indexer First Stage", ControlMode.PercentOutput, RobotMap.INDEXER_SENSOR_INVERTED,
            RobotMap.FIRST_MAGAZINE_FEED_MOTOR_INVERTED, indexFirstMotor);
        indexSecondStage = new TalonSpeedControllerGroup("Indexer Second Stage", ControlMode.PercentOutput, RobotMap.INDEXER_SENSOR_INVERTED,
            RobotMap.SECOND_MAGAZINE_FEED_MOTOR_INVERTED, indexSecondMotor);

      } else {
        indexFirstStage = new TalonSpeedControllerGroup();
        indexSecondStage = new TalonSpeedControllerGroup();
      }

      if (RobotMap.HAS_INDEXER_LIMIT_SWITCHES) {
        mouthLimit = new DigitalInput(RobotMap.INDEXER_MOUTH_SWITCH_CHANNEL);
        chamberLimit = new DigitalInput(RobotMap.INDEXER_CHAMBER_SWITCH_CHANNEL);
      }

      instance = new IndexerAL();
      instance.stopIndexer();
    }
    return instance;
  }

  private IndexerAL() {
    super("Telemetry", "Indexer");
  }

  public void stopIndexer() {
    if (RobotMap.HAS_INDEXER) {
      if (indexFirstStage != null) {
        indexFirstStage.set(0.0);
      }
      if (indexSecondStage != null) {
        indexSecondStage.set(0.0);
      }
    }
  }

  public void setIndexerFirstStageSpeed(double speed) {
    if (indexFirstStage != null && RobotMap.HAS_INDEXER) {
      double output = Math.max(-1.0, Math.min(1.0, speed));
      indexFirstStage.set(output);
    }
  }

  public void setIndexerSecondStageSpeed(double speed) {
    if (indexSecondStage != null && RobotMap.HAS_INDEXER) {
      double output = Math.max(-1.0, Math.min(1.0, speed));
      indexSecondStage.set(output);
    }
  }

  public void setForceBallInMouth(final SensorTestMode mode) {
    forceMouthSensor = mode;
  }

  public void setForceBallInChamber(final SensorTestMode mode) {
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
    
   // LOGGER.info("Ball is in mouth {}", result);

   boolean result = false;

   if (RobotMap.HAS_INDEXER_LIMIT_SWITCHES) {
    result = !mouthLimit.get();
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
    // LOGGER.info("Ball is in Chamber {}", result);

    boolean result = false;

   if (RobotMap.HAS_INDEXER_LIMIT_SWITCHES) {
    result = !chamberLimit.get();
   }

    return result;
  }

  public void loadBall() {
    LOGGER.debug("Loaded shot");
    ballLoaded = true;
  }

  public void shootBall() {
    LOGGER.debug("Ball shot");
    ballLoaded = false;
  }

  public boolean ballLoaded() {
    return ballLoaded;
  }

  private void setForward() {
    LOGGER.debug("Indexer going forward");
    indexFirstStage.set(RobotMap.INDEXER_FORWARD_SPEED);
  }

  private void setBackwards() {
    LOGGER.debug("Indexer going backwards");
    indexFirstStage.set(RobotMap.INDEXER_INVERSE_SPEED);
  }

  private void setStop() {
    LOGGER.debug("Indexer stopped");
    indexFirstStage.set(0.0);
  }

  public static void moveBallsTowardIntake() {
    LOGGER.debug("moving toward intake");
    IndexerAL.getInstance().setBackwards();

  }

  public static void advanceBallsToShooter() {
    LOGGER.debug("advancing toward shooter");
    IndexerAL.getInstance().setForward();
  }

  public static void callStop() {
    IndexerAL.getInstance().setStop();
  }

  public enum SetBelts {
    FORWARD, OFF, REVERSE

  }

  public TalonSpeedControllerGroup getindexFirstMotor() {
    return indexFirstStage;
  }

  public TalonSpeedControllerGroup getindexSecondMotor() {
    return indexSecondStage;
  }

  public void setDirection(final SetBelts direction) {

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

  //Belts

  public void setIntakeBeltSpeed(final double speed) {
    if (indexSecondStage != null && RobotMap.HAS_INTAKE) {
        final double output = Math.max(-1.0, Math.min(1.0, speed));
       // LOGGER.debug("Intake belt speed");
       indexSecondStage.set(output);
    }
}

public void setIntakeBeltToIndexer() {
    if (indexSecondStage != null && RobotMap.HAS_INTAKE) {
      indexSecondStage.set(-1.0);
    }
}


public void setIntakeBeltStop() {
    if (indexSecondStage != null && RobotMap.HAS_INTAKE) {
      indexSecondStage.set(0.0);
    }
}


public void setIntakeBeltToReverse() {
    if (indexSecondStage != null && RobotMap.HAS_INTAKE) {
      indexSecondStage.set(1.0);
    }
}

public static void callIntakeBeltOff() {
  IndexerAL.getInstance().setIntakeBeltStop();
}

public static void callIntakeBeltToIndexer() {
  IndexerAL.getInstance().setIntakeBeltToIndexer();
}

public static void callIntakeBeltInverse() {
  IndexerAL.getInstance().setIntakeBeltToReverse();
}

  private IndexerAL(final TalonSpeedControllerGroup indexer) {
    super("Telemetry", "Indexer");
  }
  
  @Override
  public void checkSystem() {

  try {
    getInstance();
   setDirection(SetBelts.FORWARD);
  } catch (final Exception e) {
    LOGGER.error("Indexer problem");
    e.printStackTrace();
  }

  }
}