package frc.robot.tuning;

import static org.apache.logging.log4j.util.Unbox.box;
import frc.robot.gamepieces.Turret;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurretTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getLogger(TuneController.class.getName());

  Turret turret;

  TurretTuner() {
    turret = Turret.getInstance();
  }

  public void init() {
    LOGGER.info("Init Turret Tuning");
    double kP = Double.parseDouble(SmartDashboard.getString("DB/String 1", "40")); // 1.6
    double kI = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0")); // 0.0
    double kD = Double.parseDouble(SmartDashboard.getString("DB/String 3", "5")); // 165
    turret.configPid(kP, kI, kD);
  }

  public void periodic() {
    double tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 5", "0.0"));
    LOGGER.info("Tuning Value: {}", box(tuningValue));
    turret.target(tuningValue);
    LOGGER.debug("Turret position at {} degrees", box(turret.position()));
  }

}