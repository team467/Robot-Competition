package frc.robot.tuning;

import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.logging.RobotLogManager;

public class TuneController {

  public static void loadTuners() {
    register("Advanced_Tuner", new AdvancedTuner());
    register("Gyro_Tuner", new GyroTuner());
    register("Puppy_Mode", new PuppyModeTuner());
    register("PID_Velocity_Tuner", new PIDVelocityTuner());
    register("Shooter_Tuner", new ShooterTuner());
    register("Climber_Tuner", new ClimberTuner());
    register("Drive_Straight", new DriveConstantTuner());
    register("Indexer_Tuner", new IndexerTuner());
    register("Sm_Test", new ShooterStateMachineTuner());
    register("Indexer_SM_Tuner", new IndexerStateTuner());
    register("GPC_Tuner", new GPCTuner());
    register("Climber_SM_Tunner", new ClimberStateTuner());
    register("Intake_Tuner", new IntakeTuner());
  }

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  private static Tuner tuner;

  private static final HashMap<String, Tuner> tuners = new HashMap<String, Tuner>();

  private static void register(String name, Tuner tuner) {
    if (name != null && tuner != null) {
      tuners.put(name.toUpperCase(), tuner);
    }
  }

  public static void init() {
    String key = SmartDashboard.getString("DB/String 0", "NO_TEST").toUpperCase();
    SmartDashboard.putString("DB/String 0", key);
    LOGGER.error("Tuner initialized");
  
    if (tuners.containsKey(key)) {
      tuner = tuners.get(key);
      tuner.init();
    } else {
      tuner = null;
      LOGGER.warn("Invalid Tuner in DB/String 0");
      LOGGER.info("Tuning Modes are");
      for (String tunerName: tuners.keySet()) {
        LOGGER.info(" - {}", tunerName);
      }
    }
  }

  public static void periodic() {
    if (tuner != null) {
      tuner.periodic();
    }
  }

}