package frc.robot.gamepieces.AbstractLayers;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.RobotMap;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
import com.ctre.phoenix.motorcontrol.NeutralMode;

import java.util.Random;

public class ShooterAL extends GamePieceBase implements GamePiece {

  private static ShooterAL instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(ShooterAL.class.getName());

  private static final int TALON_PID_SLOT_ID = 0;

  private boolean triggerState = false;
  private double speed = 0.1; //TODO gamepeice controller helps to determine this

  private static WPI_TalonSRX flywheelLeader;
  private static WPI_TalonSRX flywheelFollower;
  public static TalonSpeedControllerGroup flywheel;

  private static WPI_TalonSRX triggerMotor;
  public static TalonSpeedControllerGroup trigger;
  

  public static Servo hoodLeft;
  public static Servo hoodRight;

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
        LOGGER.debug("Creating Lead Motors");

        flywheelLeader = new WPI_TalonSRX(RobotMap.SHOOTER_MOTOR_CHANNEL);
        flywheelLeader.setNeutralMode(NeutralMode.Coast);
        flywheelFollower = null;

        if (RobotMap.SHOOTER_FOLLOWER) {
          LOGGER.debug("Creating follow motors");
          flywheelFollower = new WPI_TalonSRX(RobotMap.SHOOTER_MOTOR_FOLLOWER_CHANNEL);
          flywheelFollower.setNeutralMode(NeutralMode.Coast);
        }

        flywheel = new TalonSpeedControllerGroup("Shooter", ControlMode.Velocity, RobotMap.SHOOTER_SENSOR_INVERTED,
            RobotMap.SHOOTER_MOTOR_INVERTED, flywheelLeader, flywheelFollower);

        flywheel.pidf(RobotMap.SHOOTER_PID_SLOT_DRIVE, RobotMap.SHOOTER_P, RobotMap.SHOOTER_I, RobotMap.SHOOTER_D,
            RobotMap.SHOOTER_F, RobotMap.VELOCITY_MULTIPLIER_SHOOTER);
      } else {
        flywheel = new TalonSpeedControllerGroup();
      }

      if (RobotMap.HAS_SHOOTER_TRIGGER) {
        triggerMotor = new WPI_TalonSRX(RobotMap.TRIGGER_MOTOR_CHANNEL);
        trigger = new TalonSpeedControllerGroup("Trigger", ControlMode.PercentOutput, false, RobotMap.TRIGGER_MOTOR_INVERTED, triggerMotor);
      } else {
        trigger = new TalonSpeedControllerGroup();
      }

      if (RobotMap.HAS_SHOOTER_HOOD) {
        hoodLeft = new Servo(RobotMap.HOOD_LEFT_PWM_PORT);
        hoodRight = new Servo(RobotMap.HOOD_RIGHT_PWM_PORT);

        hoodLeft.set(RobotMap.HOOD_LEFT_STARTING_POSITION);
        hoodRight.set(RobotMap.HOOD_RIGHT_STARTING_POSITION);

      } else {
        hoodLeft = null;
        hoodRight = null;
      }

      if (RobotMap.HAS_SHOOTER_LEDS) {
        leds = new AddressableLED(RobotMap.SHOOTER_LED_CHANNEL);
        ledBuffer = new AddressableLEDBuffer(RobotMap.SHOOTER_LED_AMOUNT * (RobotMap.SHOOTER_DOUBLESIDE_LED ? 2: 1));
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
    //LOGGER.error("Stop called");
    flywheel.stopMotor();
  }

  public void setSpeed(double speed) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      double output = Math.max(-1.0, Math.min(1.0, speed));
      flywheel.set(ControlMode.PercentOutput, output);
      LOGGER.debug("the speed is {}", output);
    }
    
  }

  public void rampToSpeed(double speed) {
    if (flywheel != null && RobotMap.HAS_SHOOTER) {
      double output = Math.max(-1.0, Math.min(1.0, speed));
      flywheel.set(ControlMode.Velocity, output);
      LOGGER.debug("the speed is {}", output);
    }
  }

  public boolean atSpeed() {
    double current = flywheel.velocity();
    double target = flywheel.closedLoopTarget();
    double error = current / target;

    if (Math.abs(error - 1) <= RobotMap.SHOOTER_SPEED_TOLERANCE) {
      return true;
    } else {
      return false;
    }
  }


  public void startShooting() {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      trigger.set(1.0);
      LOGGER.debug(trigger.get());
    }
  }

  public void stopShooting() {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      trigger.set(0.0);
    }
  }

  public void reverseShooter() {
    if (trigger != null && RobotMap.HAS_TRIGGER) {
      trigger.set(-1.0);
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

  public void setLedStrip(int r, int g, int b, int startingLed, int endingLed) {
    if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTER_LEDS) {
      for (var i = Math.max(0, startingLed); i <= Math.min(ledBuffer.getLength()-1, endingLed); i++) {
        LOGGER.warn("Setting led {} to color R{} G{} B{}", i, r, g, b);
        ledBuffer.setRGB(i, r, g, b);
     }
     leds.setData(ledBuffer);
    }
  }

  public void fillStrip(int r, int g, int b, int led) {
    if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTER_LEDS) {
      int setLed = Math.min(RobotMap.SHOOTER_LED_AMOUNT-1, led);
      setLedStrip(r, g, b, 0, setLed);
      if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
        setLedStrip(r, g, b, RobotMap.SHOOTER_LED_AMOUNT, RobotMap.SHOOTER_LED_AMOUNT + setLed);
      }
      if (setLed < RobotMap.SHOOTER_LED_AMOUNT-1) {
        setLedStrip(0, 0, 0, setLed + 1, RobotMap.SHOOTER_LED_AMOUNT-1);
        if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
          setLedStrip(r, g, b, RobotMap.SHOOTER_LED_AMOUNT + setLed + 1, ledBuffer.getLength()-1 + setLed);
        }
      }
    }
  }

  public void fillStrip(Color color, int led) {
    int r = (int) color.red * 255; 
    int g = (int) color.green * 255; 
    int b = (int) color.blue * 255; 

    fillStrip(r, g, b, led);
  }

  public void clearStrip() {
    setLedStrip(0, 0, 0, 0, ledBuffer.getLength()-1);
  }

  public void setHoodAngle(double leftAngle, double rightAngle) {
    if (hoodLeft != null && hoodRight != null && RobotMap.HAS_SHOOTER_HOOD) {
      setLeftHoodAngleRaw(leftAngle);
      setRightHoodAngleRaw(rightAngle);
    }
  }

  public void setLeftHoodAngle(double angle) {
    if (hoodLeft != null && RobotMap.HAS_SHOOTER_HOOD) {
      angle = Math.max(0, Math.min(1, angle));

      if (RobotMap.HOOD_LEFT_INVERTED) {
        angle = Math.abs(angle-1);
      }

      if (RobotMap.HOOD_ADD_NOISE) {
        angle = angle + ((new Random().nextInt(101)-50)/10000);
      }

      angle = (angle * (RobotMap.HOOD_LEFT_MAX - RobotMap.HOOD_LEFT_MIN)) - RobotMap.HOOD_LEFT_MIN;

      hoodLeft.set(angle);
    }
  }

  public void setRightHoodAngle(double angle) {
    if (hoodRight != null && RobotMap.HAS_SHOOTER_HOOD) {
      angle = Math.max(0, Math.min(1, angle));

      if (RobotMap.HOOD_RIGHT_INVERTED) {
        angle = Math.abs(angle-1);
      }

      if (RobotMap.HOOD_ADD_NOISE) {
        angle = angle + ((new Random().nextInt(101)-50)/10000);
      }

      angle = (angle * (RobotMap.HOOD_RIGHT_MAX - RobotMap.HOOD_RIGHT_MIN)) - RobotMap.HOOD_RIGHT_MIN;

      hoodRight.set(angle);
    }
  }

  public void setLeftHoodAngleRaw(double angle) {
    if (hoodLeft != null && RobotMap.HAS_SHOOTER_HOOD) {
      angle = Math.max(RobotMap.HOOD_LEFT_MIN, Math.min(RobotMap.HOOD_LEFT_MAX, angle));

      if (RobotMap.HOOD_LEFT_INVERTED) {
        angle = Math.abs(angle-1);
      }

      if (RobotMap.HOOD_ADD_NOISE) {
        angle = angle + ((new Random().nextInt(101)-50)/10000);
      }

      hoodLeft.set(angle);
    }
  }

  public void setRightHoodAngleRaw(double angle) {
    if (hoodRight != null && RobotMap.HAS_SHOOTER_HOOD) {
      angle = Math.max(RobotMap.HOOD_RIGHT_MIN, Math.min(RobotMap.HOOD_RIGHT_MAX, angle));

      if (RobotMap.HOOD_RIGHT_INVERTED) {
        angle = Math.abs(angle-1);
      }

      if (RobotMap.HOOD_ADD_NOISE) {
        angle = angle + ((new Random().nextInt(101)-50)/10000);
      }

      hoodRight.set(angle);
    }
  }

  public double getLeftHoodAngle() {
    double angle = 0;
    if (hoodLeft != null && RobotMap.HAS_SHOOTER_HOOD) {
      angle = hoodLeft.getAngle();
    }
    return angle;
  }

  public double getRightHoodAngle() {
    double angle = 0;
    if (hoodRight != null && RobotMap.HAS_SHOOTER_HOOD) {
      angle = hoodRight.getAngle();
    }
    return angle;
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
   speed = GamePieceController.getInstance().shooterSpeed;
    switch(setting) {
      case FORWARD:
        rampToSpeed(-speed);
        break;

      case BACKWARD:
        rampToSpeed(speed);
        break;

      case MANUAL_FORWARD:
        rampToSpeed(RobotMap.MANUAL_MODE_SHOOTER_SPEED); //TODO tbd speed
        break;
      case STOP:
      stop();
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
          LOGGER.debug("flywheel is not alive");
        }

      } catch (NullPointerException e) {
        LOGGER.debug("something is wrong with the shooter");
         e.printStackTrace();
      }

    }
}