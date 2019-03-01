package frc.robot.gamepieces;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;
import frc.robot.vision.VisionController;

import org.apache.logging.log4j.Logger;

public class Turret extends GamePieceBase implements GamePiece {

  private static Turret instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Turret.class.getName());

  // Physical components
  private final WpiTalonSrxInterface talon;
  private static final int TALON_SENSOR_ID = 0;
  private static final int TALON_PID_SLOT_ID = 0;
  private double ticksPerDegree;
  private boolean onManualControl = true;
  private boolean targetLock = false;

  private VisionController vision;

  // Position in degress
  private double targetPosition;
  private double currentPosition;
  private double simulatedPosition;

  public double targetPositon;

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */
  public static Turret getInstance() {
    if (instance == null) {
      instance = new Turret();
    }
    return instance;
  }

  private Turret() {
    super("Telemetry", "Turret");

    // Initialize the sensors and actuators
    if (RobotMap.HAS_TURRET) {
      talon = TalonProxy.create(RobotMap.TURRET_MOTOR_CHANNEL);
      talon.setName("Telemetry", "TurretMotor");
      talon.setInverted(RobotMap.TURRET_MOTOR_INVERTED);
      talon.setSensorPhase(RobotMap.TURRET_SENSOR_INVERTED);
      talon.selectProfileSlot(TALON_PID_SLOT_ID, TALON_SENSOR_ID);
      talon.config_kP(TALON_PID_SLOT_ID, RobotMap.TURRET_P, RobotMap.TALON_TIMEOUT);
      talon.config_kI(TALON_PID_SLOT_ID, RobotMap.TURRET_I, RobotMap.TALON_TIMEOUT);
      talon.config_kD(TALON_PID_SLOT_ID, RobotMap.TURRET_D, RobotMap.TALON_TIMEOUT);
      talon.config_kF(TALON_PID_SLOT_ID, RobotMap.TURRET_F, RobotMap.TALON_TIMEOUT);
      // talon.configForwardSoftLimitThreshold(
      //     RobotMap.TURRET_RIGHT_LIMIT_TICKS, RobotMap.TALON_TIMEOUT);
      
      
      talon.configForwardSoftLimitEnable(false, RobotMap.TALON_TIMEOUT);
      // talon.configReverseSoftLimitThreshold(
      //     RobotMap.TURRET_LEFT_LIMIT_TICKS, RobotMap.TALON_TIMEOUT);
       talon.configReverseSoftLimitEnable(false, RobotMap.TALON_TIMEOUT);
      talon.configAllowableClosedloopError(TALON_PID_SLOT_ID,
          RobotMap.TURRET_ALLOWABLE_ERROR_TICKS, RobotMap.TALON_TIMEOUT);
      LOGGER.debug("Created Turret game piece. Channel: {}", talon.getDeviceID());
    } else {
      talon = null;
    }
   
    ticksPerDegree = 
        ((double) (RobotMap.TURRET_RIGHT_LIMIT_TICKS - RobotMap.TURRET_LEFT_LIMIT_TICKS))
        / (RobotMap.TURRET_RIGHT_LIMIT_DEGREES - RobotMap.TURRET_LEFT_LIMIT_DEGREES);

    vision = VisionController.getInstance();
    
    initSendable(TelemetryBuilder.getInstance());
  }

  public void configPid(double kP, double kI, double kD) {
    if (talon != null) {
      talon.config_kP(TALON_PID_SLOT_ID, kP, RobotMap.TALON_TIMEOUT);
      talon.config_kI(TALON_PID_SLOT_ID, kI, RobotMap.TALON_TIMEOUT);
      talon.config_kD(TALON_PID_SLOT_ID, kD, RobotMap.TALON_TIMEOUT);
    }
  }

  /**
   * Manually overides the automated turret position. Used by navigator for fine
   * tuning.
   * 
   * @param speed the speed to move the turret.
   */
  public void manual(double speed) {
    onManualControl = true;
    targetLock = false;
    targetPosition = currentPosition;
    LOGGER.debug("talon mode: {}", talon.getControlMode());

    if (RobotMap.HAS_TURRET) { 
      if (true) { //enabled
        talon.set(ControlMode.PercentOutput, speed);
        LOGGER.debug("Control mode: {} Manual override for turret: {} Expected: {}", talon.getControlMode(), 
        talon.getMotorOutputPercent(), speed);
      }
    }
  }

  public boolean isOveride() {
    return onManualControl;
  }

  private void followVision() {
    LOGGER.debug("Following vision.");
    if (RobotMap.HAS_TURRET) {
      if (targetLock && !onManualControl) {
        double visionAngle = vision.angle();
        double targetAngle = talon.getSelectedSensorPosition(TALON_SENSOR_ID) + visionAngle;
        targetPosition = targetAngle;
        onManualControl = false;
      }
    }
  }

  /**
   * Sets the turret to follow directions from an angle offset.
   */
  public void lockOnTarget() {
    LOGGER.debug("Locking on target.");
    targetLock = true;
    onManualControl = false;
  }

  /**
   * Moves the turret to the target position
   * 
   * @param targetInDegrees the target angle.
   */
  public void target(double targetInDegrees) {
    LOGGER.debug("Setting target position: {}", targetInDegrees);
    targetPosition = targetInDegrees;
    onManualControl = false;
    targetLock = false;
  }

  /**
   * Gets the current target position, not actual.
   * 
   * @return the target position
   */
  public double target() {
    //LOGGER.debug("Current target position: {}", targetPosition);
    return targetPosition;
  }

  /**
   * Returns the position from the last sensor read.
   * 
   * @return the position in degrees
   */
  public double position() {
    //LOGGER.debug("Current position: {}", currentPosition);
    return currentPosition;
  }

  /**
   * Handles commands and update state.
   */
  public void periodic() {
    if (RobotMap.HAS_TURRET) {
      if (enabled) {
        if (!RobotMap.useSimulator) {
          if (!onManualControl) {
            // followVision();
            talon.set(ControlMode.Position, (targetPosition * ticksPerDegree));
            LOGGER.info("trying to move turret to: {}", targetPosition * ticksPerDegree);
          }
          // Update state
          currentPosition = (talon.getSelectedSensorPosition(TALON_SENSOR_ID) / ticksPerDegree);
        } else {
          LOGGER.debug("Using simulated position of {}", simulatedPosition);
          currentPosition = simulatedPosition;
        } 
      } else {
        LOGGER.debug("Turret is disabled.");
      }
      LOGGER.debug("Current turrent position is {}", currentPosition);          
    }
  }

  void simulatedSensorData(double simulatedPosition) {
    LOGGER.debug("Setting simulated turret data to {}", simulatedPosition);
    this.simulatedPosition = simulatedPosition;
  }

  /**
   * Checks if turret is in the Home (default) position.
   */
  public boolean isHome() {
    double distanceToHome = instance.position() - RobotMap.TURRET_HOME;
    if (Math.abs(distanceToHome) <= RobotMap.TURRET_ALLOWABLE_ERROR_TICKS) {
      LOGGER.debug("Turret is home at distance {}", distanceToHome);
      return true;
    }
    LOGGER.debug("Turret is NOT home at distance {}", distanceToHome);
    return true;
  }

  public void moveTurretToHome() {
    LOGGER.error("Moving turret to home.");
    //instance.target(RobotMap.TURRET_HOME);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Turret Target", this::target, 
        (targetInDegrees) -> target(targetInDegrees));
    builder.addDoubleProperty("Turret Position", this::position, null);
    if (RobotMap.HAS_TURRET) {
      builder.addDoubleProperty("Turret Motor Output", talon::get, null);
      builder.addDoubleProperty("Turret Sensor Position", this::turretSensorPosition, null);
    }
  }

  private double turretSensorPosition() {
    return talon.getSelectedSensorPosition(TALON_SENSOR_ID);
  }

}