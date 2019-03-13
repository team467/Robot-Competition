package frc.robot.gamepieces;

import static org.apache.logging.log4j.util.Unbox.box;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class Turret extends GamePieceBase implements GamePiece {

  private static Turret instance = null;

  private static final Logger LOGGER = RobotLogManager.getLogger(Turret.class.getName());

  // Physical components
  private final WpiTalonSrxInterface talon;
  private static final int TALON_SENSOR_ID = 0;
  private static final int TALON_PID_SLOT_ID = 0;
  private double ticksPerDegree;
  private boolean onManualControl = true;
  private boolean targetLock = false;

  // Position in degress
  private double targetAngle;
  private double currentAngle;

  // Ticks for tracking
  private int targetTicks;
  private int currentTicks;
  private int simulatedTicks;

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
      talon.configSelectedFeedbackSensor(FeedbackDevice.Analog, TALON_PID_SLOT_ID, 
      RobotMap.TALON_TIMEOUT);
      talon.selectProfileSlot(TALON_PID_SLOT_ID, TALON_SENSOR_ID);
      talon.configSelectedFeedbackSensor(FeedbackDevice.Analog, 
          TALON_PID_SLOT_ID, RobotMap.TALON_TIMEOUT);
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
      LOGGER.debug("Created Turret game piece. Channel: {}", box(talon.getDeviceID()));
    } else {
      talon = null;
    }
   
    ticksPerDegree = 
        ((double) (RobotMap.TURRET_RIGHT_LIMIT_TICKS - RobotMap.TURRET_LEFT_LIMIT_TICKS))
        / (RobotMap.TURRET_RIGHT_LIMIT_DEGREES - RobotMap.TURRET_LEFT_LIMIT_DEGREES);

    registerMetrics();
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
    targetAngle = currentAngle;
    targetTicks = currentTicks;
    LOGGER.debug("Talon mode: {}", talon.getControlMode());

    if (RobotMap.HAS_TURRET) { 
      if (true) { //enabled
        talon.set(ControlMode.PercentOutput, speed);
        LOGGER.debug("Control mode: {} Manual override for turret: {} Expected: {}", 
            talon.getControlMode(), talon.getMotorOutputPercent(), box(speed));
      }
    }
  }

  public boolean isOveride() {
    return onManualControl;
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
    LOGGER.debug("Setting target position: {}", box(targetInDegrees));
    targetAngle = targetInDegrees;
    onManualControl = false;
    targetLock = false;
  }

  /**
   * Gets the current target position, not actual.
   * 
   * @return the target position
   */
  public double target() {
    LOGGER.debug("Current target position: {}", box(targetAngle));
    return targetAngle;
  }

  /**
   * Returns the position from the last sensor read.
   * 
   * @return the position in degrees
   */
  public double position() {
    LOGGER.debug("Current position: {}", box(currentAngle));
    return currentAngle;
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
            targetTicks = (int) Math.round(RobotMap.TURRET_HOME_TICKS + targetAngle * ticksPerDegree);
            talon.set(ControlMode.Position, targetTicks);
            LOGGER.info("trying to move turret to: {}", targetTicks);
          }
          // Update state
          currentTicks = talon.getSelectedSensorPosition(TALON_SENSOR_ID);
        } else {
          LOGGER.debug("Using simulated position of {}", box(simulatedTicks));
          currentTicks = simulatedTicks;
        } 
        currentAngle = ((double) (currentTicks - RobotMap.TURRET_HOME_TICKS)) / ticksPerDegree;
      } else {
        LOGGER.debug("Turret is disabled.");
      }
      LOGGER.debug("Current turrent position is {}", box(currentAngle));          
    }
  }

  void simulatedSensorData(int ticks) {
    LOGGER.debug("Setting simulated turret data to {}", box(ticks));
    this.simulatedTicks = ticks;
  }

  /**
   * Checks if turret is in the Home (default) position.
   */
  public boolean isHome() {
    double distanceToHome = currentTicks - RobotMap.TURRET_HOME_TICKS;
    if (Math.abs(distanceToHome) <= RobotMap.TURRET_ALLOWABLE_ERROR_TICKS) {
      LOGGER.debug("Turret is home at distance: {}, ticks {}, angle: {}", 
          currentTicks, distanceToHome, currentAngle);
      return true;
    }
    LOGGER.debug("Turret is NOT home at distance: {}, ticks {}, angle: {}", 
        box(currentTicks), box(distanceToHome), box(currentAngle));
    return false;
  }

  public void moveTurretToHome() {
    LOGGER.error("Moving turret to home.");
    instance.target(RobotMap.TURRET_HOME_TICKS);
  }

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addDoubleMetric("Turret Target", this::target);
    telemetry.addDoubleMetric("Turret Position", this::position);
    if (RobotMap.HAS_TURRET) {
      telemetry.addDoubleMetric("Turret Motor Output", talon::get);
      telemetry.addDoubleMetric("Turret Sensor Position", this::turretSensorPosition);
    }
  }

  private double turretSensorPosition() {
    return talon.getSelectedSensorPosition(TALON_SENSOR_ID);
  }

}