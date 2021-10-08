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
    LOGGER.debug("Gyro created: " + gyro);
  }

    public void init() {
      drive.zero();
      gyro.zero();
      SmartDashboard.putNumber("Turn Degrees", 0);
      SmartDashboard.putNumber("Turn multiplier", 0);
    }

    public void periodic() {
      double turn = SmartDashboard.getNumber("Turn Degrees", 0);
      double turnMult = SmartDashboard.getNumber("Turn multiplier", 0);


     
      // if(gyro.getPitchDegrees() <= Math.abs(turn) * turnMult){ } else {
      //   drive.arcadeDrive(0, 0);
      // }
        drive.arcadeDrive(0, 0.2);

     
    


       LOGGER.info("Yaw: {}, Pitch: {}, Roll: {}", gyro.getYawDegrees(), gyro.getPitchDegrees(), gyro.getRollDegrees());

    }
}