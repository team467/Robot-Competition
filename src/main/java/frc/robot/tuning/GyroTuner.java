package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GyroTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  Drive drive;
  Gyrometer gyro;
  NetworkTableInstance inst = NetworkTableInstance.getDefault();

  GyroTuner() {
    drive = Drive.getInstance();

    gyro = Gyrometer.getInstance();
    LOGGER.info("Gyro created: " + gyro);
  }

    public void init() {
      drive.zero();
      gyro.zero();
    }

    public void periodic() {

      if(gyro.getPitchDegrees() > 4){
        drive.arcadeDrive(0, 0.3);
      }else if (gyro.getPitchDegrees() < -4) {
        drive.arcadeDrive(0, -0.3);
      } else {
        drive.arcadeDrive(0, 0);
      }


       LOGGER.info("Yaw: {}, Pitch: {}, Roll: {}", gyro.getYawDegrees(), gyro.getPitchDegrees(), gyro.getRollDegrees());

    }
}