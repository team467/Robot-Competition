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

    ShooterAL ShooterAL;
    TalonSpeedControllerGroup ShooterALMotor;
    boolean useVelocity;

    ShooterTuner() {
        ShooterAL = ShooterAL.getInstance();
        ShooterALMotor = ShooterAL.getMotor();
    }

    public void init() {
        SmartDashboard.putNumber("Speed", 0);

        SmartDashboard.putNumber("ShooterAL Current", 0);
        SmartDashboard.putNumber("ShooterAL Voltage", 0);
        SmartDashboard.putNumber("ShooterAL Speed", 0);
        SmartDashboard.putNumber("ShooterAL Position", 0);

        useVelocity = SmartDashboard.getBoolean("Use Velocity", false);
        double kP = SmartDashboard.getNumber("ShooterAL P", 0);
        double kI = SmartDashboard.getNumber("ShooterAL I", 0);
        double kD = SmartDashboard.getNumber("ShooterAL D", 0);
        double kF = SmartDashboard.getNumber("ShooterAL F", 0);
        double kMaxVelocity = SmartDashboard.getNumber("ShooterAL Max Velocity", 0);

        SmartDashboard.putBoolean("Use Velocity", useVelocity);
        SmartDashboard.putNumber("ShooterAL P", kP);
        SmartDashboard.putNumber("ShooterAL I", kI);
        SmartDashboard.putNumber("ShooterAL D", kD);
        SmartDashboard.putNumber("ShooterAL F", kF);
        SmartDashboard.putNumber("ShooterAL Max Velocity", kMaxVelocity);

        SmartDashboard.putBoolean("Shoot", false);

        ShooterAL.flyWheelPIDF(kP, kI, kD, kF, kMaxVelocity);

        ShooterAL.stop();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);
        boolean startShooting = SmartDashboard.getBoolean("Shoot", false);

        if (useVelocity) {
            ShooterAL.rampToSpeed(speed);

            if (RobotMap.HAS_SHOOTERLEDS) {
                double currentVel = ShooterAL.getMotor().velocity();
                double setVel = ShooterAL.getMotor().closedLoopTarget();

                double ledFillPercent = Math.min(0, Math.max(1, setVel/currentVel));
                int fillLeds = (int) (RobotMap.SHOOTER_LED_AMOUNT * ledFillPercent)-1;
                if (fillLeds >= 0) {
                    ShooterAL.fillStrip(0, 0, 255, fillLeds);
                } else {
                    ShooterAL.clearStrip();
                }
            }
        } else {
            ShooterALMotor.set(ControlMode.PercentOutput, speed);

            if (RobotMap.HAS_SHOOTERLEDS) {
                double ledFillPercent = Math.max(0, Math.min(1, Math.abs(speed)));
                int fillLeds = (int) (RobotMap.SHOOTER_LED_AMOUNT * ledFillPercent)-1;
                if (fillLeds >= 0) {
                    ShooterAL.fillStrip(0, 0, 255, fillLeds);
                } else {
                    ShooterAL.clearStrip();
                }
            }
        }

        if (startShooting) {
            ShooterAL.startShooting();
        } else {
            ShooterAL.stopShooting();
        }

        SmartDashboard.putNumber("ShooterAL Current", ShooterALMotor.current());
        SmartDashboard.putNumber("ShooterAL Voltage", ShooterALMotor.velocity());
        SmartDashboard.putNumber("ShooterAL Speed", ShooterALMotor.velocity());
        SmartDashboard.putNumber("ShooterAL Position", ShooterALMotor.position());
    }
}