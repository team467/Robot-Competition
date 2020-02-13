package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.Shooter;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

public class AutoShooterTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    Shooter shooter;
    TalonSpeedControllerGroup shooterMotor;
    NetworkTableEntry distanceTable;
    NetworkTableEntry hasDistanceTable;


    AutoShooterTuner() {
        shooter = Shooter.getInstance();
        shooterMotor = shooter.getMotor();
    }

    public void init() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("vision");
        distanceTable = table.getEntry("DistanceFromTarget");
        hasDistanceTable = table.getEntry("haveDistance");

        SmartDashboard.putNumber("Shooter Current", 0);
        SmartDashboard.putNumber("Shooter Voltage", 0);
        SmartDashboard.putNumber("Shooter Speed", 0);
        SmartDashboard.putNumber("Shooter Position", 0);
        SmartDashboard.putNumber("Speed", 0);
        SmartDashboard.putBoolean("Shoot", false);
        SmartDashboard.putBoolean("Flywheel", false);
        
        shooter.stop();
    }

    public void periodic() {
        double distance = distanceTable.getDouble(0);
        boolean hasDistance = hasDistanceTable.getBoolean(false);
        boolean startShooting = SmartDashboard.getBoolean("Shoot", false);
        boolean startFlywheel = SmartDashboard.getBoolean("Flywheel", false);
        double speed = (0.019 * distance) + 0.827;

        if (startFlywheel) {
            if (hasDistance) {
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
            }
        } else {
            shooter.stop();
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
        SmartDashboard.putNumber("Speed", speed);
    }
}