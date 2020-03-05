package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;
import frc.robot.vision.VisionController;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GPCTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());


  GamePieceController gamePieceController;
  VisionController visionContoller;
  

  GPCTuner() {
    gamePieceController = GamePieceController.getInstance();
    visionContoller = VisionController.getInstance();
  }

    public void init() {
    LOGGER.info("Full state machine tester");
        //Shooter controls
      SmartDashboard.putBoolean("Shoot", false);
      SmartDashboard.putBoolean("Auto Shooter", true);
      SmartDashboard.putBoolean("FlyWheel Man", false);
      SmartDashboard.putBoolean("Trigger Man", false);

      //Intake controls
      SmartDashboard.putBoolean("Roller-F", false);
      SmartDashboard.putBoolean("Roller-B", false);
      SmartDashboard.putBoolean("Intake U-D", true);

      //Index
      SmartDashboard.putBoolean("Index-F", false);
      SmartDashboard.putBoolean("Index-R", false);
      SmartDashboard.putBoolean("Auto Index", true);

      //Climber
      SmartDashboard.putBoolean("Climber activation", false);
      SmartDashboard.putBoolean("Climber Up", false);
      SmartDashboard.putBoolean("Climber Down", false);

    }

    public void periodic() {

        //Assign values

        //Shooter
        gamePieceController.ShooterAuto = SmartDashboard.getBoolean("Auto Shooter", false);

        // if (visionContoller.hasAngle()) {
        //     gamePieceController.determineShooterSpeed();
        // }

        gamePieceController.setAutomousFireWhenReady(SmartDashboard.getBoolean("Shoot", false));
        gamePieceController.flywheelManual = SmartDashboard.getBoolean("FlyWheel Man", false);
        gamePieceController.triggerManual = SmartDashboard.getBoolean("Trigger Man", false);
      
        //Intake
        gamePieceController.rollerStateIN = SmartDashboard.getBoolean("Roller-B", false);
        gamePieceController.rollerStateOUT = SmartDashboard.getBoolean("Roller-F", false);
        gamePieceController.armPosition = SmartDashboard.getBoolean("Intake U-D", true);

        //Index 
        gamePieceController.IndexAuto = SmartDashboard.getBoolean("Auto Index", true);
        gamePieceController.indexerBallsForward = SmartDashboard.getBoolean("Index-F", false);
        gamePieceController.indexerBallsReverse = SmartDashboard.getBoolean("Index-R", false);

        //climber
        gamePieceController.climberEnabled = SmartDashboard.getBoolean("Climber activation", false);
        gamePieceController.climberUpButtonPressed = SmartDashboard.getBoolean("Climber Up", false);
        gamePieceController.climberDownButtonPressed = SmartDashboard.getBoolean("Climber Down", false);

        //Auto alignment not tested here
        
        gamePieceController.periodic();


    }
}