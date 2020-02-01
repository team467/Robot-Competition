package frc.robot.gamepieces;

import java.util.Hashtable;
import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;
import frc.robot.gamepieces.GamePiece;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public class Shooter extends GamePieceBase implements GamePiece {

  private static Shooter instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Shooter.class.getName());

  private static final int TALON_PID_SLOT_ID = 0;

  private boolean shootState = false;
  private boolean triggerState = false;
  private double speed = 0;

  private static WPI_TalonSRX flywheelLeader;
  private static WPI_TalonSRX flywheelFollower;
  public static TalonSpeedControllerGroup flywheel;

  private static WPI_TalonSRX triggerMotor;
  public static TalonSpeedControllerGroup trigger;

  private static AddressableLED leds;
  private static AddressableLEDBuffer ledBuffer;

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

        if (RobotMap.SHOOTER_FOLLOWER) {
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

      if (RobotMap.HAS_TRIGGER) {
        triggerMotor = new WPI_TalonSRX(RobotMap.TRIGGER_MOTOR_CHANNEL);
        trigger = new TalonSpeedControllerGroup("Trigger", ControlMode.PercentOutput, false, RobotMap.TRIGGER_MOTOR_INVERTED, triggerMotor);
      } else {
        trigger = new TalonSpeedControllerGroup();
      }

      if (RobotMap.HAS_SHOOTERLEDS) {
        leds = new AddressableLED(RobotMap.SHOOTER_LED_CHANNEL);
        ledBuffer = new AddressableLEDBuffer(RobotMap.SHOOTER_LED_AMOUNT * (RobotMap.SHOOTER_DOUBLESIDE_LED? 2: 1));
        leds.setLength(ledBuffer.getLength());

        for (var i = 0; i < ledBuffer.getLength(); i++) {
          ledBuffer.setRGB(i, 0, 0, 0);
        }

        leds.setData(ledBuffer);
        leds.start();
      } else {
        leds = null;
        ledBuffer = null;
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
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      trigger.set(1.0);
    }
  }

  public void stopShooting() {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      trigger.set(0.0);
    }
  }

  public void setTriggerState(boolean state) {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      this.triggerState = state;
    }
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

  public void setLedSrip(int r, int g, int b, int startingLed, int endingLed) {
    for (var i = Math.max(0, startingLed); i <= Math.min(ledBuffer.getLength()-1, endingLed); i++) {
      ledBuffer.setRGB(i, r, g, b);
   }
   leds.setData(ledBuffer);
  }

  public void fillStrip(int r, int g, int b, int led) {
    int setLed = Math.min(RobotMap.SHOOTER_LED_AMOUNT-1, led);
    setLedSrip(r, g, b, 0, setLed);
    if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
      setLedSrip(r, g, b, RobotMap.SHOOTER_LED_AMOUNT, RobotMap.SHOOTER_LED_AMOUNT + setLed);
    }
    if (setLed < RobotMap.SHOOTER_LED_AMOUNT-1) {
      setLedSrip(0, 0, 0, setLed + 1, RobotMap.SHOOTER_LED_AMOUNT-1);
      if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
        setLedSrip(r, g, b, RobotMap.SHOOTER_LED_AMOUNT + setLed + 1, ledBuffer.getLength()-1 + setLed);
      }
    }
  }

  public void clearStrip() {
    setLedSrip(0, 0, 0, 0, ledBuffer.getLength()-1);
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
            setTriggerState(true);
          } else {
            rampToSpeed(speed);
          }
        } else {
          setTriggerState(false);
          rampToSpeed(0);
        }

        if (triggerState) {
          startShooting();
        } else {
          stopShooting();
        }
      } else {
        stop();
      }
    }
  }
}