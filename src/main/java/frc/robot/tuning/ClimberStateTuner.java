package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import frc.robot.gamepieces.AbstractLayers.ClimberAL.climberSpeed;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ClimberStateTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    ClimberAL climber;
    GamePieceController gamePieceController;

    ClimberStateTuner() {
        climber = ClimberAL.getInstance();
    }

    public void init() {
        SmartDashboard.putBoolean("isEnabled", false);
        SmartDashboard.putBoolean("upButton", false);
        SmartDashboard.putBoolean("downButton", false);
        // SmartDashboard.putBoolean("topSensor", false);
        // SmartDashboard.putBoolean("bottomSensor", false);

        gamePieceController = GamePieceController.getInstance();

        climber.stopMotors();
    }

    public void periodic() {

        boolean climberEnabled = SmartDashboard.getBoolean("isEnabled", false);
        boolean climberUpButtonPressed = SmartDashboard.getBoolean("upButton", false);
        boolean climberDownButtonPressed = SmartDashboard.getBoolean("downButton", false);
        // boolean climberTopSensor = SmartDashboard.getBoolean("topSensor", false);
        // boolean climberBottomSensor = SmartDashboard.getBoolean("bootomSensor",
        // false);

        if (climberEnabled) { 
            gamePieceController.climberForceEnabled = true;
        } else {
            gamePieceController.climberForceEnabled = false;
        }

        if (climberUpButtonPressed) {
            gamePieceController.climberUpButtonPressed = true;
        } else {
            gamePieceController.climberUpButtonPressed = false;
        }

        if (climberDownButtonPressed) {
            gamePieceController.climberDownButtonPressed = true;
        } else {
            gamePieceController.climberDownButtonPressed = false;
        }

        // if (climberTopSensor) {
        // climber.hasHighestPoint = true;
        // } else {
        // climber.hasHighestPoint = false;
        // }

        // if (climberBottomSensor) {
        // climber.hasLowestPoint = true;
        // } else {
        // climber.hasLowestPoint = false;
        // }

    }
}