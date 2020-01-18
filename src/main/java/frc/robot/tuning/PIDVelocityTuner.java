package frc.robot.tuning;

import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.ControlType;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDVelocityTuner implements Tuner {

    private static final Logger LOGGER
        = RobotLogManager.getMainLogger(TuneController.class.getName());
    Drive drive;

    PIDVelocityTuner() {
        drive = Drive.getInstance();
    }

    @Override
    public void init() {
        LOGGER.info("Init Drive Straight Test");
        SmartDashboard.putNumber("Speed", 0);
        drive.zero();

        double leftP = SmartDashboard.getNumber("Left P", 0);
        double leftI = SmartDashboard.getNumber("Left I", 0);
        double leftD = SmartDashboard.getNumber("Left D", 0);
        double leftF = SmartDashboard.getNumber("Left F", 0);
        double leftMaxVelocity = SmartDashboard.getNumber("Left Max Velocity", 0);

        double rampRate = SmartDashboard.getNumber("Ramp Rate", 1);

        double rightP = SmartDashboard.getNumber("Right P", 0);
        double rightI = SmartDashboard.getNumber("Right I", 0);
        double rightD = SmartDashboard.getNumber("Right D", 0);
        double rightF = SmartDashboard.getNumber("Right F", 0);
        double rightMaxVelocity = SmartDashboard.getNumber("Right Max Velocity", 0);

        SmartDashboard.putNumber("Left P", leftP);
        SmartDashboard.putNumber("Left I", leftI);
        SmartDashboard.putNumber("Left D", leftD);
        SmartDashboard.putNumber("Left F", leftF);
        SmartDashboard.putNumber("Left Max Velocity", leftMaxVelocity);

        SmartDashboard.putNumber("Ramp Rate", rampRate);

        SmartDashboard.putNumber("Right P", rightP);
        SmartDashboard.putNumber("Right I", rightI);
        SmartDashboard.putNumber("Right D", rightD);
        SmartDashboard.putNumber("Right F", rightF);
        SmartDashboard.putNumber("Right Max Velocity", rightMaxVelocity);


        drive.setLeftPIDFs(leftP, leftI, leftD, leftF, leftMaxVelocity);
        drive.setRightPIDFs(rightP, rightI, rightD, rightF, rightMaxVelocity);
        drive.setClosedSetRampRate(rampRate);
    }

    @Override
    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);
        LOGGER.info("speed Value: {}", speed);
        drive.set(ControlType.kVelocity, speed);
    }

}
