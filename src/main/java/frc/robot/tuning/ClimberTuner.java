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
    boolean useVelocity = false;

    ClimberTuner() {
        climber = ClimberAL.getInstance();
    }

    public void init() {
        SmartDashboard.putNumber("Speed", 0);
        SmartDashboard.putNumber("Climber Speed", 0);
        SmartDashboard.putNumber("Climber Position", 0);
        SmartDashboard.putBoolean("Lock Solenoid", false);
        SmartDashboard.putBoolean("Top Sensor", false);
        SmartDashboard.putBoolean("Bottom Sensor", false);

        double kP = SmartDashboard.getNumber("Climber P", 0);
        double kI = SmartDashboard.getNumber("Climber I", 0);
        double kD = SmartDashboard.getNumber("Climber D", 0);
        double kF = SmartDashboard.getNumber("Climber F", 0);
        double maxVelocity = SmartDashboard.getNumber("Climber Max Velocity", 0);
        useVelocity = SmartDashboard.getBoolean("Use Velocity", false);

        SmartDashboard.putNumber("Climber P", kP);
        SmartDashboard.putNumber("Climber I", kI);
        SmartDashboard.putNumber("Climber D", kD);
        SmartDashboard.putNumber("Climber F", kF);
        SmartDashboard.putNumber("Climber Max Velocity", maxVelocity);
        SmartDashboard.putBoolean("Use Velocity", useVelocity);

        climber.setClimberPIDF(kP, kI, kD, kF, maxVelocity);

        climber.stopMotors();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);
        boolean lockSolenoid = SmartDashboard.putBoolean("Lock Solenoid", false);

        if (useVelocity) {
            climber.setClimb(speed);
        } else {
            climber.setSpeed(speed);
        }

        if (lockSolenoid) {
            climber.climberLock();
        } else {
            climber.climberUnlock();
        }

        SmartDashboard.putNumber("Climber Speed", climber.getSpeed());
        SmartDashboard.putNumber("Climber Position", climber.getPosition());
        SmartDashboard.putBoolean("Top Sensor", climber.getTopSensor());
        SmartDashboard.putBoolean("Bottom Sensor", climber.getBottomSensor());
    }
}
