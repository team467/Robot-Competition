
package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

public class ShooterTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    ShooterAL shooter;
    TalonSpeedControllerGroup shooterMotor;
    boolean useVelocity;

    ShooterTuner() {
        shooter = ShooterAL.getInstance();
        shooterMotor = shooter.getMotor();
    }

    public void init() {
        SmartDashboard.putNumber("Speed", 0);

        SmartDashboard.putNumber("Shooter Current", 0);
        SmartDashboard.putNumber("Shooter Voltage", 0);
        SmartDashboard.putNumber("Shooter Speed", 0);
        SmartDashboard.putNumber("Shooter Position", 0);
        SmartDashboard.putNumber("Left Hood Angle", RobotMap.HOOD_LEFT_STARTING_POSITION);
        SmartDashboard.putNumber("Right Hood Angle", RobotMap.HOOD_RIGHT_STARTING_POSITION);
        SmartDashboard.putNumber("Hood Angle", 0.5);

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

        SmartDashboard.putBoolean("Shoot", false);
        SmartDashboard.putBoolean("Left Servo", false);
        SmartDashboard.putBoolean("Right Servo", false);
        SmartDashboard.putBoolean("Control Both", false);

        shooter.flyWheelPIDF(kP, kI, kD, kF, kMaxVelocity);

        shooter.stop();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);
        boolean startShooting = SmartDashboard.getBoolean("Shoot", false);
        double leftAngle = SmartDashboard.getNumber("Left Hood Angle", RobotMap.HOOD_LEFT_STARTING_POSITION);
        double rightAngle = SmartDashboard.getNumber("Right Hood Angle", RobotMap.HOOD_LEFT_STARTING_POSITION);
        boolean leftServo = SmartDashboard.getBoolean("Left Servo", false);
        boolean rightServo = SmartDashboard.getBoolean("Right Servo", false);
        double hoodAngle = SmartDashboard.getNumber("Hood Angle", 0.5);
        boolean useBoth = SmartDashboard.getBoolean("Control Both", false);

        if (useVelocity) {
            shooter.rampToSpeed(speed);

            if (RobotMap.HAS_SHOOTER_LEDS) {
                double currentVel = shooter.getMotor().velocity();
                double setVel = shooter.getMotor().closedLoopTarget();

                double ledFillPercent = Math.max(0, Math.min(1, Math.abs(speed)));
                Color ledColor = Color.kBlueViolet;
                if (currentVel/setVel > 0.9) {
                    ledColor = Color.kDarkGoldenrod;
                }
                int fillLeds = (int) (RobotMap.SHOOTER_LED_AMOUNT * ledFillPercent)-1;
                if (fillLeds >= 0) {
                    shooter.fillStrip(ledColor, fillLeds);
                } else {
                    shooter.clearStrip();
                }
            }
        } else {
            shooterMotor.set(ControlMode.PercentOutput, speed);

            if (RobotMap.HAS_SHOOTER_LEDS) {
                double ledFillPercent = Math.max(0, Math.min(1, Math.abs(speed)));
                int fillLeds = (int) (RobotMap.SHOOTER_LED_AMOUNT * ledFillPercent)-1;
                if (fillLeds >= 0) {
                    shooter.fillStrip(0, 0, 255, fillLeds);
                } else {
                    shooter.clearStrip();
                }
            }
        }

        if (useBoth) {
            shooter.setHoodAngle(hoodAngle, hoodAngle);
        } else {
            if (leftServo) {
               // shooter.setLeftHoodAngle(leftAngle);
            }
    
            if (rightServo) {
               // shooter.setRightHoodAngle(rightAngle);
            }
        }
        

        if (startShooting) {
            LOGGER.error("trigger");
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