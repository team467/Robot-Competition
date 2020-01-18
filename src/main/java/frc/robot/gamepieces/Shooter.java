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

  // private boolean onManualControl = true;
  private final TalonSpeedControllerGroup talon1;

  private ControlMode shooterControlMode;

  private static final int TALON_PID_SLOT_ID = 0;

  public static WPI_TalonSRX motorLeader;
  public static WPI_TalonSRX motorFollower;

  private Hashtable<Integer, Integer> distanceToPower = new Hashtable<Integer, Integer>();

  /**
   * Gets the single instance of this class.
   *
   * @return The single instance.
   */
  // WPI_TalonSRX
  public static Shooter getInstance() {
    if (instance == null) {
      TalonSpeedControllerGroup talon1;

      if (RobotMap.HAS_SHOOTER && RobotMap.SHOOTERMOTOR > 0) {
        LOGGER.info("Creating  Lead Motors");

        final WPI_TalonSRX motorLeader = new WPI_TalonSRX(RobotMap.LEFT_LEAD_CHANNEL);
        WPI_TalonSRX motorFollower = null;

        if (RobotMap.SHOOTERMOTOR == 2) {
          LOGGER.info("Creating first set of follower motors");
          motorFollower = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_1_CHANNEL);
        }

        ControlMode shooterControlMode = ControlMode.PercentOutput;
        if (RobotMap.USE_VELOCITY_SPEED_CONTROL_FOR_TELOP) {
          shooterControlMode = ControlMode.Velocity;
        }
        talon1 = new TalonSpeedControllerGroup("Shooter", shooterControlMode, RobotMap.SHOOTER_SENSOR_INVERTED,
            RobotMap.SHOOTER_MOTOR_INVERTED, motorLeader, motorFollower);
      } else {
        talon1 = new TalonSpeedControllerGroup();
      }
      instance = new Shooter(talon1);
      instance.stop();
    }
    return instance;
  }

  private Shooter(TalonSpeedControllerGroup talon1) {
    super("Telemetry", "Shooter");
    this.talon1 = talon1;

    // talon1.config_kP(TALON_PID_SLOT_ID, p, RobotMap.TALON_TIMEOUT);
    // talon1.config_kI(TALON_PID_SLOT_ID, i, RobotMap.TALON_TIMEOUT);
    // talon1.config_kD(TALON_PID_SLOT_ID, d, RobotMap.TALON_TIMEOUT);
    // talon1.config_kF(TALON_PID_SLOT_ID, f, RobotMap.TALON_TIMEOUT);

  }

  public void flyWheeelPower() {
    if (RobotMap.HAS_SHOOTER) {
      talon1.set(1.0);
    }
  }

  public void kick(int count, int limit) {
    if (RobotMap.HAS_SHOOTER) {
      talon1.set(1.0);
    }
    // count = new
  }

  public void flyWheelPIDF(double kP, double kI, double kD, double kF) {
    if (talon1 != null) {

      double coefficientP = RobotMap.SHOOTER_P;
      double coefficientI = RobotMap.SHOOTER_I;
      double coefficientD = RobotMap.SHOOTER_D;
      double coefficientF = RobotMap.SHOOTER_F;

      talon1.pidf(RobotMap.SHOOTER_PID_SLOT_DRIVE, coefficientP, coefficientI, coefficientD, coefficientF,
          RobotMap.VELOCITY_MULTIPLIER_SHOOTER);
    }
  }

  // public void tuneSpeed() {
 
  // }

  // public flyWheelSpeed(){

  // }

  // TODO: implement distance with shoot
  private void smartShoot() {
    if (RobotMap.HAS_SHOOTER) {
      if (!RobotMap.HAS_CAMERA) {
        // distance and calc to shoot
        // shoot

      } else {

      }
    }

  }
  // public void enabled(boolean enabled) {

  // }

  /**
   * Called once per robot iteration. This conducts any movement if enabled, and
   * sends telemetry and state information in all cases.
   */
  
   public void stop() {
    talon1.stopMotor();

  }
  public void periodic() {

    if (RobotMap.HAS_SHOOTER) {
      if (enabled) {
        // if (kick(count) > kick(limit)) {
        stop();
      }
    }
  }


 
}