package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;
import frc.robot.tuning.SplineAlgorithem;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SplineAutoTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  Drive drive;
  Gyrometer gyro;
  Timer timer;


  SplineAlgorithem splineAlgorithem = new SplineAlgorithem();
  double angleRate;


  SplineAutoTuner() {
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
      double angleRate = gyro.getRate();
      double VolitionSpeed, VolitionAngle;
      boolean isTurnDone = false;//SmartDashboard.getBoolean("Turn", false);
      boolean isDriveDone = false;
      double robotTurner;




        if(Math.abs(splineAlgorithem.calculateAngleRate(timer.get(), false) + -angleRate) < 2) {
          robotTurner = 0.0;
        } else if(-angleRate < splineAlgorithem.calculateAngleRate(timer.get(), false)) {
            robotTurner = splineAlgorithem.calculateAngleRate(timer.get(), false) / 40;
         } else if(-angleRate > splineAlgorithem.calculateAngleRate(timer.get(), false)) {
           robotTurner = splineAlgorithem.calculateAngleRate(timer.get(), false) / 40;
         } else {
           robotTurner = 0.0;
         }       

      drive.arcadeDrive(0, robotTurner);
    

      LOGGER.info("angle= {}, dist= {}", angleRate, splineAlgorithem.calculateAngleRate(timer.get(), false));

      LOGGER.info("Yaw: {}, Pitch: {}, Roll: {}", gyro.getYawDegrees(), gyro.getPitchDegrees(), gyro.getRollDegrees());

    }
}