package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import frc.robot.RobotMap;
import frc.robot.drive.NullSpeedController;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.OpticalSensor;
import frc.robot.usercontrol.DriverStation467;
import org.apache.logging.log4j.Logger;

public class Grabber {

  public enum GrabberState {
    START_GRAB,
    GRAB,
    NEUTRAL,
    RELEASE
  }

  public static final int GRAB_TIME_MS = 1000;
  public static final int RELEASE_TIME_MS = 1000;
  private GrabberState state = GrabberState.NEUTRAL;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Grabber.class.getName());

  private static Grabber instance;
  private SpeedController left;
  private SpeedController right;
  private boolean hadCube = false;
  private boolean hasCube = false;
  private OpticalSensor os;
  private GrabberSolenoid rightGrab;
  private GrabberSolenoid leftGrab;

  //private boolean grabberButtonDown = false;

  private Grabber() {
    if (RobotMap.HAS_GRABBER && !RobotMap.useSimulator) {
      left = new Spark(RobotMap.GRABBER_L_CHANNEL);
      left.setInverted(RobotMap.GRABBER_INVERT);
      right = new Spark(RobotMap.GRABBER_R_CHANNEL);
      right.setInverted(RobotMap.GRABBER_INVERT);
      os = OpticalSensor.getInstance();
      leftGrab = GrabberSolenoid.getLeftInstance();
      rightGrab = GrabberSolenoid.getRightInstance();
    } else {
      left = new NullSpeedController();
      right = new NullSpeedController();
      os = OpticalSensor.getInstance();
    }

  }

  public static Grabber getInstance() {
    if (instance == null) {
      instance = new Grabber();
    }

    return instance;
  }

  public void periodic() {
    if (!RobotMap.HAS_GRABBER) {
      return;
    }

    double speed = 0.0;
    switch (state) {

      case START_GRAB:
        if (hasCube()) {
          state = GrabberState.NEUTRAL;
        } else {
          speed = RobotMap.MAX_GRAB_SPEED;
        }
        break;

      case GRAB:
        speed = RobotMap.MAX_GRAB_SPEED;
        break;

      case NEUTRAL:
        speed = 0.0;
        break; 

      case RELEASE:
        speed = -RobotMap.MAX_GRAB_SPEED;
        break;

      default:

    }

    if (!RobotMap.useSimulator) {
      left.set(speed);
      right.set(-speed);
    }

    // Save the previous state and check for current state.
    hadCube = hasCube;
    hasCube = hasCube();
  }

  public void startGrab() {
    state = GrabberState.START_GRAB;
  }

  public void grab() {
    //only used by auto
    state = GrabberState.GRAB;
  }
  
  public void grab(double throttle) {
    if (!RobotMap.HAS_GRABBER) {
      return;
    }

    if (Math.abs(throttle) < RobotMap.MIN_GRAB_SPEED) {
      throttle = 0.0;
    }

    if (!RobotMap.useSimulator) {
      if (Math.abs(DriverStation467.getInstance().getNavJoystick().getLeftStickY()) > 0.5) {
        DriverStation467.getInstance().getNavRumbler().rumble(25, 0.2);
        DriverStation467.getInstance().getDriverRumbler().rumble(25, 0.2);
        if (hasCube()) {
          DriverStation467.getInstance().getNavRumbler().rumble(150, 1.0);
          DriverStation467.getInstance().getDriverRumbler().rumble(50, 1.0);
        }
      }
    }

    LOGGER.debug("Grabber Throttle= {}", throttle);
    left.set(throttle * RobotMap.MAX_GRAB_SPEED);
    right.set(-throttle * RobotMap.MAX_GRAB_SPEED);

    // Save the previous state and check for current state.
    hadCube = hasCube;
    hasCube = hasCube();
  }

  public void grabAndOpen() {
    state = GrabberState.GRAB;
    open();
  }
  
  public void  grabAndClose() {
    state = GrabberState.GRAB;
    close();
  }

  public void release() {
    state = GrabberState.RELEASE;
  }

  public void pause() {
    state = GrabberState.NEUTRAL;
    close();
  }

  public void open() {
    leftOpen();
    rightOpen();
  }

  public void close() {
    leftClose();
    rightClose();
  }

  public void rightClose() {
    if (!RobotMap.useSimulator) {
      if (rightGrab.exists()) {
        rightGrab.close();
      } else {
        LOGGER.info("Right Solenoid does not exist");
      }
    }
  }

  public void rightOpen() {
    if (!RobotMap.useSimulator) {
      if (rightGrab.exists()) {
        rightGrab.open();
      } else {
        LOGGER.info("Right Solenoid does not exist");
      }
    }
  }

  public void leftClose() {
    if (!RobotMap.useSimulator) {
      if (leftGrab.exists()) {
        leftGrab.close();
      } else {
        LOGGER.info("Left solenoid does not exist");
      }
    }
  }

  public void leftOpen() {
    if (!RobotMap.useSimulator) {
      if (leftGrab.exists()) {
        leftGrab.open();
      } else {
        LOGGER.info("Left solenoid does not exist");
      }
    }
  }

  public boolean justGotCube() {
    if (!RobotMap.useSimulator) {
      return (!hadCube && hasCube());
    } else {
      return false;
    }
  }

  public boolean hasCube() {
    return (!RobotMap.useSimulator && RobotMap.HAS_GRABBER && os.detectedTarget());
  }

  public void reset() {
    rightGrab.reset();
  }
}
