package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTurnTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  Drive drive;

  DriveTurnTuner() {
    drive = Drive.getInstance();
  }

  public void init() {
    LOGGER.info("Init Drive Turn Test");
    LOGGER.info("Tuning PID Slot {}", RobotMap.PID_SLOT_TURN);
    drive.readPidsFromSmartDashboard(RobotMap.PID_SLOT_TURN);
  }

  public void periodic() {
    double tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0"));
    LOGGER.info("Tuning Value: " + tuningValue);
    drive.zero();
    drive.tuneTurn(tuningValue, RobotMap.PID_SLOT_TURN);
    LOGGER.debug("Distance {} feet", drive.getLeftDistance());
  }

}