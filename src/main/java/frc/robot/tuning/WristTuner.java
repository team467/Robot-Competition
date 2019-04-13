package frc.robot.tuning;

import static org.apache.logging.log4j.util.Unbox.box;
import frc.robot.gamepieces.CargoMech;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WristTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(WristTuner.class.getName());

  CargoWrist cargoMech;

  WristTuner() {
    cargoMech = CargoWrist.getInstance();
  }

  public void init() {
    LOGGER.info("Init Turret Tuning");
    double kP = Double.parseDouble(SmartDashboard.getString("DB/String 1", "20")); 
    double kI = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
    double kD = Double.parseDouble(SmartDashboard.getString("DB/String 3", "5"));
    cargoMech.configWristPid(kP, kI, kD);
  }

  public void periodic() {
    double tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 5", "0.0"));
    LOGGER.info("Tuning Value: {}", box(tuningValue));
    cargoMech.tuneMove(tuningValue);
    LOGGER.debug("Wrist state: {}", cargoMech.wrist().toString());
  }

}