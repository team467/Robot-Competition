package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AdvancedTuner implements Tuner {

  private static final Logger LOGGER
      = RobotLogManager.getMainLogger(TuneController.class.getName());

  Drive drive;

  AdvancedTuner() {
    drive = Drive.getInstance();
  }

    public void init() {
      SmartDashboard.putNumber("Speed", 0);
      
      SmartDashboard.putNumber("Left Current", 0);
      SmartDashboard.putNumber("Left Distance", 0);
      SmartDashboard.putNumber("Left Speed", 0);
      SmartDashboard.putNumber("Left CPR", 0);

      SmartDashboard.putNumber("Right Current", 0);
      SmartDashboard.putNumber("Right CPR", 0);
      SmartDashboard.putNumber("Right Distance", 0);
      SmartDashboard.putNumber("Right Speed", 0);
      drive.zero();
    }

    public void periodic() {
      double speed = SmartDashboard.getNumber("Speed", 0);
      drive.tankDrive(speed, speed);
      drive.getLeftCPR();

      SmartDashboard.putNumber("Left Current", drive.getLeftCurrent());
      SmartDashboard.putNumber("Left Distance", drive.getLeftDistance());
      SmartDashboard.putNumber("Left Speed", drive.getLeftVelocity());
      SmartDashboard.putNumber("Left CPR", drive.getLeftCPR());

      SmartDashboard.putNumber("Right Current", drive.getRightCurrent());
      SmartDashboard.putNumber("Right CPR", drive.getRightCPR());
      SmartDashboard.putNumber("Right Distance", drive.getRightDistance());
      SmartDashboard.putNumber("Right Speed", drive.getRightVelocity());
    }
}