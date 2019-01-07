package frc.robot.gamepieces;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DriverStation;

import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class Climber {
  private static final Logger LOGGER = RobotLogManager.getMainLogger(Climber.class.getName());
  private static Climber instance;
  WpiTalonSrxInterface climbMotorLeader;
  WpiTalonSrxInterface climbMotorFollower1;

  private TalonSpeedControllerGroup climbController;

  public Climber() {
    if (!RobotMap.HAS_CLIMBER) {
      return;
    }
    
    if (RobotMap.HAS_CLIMBER) {
      climbMotorLeader = TalonProxy.create(RobotMap.CLIMB_MOTOR_CONTROLLER_LEADER);
      climbMotorFollower1 = TalonProxy.create(RobotMap.CLIMB_MOTOR_CONTROLLER_FOLLOWER1);
      climbController 
          = new TalonSpeedControllerGroup(ControlMode.PercentOutput, false, false,
            climbMotorLeader, climbMotorFollower1);
      LOGGER.info("Created climber Motors");
    } else {
      LOGGER.info("Not enough climb motors, no climb capabilities");
    }
  }
  
  public static Climber getInstance() {
    if (instance == null) {
      if (RobotMap.HAS_CLIMBER) {
        instance = new Climber();
      }
    }
    return instance;
  }

  public void climbUp() {
    if (DriverStation.getInstance().getMatchTime() <= 30.0) {
      climbController.set(ControlMode.PercentOutput, RobotMap.CLIMBER_SPEED);
      LOGGER.info("Climbing up.");
    } else {
      LOGGER.info("Too early to climb up.");
    }
  }

  public void neutral() {
    climbController.set(ControlMode.PercentOutput, 0);
    //LOGGER.info("Climber stopped");
  }

  public void setOpenLoopRamp() {
    climbController.setOpenLoopRamp(RobotMap.CLIMBER_RAMP_TIME);
    
  }
}