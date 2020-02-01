package frc.robot.tuning;

import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.RobotMap;
 

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
public class DriveConstantTuner implements Tuner {

    private static final Logger LOGGER
        = RobotLogManager.getMainLogger(TuneController.class.getName());
    Drive drive;
    Timer timer;

    DriveConstantTuner() {
        drive = Drive.getInstance();
    }


    @Override
    public void init() {
        LOGGER.info("Init Drive Constant Test");
        SmartDashboard.putNumber("Speed", 0);
        timer = new Timer();
        LOGGER.info("Drive left CPR: {}",drive.getLeftCPR());
        LOGGER.info("Drive Right CPR: {}", drive.getRightCPR());
        drive.setClosedSetRampRate(0.4);
        timer.start();

    }

    @Override
    public void periodic() {
        double tuningValue = SmartDashboard.getNumber("Speed", 0);
        LOGGER.trace("Tuning Value: {}", tuningValue);
        if(timer.get() <= 2.4){
        drive.arcadeDrive(0.8, 0, true);
        LOGGER.info("Left Rotations: {}, right Rotations: {}" ,drive.getLeftVelocity() ,drive.getRightVelocity());
        } else {
            drive.arcadeDrive(0, 0, true);
        }


    }

}
