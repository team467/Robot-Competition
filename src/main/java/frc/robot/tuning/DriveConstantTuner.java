package frc.robot.tuning;

import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.RobotMap;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveConstantTuner implements Tuner {

    private static final Logger LOGGER
        = RobotLogManager.getMainLogger(TuneController.class.getName());
    Drive drive;

    DriveConstantTuner() {
        drive = Drive.getInstance();
    }


    @Override
    public void init() {
        LOGGER.info("Init Drive Constant Test");

    }

    @Override
    public void periodic() {
        double tuningValue = Double.parseDouble(SmartDashboard.getString("DB/String 5", "0.0"));
        LOGGER.info("Tuning Value: {}", tuningValue);
        drive.arcadeDrive(tuningValue, 0, true);

    }

}
