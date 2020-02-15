package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;
import frc.robot.vision.VisionController;

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
  GamePieceController gamePieceController;
  VisionController visionController;
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
    visionController = visionController.getInstance();

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
      SmartDashboard.putBoolean("Try Shot", false);
      gamePieceController = GamePieceController.getInstance();
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
      boolean tryShot = SmartDashboard.getBoolean("Try Shot", false);



      gamePieceController.periodic();

      if(tryShot){
      gamePieceController.determineShooterSpeed();
      drive.arcadeDrive(0, visionController.setTurn());

      if(visionController.atAngle()){ 
        gamePieceController.setAutomousFireWhenReady(true);
      
      } else {
        gamePieceController.setAutomousFireWhenReady(false);
      }
    } else {
      gamePieceController.setAutomousFireWhenReady(false);
      LOGGER.debug("Speed Shooter = {}", gamePieceController.getFireWhenReady());
      
    }
    

      

      LOGGER.debug("Yaw: {}, Pitch: {}, Roll: {}", gyro.getYawDegrees(), gyro.getPitchDegrees(), gyro.getRollDegrees());

    }
}