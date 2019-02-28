package frc.robot.tuning;

import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.logging.RobotLogManager;

public class TuneController {

  public static void loadTuners() {
    register("Drive Straight", new DriveStraightTuner());
    register("Drive Turn", new DriveTurnTuner());
    register("Turret", new TurretTuner());
    register("Wrist", new WristTuner());
  }

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  private static Tuner tuner;

  private static final HashMap<String, Tuner> tuners = new HashMap<String, Tuner>();

  private static void register(String name, Tuner tuner) {
    if (name != null && tuner != null) {
      tuners.put(name, tuner);
    }
  }

  public static void init() {
    String key = SmartDashboard.getString("DB/String 0", "NO_TEST");
    if (tuners.containsKey(key)) {
      tuner = tuners.get(key);
      tuner.init();
    } else {
      tuner = null;
      LOGGER.warn("Invalid Tuner in DB/String 5");
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