package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import frc.robot.gamepieces.AbstractLayers.ClimberAL.climberSpeed;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ClimberTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    ClimberAL climber;

    ClimberTuner() {
        climber = climber.getInstance();

    }

    public void init() {
        SmartDashboard.putNumber("Climber Speed", 0);
        SmartDashboard.putNumber("Climber Position", 0);
        SmartDashboard.putBoolean("Lock Solenoid", false);

        double kP = SmartDashboard.getNumber("Climber P", 0);
        double kI = SmartDashboard.getNumber("Climber I", 0);
        double kD = SmartDashboard.getNumber("Climber D", 0);
        double kF = SmartDashboard.getNumber("Climber F", 0);
        double maxVelocity = SmartDashboard.getNumber("Climber Max Velocity", 0);

        SmartDashboard.putNumber("Climber P", kP);
        SmartDashboard.putNumber("Climber I", kI);
        SmartDashboard.putNumber("Climber D", kD);
        SmartDashboard.putNumber("Climber F", kF);

        climber.setClimberPIDF(kP, kI, kD, kF, maxVelocity);

        climber.stopMotors();
    }

    public void periodic() {
        double climberSpeed = SmartDashboard.getNumber("Speed", 0);
        double climberPosition = SmartDashboard.getNumber("Position", 0);
        double solenoidLock = SmartDashboard.getNumber("Solenoid Lock", 0);
        boolean climb = SmartDashboard.getBoolean("Climb", false);
        if(climb){
            climber.set(climberSpeed.UP);
        }else{
            climber.set(climberSpeed.OFF);
        }
        climber.
    }
}
