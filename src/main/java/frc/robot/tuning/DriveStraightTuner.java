package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveStraightTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getLogger(TuneController.class.getName());

  Drive drive;

  DriveStraightTuner() {
    drive = Drive.getInstance();
  }

  public void init() {
    LOGGER.info("Init Drive Straight Test");
    LOGGER.info("Tuning PID Slot {}", RobotMap.PID_SLOT_DRIVE);
    drive.readPidsFromSmartDashboard(RobotMap.PID_SLOT_DRIVE);
  }

  public void periodic() {
    double tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0"));
    LOGGER.info("Tuning Value: {}", tuningValue);
    drive.zero();
    drive.tuneForward(tuningValue, RobotMap.PID_SLOT_DRIVE);
    LOGGER.debug("Distance {} feet", drive.getLeftDistance());
  }

}