package frc.robot.drive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;


import edu.wpi.first.wpilibj.SpeedController;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class SparkMaxSpeedControllerGroup implements SpeedController {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(SparkMaxSpeedControllerGroup.class.getName());

  private String subsystem = "Telemetry";
  private String name = "Generic Talon Group";

  private CANSparkMax leader;
  private CANSparkMax follower1;
  private CANSparkMax follower2;

  private CANEncoder mEncoder;
  private CANPIDController mPidController;
  private ControlType controlType = ControlType.kVoltage;

  private int pidControlSlot;
  private double previousSensorPosition;
  private double maxVelocity;


  public SparkMaxSpeedControllerGroup() {
    leader = null;
    follower1 = null;
  }

  public SparkMaxSpeedControllerGroup(String name, ControlType controlType, boolean sensorIsInverted, 
      boolean motorIsInverted, CANSparkMax leader, CANSparkMax follower1,
      CANSparkMax follower2) {
    this.name = name;
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
    this.controlType = controlType;

    initMotor(this.leader);
    if (follower1 != null) {
      initMotor(this.follower1);
      initMotor(this.follower2);
    }

    // only have sensor on leader
    this.mEncoder = leader.getEncoder();
    this.mPidController = leader.getPIDController();

    
    mEncoder.setInverted(sensorIsInverted);
    leader.setInverted(motorIsInverted);

    registerMetrics();
    zero();
  }

  public SparkMaxSpeedControllerGroup(String name, ControlType controlType, boolean sensorIsInverted,
  boolean motorIsInverted, CANSparkMax leader, CANSparkMax follower1) {
    this(name, controlType, sensorIsInverted, motorIsInverted, leader, follower1, null);
  }

  public SparkMaxSpeedControllerGroup(String name, ControlType controlType, boolean sensorIsInverted,
  boolean motorIsInverted, CANSparkMax leader) {
    this(name, controlType, sensorIsInverted, motorIsInverted, leader, null, null);
  }

  private void initMotor(CANSparkMax sparkMax) {
    sparkMax.setIdleMode(IdleMode.kBrake);

    sparkMax.set(0);
    mPidController.setReference(0, controlType.kVoltage);
    //talon.selectProfileSlot(0, 0);
    //talon.configAllowableClosedloopError(0, RobotMap.POSITION_ALLOWABLE_CLOSED_LOOP_ERROR, 0);

    // Note: -1 and 1 are the max outputs
//    sparkMax.setSmartCurrentLimit(0, 0);
    

    sparkMax.setOpenLoopRampRate(0.2);
    sparkMax.setCANTimeout(RobotMap.TALON_TIMEOUT);
    mPidController.setOutputRange(-1,1);
    // talon.configClosedloopRamp(1.0, RobotMap.TALON_TIMEOUT);

    // talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 0);
    // talon.configVelocityMeasurementWindow(2, 0);
  }

  public void logClosedLoopErrors() {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.debug("No CLosed Loop errors");
      return;
    }
    LOGGER.debug("side: {} Vel = {} Pos = {} Err = {}", name, leader.getEncoder().getVelocity(),
        leader.getEncoder().getPosition(), "no current closed loop error implemented");
  }

  public void pidf(int slotId, double p, double i, double d, double f, double maxVelocity) {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.debug("No PIDF");
      return;
    }
    mPidController.setP(p, slotId);
    mPidController.setI(i, slotId);
    mPidController.setD(d, slotId);
    mPidController.setFF(f, slotId);

    this.maxVelocity = maxVelocity;
    //leader.configNeutralDeadband(0.04, RobotMap.TALON_TIMEOUT); TODO: figure out if deadband exists
  }

  public void selectPidSlot(int slot) {
    this.pidControlSlot = slot;
  }

  public void zero() {
    if (!RobotMap.HAS_WHEELS) {
      LOGGER.trace("No drive system");
      return;
    }
    previousSensorPosition = 0;
    mEncoder.setPosition(0);
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
  }

  @Override
  public void set(double speed) {
    set(speed);
  }

  public void set(ControlType controlType, double outputValue) {
    if (leader == null) {
      LOGGER.error("No drive system");
      return;
    }

    LOGGER.debug("Output to {} drive is {} in mode: {}", name, outputValue, controlType);

    if (controlType == ControlType.kVelocity) {
      outputValue *= maxVelocity;
    }

    mPidController.setReference(0, controlType);
    leader.set(outputValue);
    if (follower1 != null) {
      follower1.follow(leader);
    }

    // LOGGER.debug("name: {} Requested Velocity: {} Velocity = {} Error: {}", 
    //     leader.getName(), box(outputValue), 
    //     box(leader.getSelectedSensorVelocity(0)), box(leader.getClosedLoopError(0)));
    // LOGGER.debug("Name: {}, Error: {}, Output Voltage: {}, Output Percent; {}", 
    //     name, box(leader.getClosedLoopError(0)), box(leader.getMotorOutputVoltage()), 
    //     box(leader.getMotorOutputPercent()));
  }

  public void configPeakOutput(double percentOut) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    //TODO: not sure if this stilll is relevant
    // leader.configPeakOutputForward(percentOut, RobotMap.TALON_TIMEOUT);
    // leader.configPeakOutputReverse(-percentOut, RobotMap.TALON_TIMEOUT);
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
  }

  public boolean isStopped() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return true;
    }

    double leaderSensorPosition = mEncoder.getPosition();
    boolean isStopped = false;

    if (leader.get() == 0) {
      isStopped = true;
    } else if (leaderSensorPosition == previousSensorPosition) {
      isStopped = true;
    }

    previousSensorPosition = leaderSensorPosition;
    return isStopped;
  }

  public double position() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return 0;
    }

    return ticksToFeet(mEncoder.getPosition());
  }

  public double velocity() {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return 0;
    }

    return mEncoder.getVelocity();
  }

  public void setOpenLoopRamp(double ramp) {
    if (leader == null) {
      LOGGER.trace("No drive system");
      return;
    }

    leader.setOpenLoopRampRate(ramp);
  }

  public void movePosition(double targetDistance) {
    if (leader == null) {
      LOGGER.trace("No Drive System");
      return;
    }
    set(ControlType.kPosition, feetToTicks(targetDistance));
  }

  private double feetToTicks(double feet) {
    double ticks = (feet / (RobotMap.WHEEL_CIRCUMFERENCE / 12.0)) 
        * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    LOGGER.trace("Feet = {} ticks = {}", feet, ticks);
    return ticks;
  }

  private double ticksToFeet(double ticks) {
    double feet = (ticks / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) 
        * (RobotMap.WHEEL_CIRCUMFERENCE / 12);
    //LOGGER.trace("Ticks = {} feet = {}", box(ticks), box(feet));
    return feet;
  }
  
  public void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addDoubleMetric(name + "_Position", this::position);
    telemetry.addDoubleMetric(name + "_Velocity", this::velocity);
  }

}
