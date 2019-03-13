package frc.robot.tuning;

import frc.robot.gamepieces.CargoIntake;
import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(IntakeTuner.class.getName());

  CargoIntake intake;

  IntakeTuner() {
    intake = CargoIntake.getInstance();
  }

  public void init() {
    LOGGER.info("Init Intake Tuning");
  }

  public void periodic() {
    String tuningValue = SmartDashboard.getString("DB/String 5", "OFF");
    LOGGER.info("Tuning Value: {}", tuningValue);
    try {
      CargoIntakeArm command = CargoIntakeArm.valueOf(tuningValue.toUpperCase());
      //intake.arm(command);
      LOGGER.debug("Intake state: {}", intake.arm().toString());
    } catch (IllegalArgumentException e) {
      LOGGER.error("Need to enter one of UP, DOWN or OFF int DB/String 5.");
      LOGGER.error(e);
    }
  }

}