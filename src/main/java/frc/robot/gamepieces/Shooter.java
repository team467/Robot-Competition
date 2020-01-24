package frc.robot.gamepieces;

import java.util.Hashtable;
import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;
import frc.robot.gamepieces.GamePiece;

import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public class Shooter extends GamePieceBase implements GamePiece {

  private static Shooter instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Shooter.class.getName());

  private static final int TALON_PID_SLOT_ID = 0;

  private boolean shootState = false;
  private double speed = 0;

  private static WPI_TalonSRX flywheelLeader;
  private static WPI_TalonSRX flywheelFollower;
  public static TalonSpeedControllerGroup flywheel;

  private Hashtable<Integer, Integer> distanceToPower = new Hashtable<Integer, Integer>();

  /**
   * Gets the single instance of this class.
   *
   * @return The single instance.
   */
  // WPI_TalonSRX
  public static Shooter getInstance() {
    if (instance == null) {
      if (RobotMap.HAS_SHOOTER) {
        LOGGER.info("Creating Lead Motors");

        flywheelLeader = new WPI_TalonSRX(RobotMap.SHOOTER_MOTOR_CHANNEL);
        flywheelFollower = null;

        if (RobotMap.SOOTER_FOLLOWER) {
          LOGGER.info("Creating first set of follower motors");
          flywheelFollower = new WPI_TalonSRX(RobotMap.SHOOTER_MOTOR_FOLLOWER_CHANNEL);
        }

        flywheel = new TalonSpeedControllerGroup("Shooter", ControlMode.Velocity, RobotMap.SHOOTER_SENSOR_INVERTED,
            RobotMap.SHOOTER_MOTOR_INVERTED, flywheelLeader, flywheelFollower);

        flywheel.pidf(RobotMap.SHOOTER_PID_SLOT_DRIVE, RobotMap.SHOOTER_P, RobotMap.SHOOTER_I, RobotMap.SHOOTER_D,
            RobotMap.SHOOTER_F, RobotMap.VELOCITY_MULTIPLIER_SHOOTER);
      } else {
        flywheel = new TalonSpeedControllerGroup();
      }
      instance = new Shooter(flywheel);
      instance.stop();
    }
    return instance;
  }

  private Shooter(TalonSpeedControllerGroup flywheel) {
    super("Telemetry", "Shooter");
  }

  public void stop() {
    flywheel.stopMotor();
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public void rampToSpeed(double speed) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      double output = Math.max(-1.0, Math.min(1.0, speed));
      flywheel.set(ControlMode.Velocity, output);
    }
  }

  public TalonSpeedControllerGroup getMotor() {
    return flywheel;
  }

  public boolean atSpeed() {
    double current = flywheel.velocity();
    double target = flywheel.closedLoopTarget();
    double error = current / target;

    if (error <= 1 + RobotMap.SHOOTER_SPEED_TOLERANCE || error >= 1 - RobotMap.SHOOTER_SPEED_TOLERANCE) {
      return true;
    } else {
      return false;
    }
  }

  public void startShooting() {

  }

  public void stopShooting() {

  }

  public void setShootState(boolean state) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      this.shootState = state;
    }
  }

  public void flyWheelPIDF(double kP, double kI, double kD, double kF, double kMaxVelocity) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      flywheel.pidf(RobotMap.SHOOTER_PID_SLOT_DRIVE, kP, kI, kD, kF, kMaxVelocity);
    }
  }

  // TODO: implement distance with shoot; auto aim/auto shooting
  private void smartShoot() {
    if (RobotMap.HAS_SHOOTER) {
      if (RobotMap.SHOOTER_SMART_SHOT) {
        // distance and calc to shoot
        // shoot

      } else {

      }
    }

  }

  /**
   * Called once per robot iteration. This conducts any movement if enabled, and
   * sends telemetry and state information in all cases.
   */

  //TODO Impliment number of balls to shoot; Press once to fire one ball; Hold to fire all
  public void periodic() {
    if (RobotMap.HAS_SHOOTER) {
      if (enabled) {
        if (shootState) {
          if (atSpeed()) {
            startShooting();
          } else {
            rampToSpeed(speed);
          }
        } else {
          stopShooting();
          rampToSpeed(0);
        }
      } else {
        stop();
      }
    }
  }
}