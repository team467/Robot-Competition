package frc.robot.tuning;

import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.RobotMap;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnDegreesTuner implements Tuner {

    private static final Logger LOGGER
        = RobotLogManager.getMainLogger(TuneController.class.getName());
    Drive drive;

    Timer timer = new Timer();

    int iteration;

    TurnDegreesTuner() {
        drive = Drive.getInstance();
    }


    @Override
    public void init() {
        LOGGER.info("Init Drive Constant Test");
        SmartDashboard.putNumber("Turn(degrees)", 0);
        SmartDashboard.putNumber("Speed", 0);
        SmartDashboard.putBoolean("turn", false);
        SmartDashboard.putBoolean("reset timer", false);
        timer.reset();
        timer.start();


    }

    @Override
    public void periodic() {
        double Turn = SmartDashboard.getNumber("Turn(degrees)", 0);
        double Speed = SmartDashboard.getNumber("Speed", 0);        
        boolean turnActivator = SmartDashboard.getBoolean("turn", false);
        boolean resetTimer = SmartDashboard.getBoolean("reset timer", false);
        LOGGER.info("Speed: {}, Turn: {}", Speed, Turn);


        if(resetTimer){
            timer.reset();
            timer.start();
        }

        if(turnActivator){
           if(timer.get() < 0.1){
                drive.arcadeDrive(Speed, Turn);
                LOGGER.error(timer.get());
            } else {
               timer.stop();
               drive.arcadeDrive(0, 0);
               turnActivator = false;
            }
        }
        SmartDashboard.putNumber("Right Speed", -drive.getRightVelocity());
        SmartDashboard.putNumber("Left Speed", drive.getLeftVelocity());
    }

}
