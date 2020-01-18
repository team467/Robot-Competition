package frc.robot.tuning;

import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.RobotMap;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnDegreesTuner implements Tuner {

    private static final Logger LOGGER
        = RobotLogManager.getMainLogger(TuneController.class.getName());
    Drive drive;

    TurnDegreesTuner() {
        drive = Drive.getInstance();
    }


    @Override
    public void init() {
        LOGGER.info("Init Drive Constant Test");
        SmartDashboard.putNumber("Turn(degrees)", 0);
        SmartDashboard.putNumber("Speed", 0);

    }

    @Override
    public void periodic() {
        double Turn = SmartDashboard.getNumber("Turn(degrees)", 0);
        double Speed = SmartDashboard.getNumber("Speed", 0);        

        LOGGER.info("Speed: {}, Turn: {}", Speed, Turn);

        drive.arcadeDrive(0, Speed);

        

    }

}
