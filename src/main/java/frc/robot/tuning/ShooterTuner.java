package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.Shooter;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

        shooter.flyWheelPIDF(kP, kI, kD, kF, kMaxVelocity);

        shooter.stop();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);

        if (useVelocity) {
            shooter.setSpeed(speed);
        } else {
            shooterMotor.set(speed);
        }

        SmartDashboard.putNumber("Shooter Current", shooterMotor.current());
        SmartDashboard.putNumber("Shooter Voltage", shooterMotor.velocity());
        SmartDashboard.putNumber("Shooter Speed", shooterMotor.velocity());
    }
}