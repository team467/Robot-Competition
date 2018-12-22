package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;

import edu.wpi.first.wpilibj.SpeedController;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

//TalonSpeedControllerGroup
public class TalonSpeedControllerGroup implements SpeedController {
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(TalonSpeedControllerGroup.class.getName());
  private WpiTalonSrxInterface leader;
  private WpiTalonSrxInterface follower1;
  private WpiTalonSrxInterface follower2;

  private int previousSensorPosition;

  private ControlMode controlMode = ControlMode.PercentOutput;

  public TalonSpeedControllerGroup() {
    leader = null;
    follower1 = null;
    follower2 = null;
  }

  public TalonSpeedControllerGroup(ControlMode controlMode, 
      boolean sensorIsInverted, boolean motorIsInverted,
      WpiTalonSrxInterface leader, WpiTalonSrxInterface follower1, WpiTalonSrxInterface follower2) {
    if (!RobotMap.HAS_WHEELS) {
      leader = null;
      follower1 = null;
      follower2 = null;
      LOGGER.trace("No drive system");
      return;
    }
    this.leader = leader;
    this.follower1 = follower1;
    this.follower2 = follower2;
    this.controlMode = controlMode;

    initMotor(this.leader);
    if (follower1 != null) {
      initMotor(this.follower1);
    }
    if (follower2 != null) {
      initMotor(this.follower2);
    }

    //only have sensor on leader
    leader.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
    leader.setSensorPhase(sensorIsInverted);
    leader.setInverted(motorIsInverted);

    zero();
  }

  public TalonSpeedControllerGroup(ControlMode controlMode, 
      boolean sensorIsInverted, boolean motorIsInverted,
      WpiTalonSrxInterface leader, WpiTalonSrxInterface follower1) {
    this(controlMode, sensorIsInverted, motorIsInverted, leader, follower1, null);
  }

  public TalonSpeedControllerGroup(ControlMode controlMode, 
      boolean sensorIsInverted, boolean motorIsInverted,
      WpiTalonSrxInterface leader) {
    this(controlMode, sensorIsInverted, motorIsInverted, leader, null, null);
  }

  private void initMotor(WpiTalonSrxInterface talon) {
    talon.set(ControlMode.PercentOutput, 0);
    talon.selectProfileSlot(0, 0);
    talon.configAllowableClosedloopError(0, RobotMap.POSITION_ALLOWABLE_CLOSED_LOOP_ERROR, 0);

    //Note: -1 and 1 are the max outputs
    talon.configNominalOutputReverse(0.0, 0);
    talon.configNominalOutputForward(0.0, 0);
    talon.configPeakOutputForward(1.0, 0);
    talon.configPeakOutputReverse(-1.0, 0);

    talon.configOpenloopRamp(0.2, RobotMap.TALON_TIMEOUT);
    //talon.configClosedloopRamp(1.0, RobotMap.TALON_TIMEOUT);
    
    talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 0);
    talon.configVelocityMeasurementWindow(2, 0);
  }

  public void logClosedLoopErrors(String side) {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.debug("No CLosed Loop errors");
      return;
    }
    LOGGER.debug("side: {} Vel = {} Pos = {} Err = {}", side, leader.getSelectedSensorVelocity(0),
        leader.getSelectedSensorPosition(0), leader.getClosedLoopError(0));
  }

  public double getSensorVelocity() {
    return leader.getSelectedSensorVelocity(0);
  }

  public double getSensorPosition() {
    return leader.getSelectedSensorPosition(0);
  }

  public void pidf(int slotId, double p, double i, double d, double f) {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.debug("No PIDF");
      return;
    }
    leader.config_kP(slotId, p, RobotMap.TALON_TIMEOUT);
    leader.config_kI(slotId, i, RobotMap.TALON_TIMEOUT);
    leader.config_kD(slotId, d, RobotMap.TALON_TIMEOUT);
    leader.config_kF(slotId, f, RobotMap.TALON_TIMEOUT);

    leader.configNeutralDeadband(0.04, RobotMap.TALON_TIMEOUT);
  }

  public void selectPidSlot(int slot) {
    leader.selectProfileSlot(slot, 0);
  }

  public void zero() {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.trace("No drive system");
      return;
    }
    previousSensorPosition = 0;
    leader.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
  }

  @Override
  public void disable() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }
    leader.disable();
    if (follower1 != null) {
      follower1.disable();
    }
    if (follower2 != null) {
      follower2.disable();
    }
  }

  @Override
  public double get() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return 0.0;
    }
    return leader.get();
  }

  @Override
  public void pidWrite(double output) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }
    leader.pidWrite(output);
    if (follower1 != null) {
      follower1.follow(leader);
    }
    if (follower2 != null) {
      follower2.follow(leader);
    }
  }

  @Override
  public void set(double speed) {
    set(controlMode, speed);
  }

  public void set(ControlMode controlMode, double outputValue) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    leader.set(controlMode, outputValue);
    if (follower1 != null) {
      follower1.follow(leader);
    }
    if (follower2 != null) {
      follower2.follow(leader);
    }
  }

  public void configPeakOutput(double percentOut) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    leader.configPeakOutputForward(percentOut, RobotMap.TALON_TIMEOUT);
    leader.configPeakOutputReverse(-percentOut, RobotMap.TALON_TIMEOUT);
  }

  @Override
  public void setInverted(boolean isInverted) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    leader.setInverted(isInverted);
    if (follower1 != null) {
      follower1.setInverted(isInverted);
    }
    if (follower2 != null) {
      follower2.setInverted(isInverted);
    }
  }

  @Override
  public boolean getInverted() {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.trace("No drive system");
      return false;
    }
    return leader.getInverted();
  }

  @Override
  public void stopMotor() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    leader.stopMotor();
    if (follower1 != null) {
      follower1.stopMotor();
    }
    if (follower2 != null) {
      follower2.stopMotor();
    }
  }

  public boolean isStopped() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return true;
    }

    int leaderSensorPosition = leader.getSelectedSensorPosition(0);
    boolean isStopped = false;

    if (leader.getControlMode() == ControlMode.Disabled) {
      isStopped =  true;
    } else if (leaderSensorPosition == previousSensorPosition) {
      isStopped = true;
    }

    previousSensorPosition = leaderSensorPosition;
    return isStopped;
  }

  public int sensorPosition() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return 0;
    }

    return leader.getSelectedSensorPosition(0);
  }

  public int sensorSpeed() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return 0;
    }

    return leader.getSelectedSensorVelocity(0);
  }

  public void setOpenLoopRamp(double ramp) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    leader.configOpenloopRamp(ramp, RobotMap.TALON_TIMEOUT);
  }

  public void movePosition(double targetDistance) {
    if (leader == null) {
      LOGGER.trace("No Drive System");
      return;
    }

    double distanceMoved = leader.getSelectedSensorPosition(0);
    double distanceToGo;

    if (targetDistance > 0) {
      distanceToGo = Math.min(targetDistance, distanceMoved + 1024);
    } else {
      distanceToGo = Math.max(targetDistance, distanceMoved - 1024);
    }

    LOGGER.info("target=" + targetDistance + " sensor=" + distanceMoved + " toGo=" + distanceToGo);
    set(ControlMode.Position, distanceToGo);
  }
}
