package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.Shooter;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

public class ShooterTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    Shooter shooter;
    TalonSpeedControllerGroup shooterMotor;
    boolean useVelocity;

    ShooterTuner() {
        shooter = Shooter.getInstance();
        shooterMotor = shooter.getMotor();
    }

    public void init() {
        SmartDashboard.putNumber("Speed", 0);

        SmartDashboard.putNumber("Shooter Current", 0);
        SmartDashboard.putNumber("Shooter Voltage", 0);
        SmartDashboard.putNumber("Shooter Speed", 0);
        SmartDashboard.putNumber("Shooter Position", 0);

        useVelocity = SmartDashboard.getBoolean("Use Velocity", false);
        double kP = SmartDashboard.getNumber("Shooter P", 0);
        double kI = SmartDashboard.getNumber("Shooter I", 0);
        double kD = SmartDashboard.getNumber("Shooter D", 0);
        double kF = SmartDashboard.getNumber("Shooter F", 0);
        double kMaxVelocity = SmartDashboard.getNumber("Shooter Max Velocity", 0);

        SmartDashboard.putBoolean("Use Velocity", useVelocity);
        SmartDashboard.putNumber("Shooter P", kP);
        SmartDashboard.putNumber("Shooter I", kI);
        SmartDashboard.putNumber("Shooter D", kD);
        SmartDashboard.putNumber("Shooter F", kF);
        SmartDashboard.putNumber("Shooter Max Velocity", kMaxVelocity);

        SmartDashboard.putBoolean("Start Shooting", false);

        shooter.flyWheelPIDF(kP, kI, kD, kF, kMaxVelocity);

        shooter.stop();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);
        boolean startShooting = SmartDashboard.getBoolean("Start Shooting", false);

        if (useVelocity) {
            shooter.rampToSpeed(speed);

            if (RobotMap.HAS_SHOOTERLEDS) {
                double currentVel = shooter.getMotor().velocity();
                double setVel = shooter.getMotor().closedLoopTarget();

                double ledFillPercent = Math.min(0, Math.max(1, setVel/currentVel));
                int fillLeds = (int) (RobotMap.SHOOTER_LED_AMOUNT * ledFillPercent)-1;
                if (fillLeds >= 0) {
                    shooter.fillStrip(0, 0, 255, fillLeds);
                } else {
                    shooter.clearStrip();
                }
            }
        } else {
            shooterMotor.set(ControlMode.PercentOutput, speed);

            if (RobotMap.HAS_SHOOTERLEDS) {
                double ledFillPercent = Math.max(0, Math.min(1, Math.abs(speed)));
                int fillLeds = (int) (RobotMap.SHOOTER_LED_AMOUNT * ledFillPercent)-1;
                if (fillLeds >= 0) {
                    shooter.fillStrip(0, 0, 255, fillLeds);
                } else {
                    shooter.clearStrip();
                }
            }
        }

        if (startShooting) {
            shooter.startShooting();
        } else {
            shooter.stopShooting();
        }

        SmartDashboard.putNumber("Shooter Current", shooterMotor.current());
        SmartDashboard.putNumber("Shooter Voltage", shooterMotor.velocity());
        SmartDashboard.putNumber("Shooter Speed", shooterMotor.velocity());
        SmartDashboard.putNumber("Shooter Position", shooterMotor.position());
    }
}