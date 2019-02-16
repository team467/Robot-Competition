package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

public class Turret extends GamePieceBase implements GamePiece {

  private static Turret instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Turret.class.getName());

  // Physical components
  private Spark motor;
  private AnalogPotentiometer rotationSensor;
  private PIDController pidController;

  private double ticksPerDegree;
  private boolean onManualControl = true;

  private boolean targetLock = false;

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
    motor = new Spark(RobotMap.TURRET_MOTOR_CHANNEL);
    motor.setInverted(RobotMap.TURRET_SENSOR_INVERTED);
    motor.setName("Telemetry", "TurretMotor");

    rotationSensor = new AnalogPotentiometer(RobotMap.TURRET_SENSOR_CHANNEL);
    rotationSensor.setName("Telemetry", "TurretRotationSensor");

    ticksPerDegree = (RobotMap.TURRET_RIGHT_LIMIT_TICKS - RobotMap.TURRET_LEFT_LIMIT_TICKS)
        / (RobotMap.TURRET_RIGHT_LIMIT_DEGREES - RobotMap.TURRET_LEFT_LIMIT_DEGREES);

    pidController = new PIDController(RobotMap.TURRET_P, RobotMap.TURRET_I, RobotMap.TURRET_D, RobotMap.TURRET_F,
        rotationSensor, motor);
    pidController.setInputRange(RobotMap.TURRET_LEFT_LIMIT_DEGREES, RobotMap.TURRET_RIGHT_LIMIT_DEGREES);
    pidController.setOutputRange(-1.0, 1.0);
    pidController.setAbsoluteTolerance(RobotMap.TURRET_ALLOWABLE_ERROR_TICKS);
    pidController.setContinuous(false);

    initSendable(TelemetryBuilder.getInstance());
    LOGGER.trace("Created Turret game piece.");
  }

  /**
   * Manually overides the automated turret position. Used by navigator for fine
   * tuning.
   * 
   * @param speed the speed to move the turret.
   */
  public void manual(double speed) {
    LOGGER.debug("Manual override for turret position: {}", speed);
    onManualControl = true;
    targetLock = false;
    targetPosition = currentPosition;
    if (!RobotMap.useSimulator && RobotMap.HAS_TURRET) {
      if (enabled) {
        motor.set(speed);
      }
    }
  }

  private void followVision() {
    if (targetLock && !onManualControl) {
      //TODO get from other class. 
      double visionAngle = 0;
      double targetAngle = rotationSensor.get() + visionAngle;
      target(targetAngle);

    }
    
  }

  public void lockOnTarget() {
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
  }

  /**
   * Gets the current target position, not actual.
   * 
   * @return the target position
   */
  public double target() {
    LOGGER.debug("Current target position: {}", targetPosition);
    return targetPosition;
  }

  /**
   * Returns the position from the last sensor read.
   * 
   * @return the position in degrees
   */
  public double position() {
    LOGGER.debug("Current position: {}", currentPosition);
    return currentPosition;
  }

  @Override
  public void enabled(boolean enabled) {
    super.enabled(enabled);
    if (!RobotMap.useSimulator && RobotMap.HAS_TURRET) {
      pidController.setEnabled(enabled);
    }
  }

  /**
   * Handles commands and update state.
   */
  public void periodic() {
    if (!RobotMap.useSimulator && RobotMap.HAS_TURRET) {
      if (enabled && !onManualControl) {
        followVision();
        pidController.setSetpoint(targetPosition * ticksPerDegree);
      }
      // Update state
      currentPosition = (rotationSensor.get() / ticksPerDegree);
    } else {
      if (enabled) {
        currentPosition = simulatedPosition;
      }
    }
  }

  void simulatedSensorData(double simulatedPosition) {
    this.simulatedPosition = simulatedPosition;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("TurretTarget", this::target, (targetInDegrees) -> target(targetInDegrees));
    builder.addDoubleProperty("TurretPosition", this::position, null);
    motor.initSendable(builder);
    rotationSensor.initSendable(builder);
  }

  /**
   * Checks if turret is in the Home (default) position.
   */
  public boolean isHome() {
    // TODO: Find out the diff between position and target
    if (instance.position() == RobotMap.TURRET_HOME) {
      return true;
    }
    return false;
  }

  public void moveTurretToHome() {
    instance.target(RobotMap.TURRET_HOME);
  }

}