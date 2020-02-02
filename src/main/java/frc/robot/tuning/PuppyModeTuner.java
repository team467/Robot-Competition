package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PuppyModeTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  Drive drive;
  Gyrometer gyro;
  Timer timer;
  NetworkTableInstance inst = NetworkTableInstance.getDefault();

  PuppyModeTuner() {
    drive = Drive.getInstance();

    gyro = Gyrometer.getInstance();
    timer = new Timer();
    LOGGER.info("Gyro created: " + gyro);
  }

    public void init() {
      drive.zero();
      gyro.zero();
      timer.start();
      drive.setClosedSetRampRate(0.4);
      SmartDashboard.putNumber("Turn Degrees", 0);
      SmartDashboard.putNumber("Turn multiplier", 0);
      SmartDashboard.putNumber("Distance to travel", 0);
      SmartDashboard.putBoolean("Turn", false);
    }

    public void periodic() {
      double turn = SmartDashboard.getNumber("Turn Degrees", 0);
      double turnMult = SmartDashboard.getNumber("Turn multiplier", 0);
      double speed = SmartDashboard.getNumber("Distance to travel", 0);
      boolean turnBool = SmartDashboard.getBoolean("Turn", false);
      double robotTurner;

      if(Math.abs(gyro.getPitchDegrees()) <= Math.abs(turn)){
          robotTurner = -Math.signum(turn) * 0.3;
      } else {
          robotTurner = 0;
      }

      if(turnBool) {
        //isdone
          gyro.zero();
          turnBool = false;
          SmartDashboard.putBoolean("Turn", false);
      }


      drive.arcadeDrive(speed, robotTurner);
    


       LOGGER.info("Yaw: {}, Pitch: {}, Roll: {}", gyro.getYawDegrees(), gyro.getPitchDegrees(), gyro.getRollDegrees());

    }
}