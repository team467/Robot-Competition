package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class stateMachineTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  Drive drive;
  Gyrometer gyro;
  GamePieceController gamePieceController;

  stateMachineTuner() {
    gamePieceController = GamePieceController.getInstance();
    LOGGER.info("Gyro created: " + gyro);
  }

    public void init() {
      SmartDashboard.putBoolean("fire", false);
      SmartDashboard.putBoolean("Auto", false);
    }

    public void periodic() {
 
        
        boolean fire = SmartDashboard.getBoolean("fire", false);
        boolean auto = SmartDashboard.getBoolean("Auto", false);
        gamePieceController.setAutomousFireWhenReady(fire);
        gamePieceController.ShooterAuto = auto;

        gamePieceController.periodic();


    }
}