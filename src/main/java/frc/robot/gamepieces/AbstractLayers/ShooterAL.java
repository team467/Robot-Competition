package frc.robot.gamepieces.AbstractLayers;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.RobotMap;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

import java.io.IOException;
import java.util.Hashtable;
import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;
import frc.robot.gamepieces.GamePiece;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.gamepieces.GamePieceController;

import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public class ShooterAL extends GamePieceBase implements GamePiece {

  private static ShooterAL instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(ShooterAL.class.getName());

  private static final int TALON_PID_SLOT_ID = 0;

  private boolean triggerState = false;
  private double speed = 0.0; //TODO gamepeice controller helps to determine this

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
  public static ShooterAL getInstance() {
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

      instance = new ShooterAL(flywheel);
      instance.stop();
    }
    return instance;
  }


  private ShooterAL(TalonSpeedControllerGroup flywheel) {
    super("Telemetry", "Shooter");
  }

  public TalonSpeedControllerGroup getMotor() {
    return flywheel;
  }

  public void stop() {
    flywheel.stopMotor();
  }

  public void setSpeed(double speed) {
    this.speed = speed;
    
  }

  public void rampToSpeed(double speed) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      double output = speed; //Math.max(-1.0, Math.min(1.0, speed));
      flywheel.set(ControlMode.Velocity, output);
      LOGGER.error("the speed is {}", output);
      LOGGER.error("Talon: {}, id: {}", flywheel.get());
    }
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
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      trigger.set(1.0);
      LOGGER.error(trigger.get());
    }
  }

  public void stopShooting() {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      trigger.set(1.0);
    }
  }

  public void reverseShooter() {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      trigger.set(1.0);
    }
  }

  public void setTriggerState(boolean state) {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      this.triggerState = state;
    }
  }

  public void flyWheelPIDF(double kP, double kI, double kD, double kF, double kMaxVelocity) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      flywheel.pidf(RobotMap.SHOOTER_PID_SLOT_DRIVE, kP, kI, kD, kF, kMaxVelocity);
    }
  }

  public void setLedSrip(int r, int g, int b, int startingLed, int endingLed) {
    if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTERLEDS) {
      for (var i = Math.max(0, startingLed); i <= Math.min(ledBuffer.getLength()-1, endingLed); i++) {
        ledBuffer.setRGB(i, r, g, b);
     }
     leds.setData(ledBuffer);
    }
  }

  public void fillStrip(int r, int g, int b, int led) {
    if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTERLEDS) {
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
  }

  public void clearStrip() {
    setLedSrip(0, 0, 0, 0, ledBuffer.getLength()-1);
  }

  public void flyWheelPIDF(double kP, double kI, double kD, double kF) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      flywheel.pidf(RobotMap.SHOOTER_PID_SLOT_DRIVE, kP, kI, kD, kF, RobotMap.VELOCITY_MULTIPLIER_SHOOTER);
    }
  }

  public enum FlywheelSettings {
    FORWARD,
    BACKWARD,
    STOP,
    MANUAL_FORWARD
  }

  public enum TriggerSettings {
    SHOOTING,
    STOP,
    REVERSE
  }

  public void setFlywheel(FlywheelSettings setting) {
    speed = 0.4; //GamePieceController.getInstance().shooterSpeed;
    switch(setting) {
      case FORWARD:
        rampToSpeed(-Math.abs(speed));
        LOGGER.error("shooter speed = {}", speed);
        break;

      case BACKWARD:
        rampToSpeed(-Math.abs(speed));
        break;

      case MANUAL_FORWARD:
        rampToSpeed(RobotMap.MANUAL_MODE_SHOOTER_SPEED); //TODO tbd speed
        break;
      case STOP:
        stop();
        LOGGER.debug("shooter stopping");
        break;
      
      default:
        stop();

    }
  }

    public void setTrigger(TriggerSettings setting) {

      switch(setting) {
        case SHOOTING:
          startShooting();
          break;
        case STOP:
          stopShooting();
          break;
        case REVERSE:
          reverseShooter();
          break;
        default:
          stopShooting();
      }

    }



    @Override
    public void checkSystem() {

      try {

        setTrigger(TriggerSettings.SHOOTING);
        setFlywheel(FlywheelSettings.FORWARD);

      
        if(flywheelLeader.isAlive()) {
          LOGGER.info("flywheel is alive");
        } else {
          LOGGER.error("flywheel is not alive");
        }

      } catch (NullPointerException e) {
        LOGGER.error("something is wrong with the shooter");
         e.printStackTrace();
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


      
      //   if (shootState) {
      //     if (atSpeed()) {
      //       setTriggerState(true);
      //       startShooting();
      //     } else {
      //       rampToSpeed(speed);
      //     }
      //   } else {
      //     setTriggerState(false);
      //     rampToSpeed(0);
      //   }

      //   if (triggerState) {
      //     startShooting();
      //   } else {
      //     stopShooting();
      //     stopShooting();
      //     rampToSpeed(0);
      //   }
      // } else {
      //   stop();
      // }
      }
    }
  }
}
  
