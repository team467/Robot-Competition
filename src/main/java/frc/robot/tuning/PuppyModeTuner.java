package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
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
  NetworkTable table = inst.getTable("vision");
  NetworkTableEntry netAngle = table.getEntry("TurningAngle");
  NetworkTableEntry netDist = table.getEntry("DistanceFromTarget");
  NetworkTableEntry net_Have_Angle = table.getEntry("haveAngle");
  NetworkTableEntry net_Have_Dist = table.getEntry("haveDistance");


  double angle = netAngle.getDouble(0.0);
  double dist = netDist.getDouble(0.0);
  boolean haveDistance = net_Have_Dist.getBoolean(false);
  boolean haveAngle = net_Have_Angle.getBoolean(false);


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
      double angle = netAngle.getDouble(0.0);
      double dist = netDist.getDouble(0.0);
      boolean haveDistance = net_Have_Dist.getBoolean(false);
      boolean haveAngle = net_Have_Angle.getBoolean(false);
      double speed;
      boolean isTurnDone = false;//SmartDashboard.getBoolean("Turn", false);
      boolean isDriveDone = false;
      double robotTurner;



      if(haveAngle){
        if(Math.abs(angle + gyro.getPitchDegrees()) < 2) {
          robotTurner = 0.0;
        } else if(-gyro.getPitchDegrees() < angle) {
            robotTurner = 0.2;
         } else if(-gyro.getPitchDegrees() > angle) {
           robotTurner = -0.2;
         } else {
           robotTurner = 0.0;
         }       

      } else {
          robotTurner = 0.0;
          
      }
      

      if(isTurnDone) {
        //isdone
          //gyro.zero();
          //isTurnDone = false;
          SmartDashboard.putBoolean("Turn", false);
      }

      if(dist > 40 && haveDistance) {
        speed = -0.2;
      } else {
        speed = 0;
      }


      drive.arcadeDrive(0, robotTurner);
    

      LOGGER.info("angle= {}, dist= {}, have angle = {}", angle, dist, haveAngle);

      LOGGER.info("Yaw: {}, Pitch: {}, Roll: {}", gyro.getYawDegrees(), gyro.getPitchDegrees(), gyro.getRollDegrees());

    }
}