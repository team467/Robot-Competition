package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class CameraSwitcher {

  private static CameraSwitcher instance = null;

  private NetworkTableEntry cameraNetworkTableEntry;
  private NetworkTableEntry resetNetworkTableEntry;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(CameraSwitcher.class.getName());

  enum cameraState {
    FORWARDS, BACKWARDS, CARGO, HATCH;
  }

  private cameraState currentState;
  private cameraState prevState;
  private boolean locked;

  /**
   * Returns a singleton instance of the game piece controller.
   * 
   * @return GamePieceController the singleton instance
   */

  public static CameraSwitcher getInstance()  {
    if (instance == null) {
      instance = new CameraSwitcher();
    }
    return instance;
  }

  private CameraSwitcher() {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("camera");
    cameraNetworkTableEntry = table.getEntry("camera");
    resetNetworkTableEntry = table.getEntry("reset");
    locked = false;
    currentState = cameraState.FORWARDS;
    forward();
  }

  public void forward() {
    LOGGER.debug("Setting camera forward at index {}", RobotMap.FORWARD_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(RobotMap.FORWARD_CAMERA_INDEX);
    prevState = currentState;
    currentState = cameraState.FORWARDS;
  }

  public void backward() {
    LOGGER.debug("Setting camera backward at index {}", RobotMap.BACKWARD_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(RobotMap.BACKWARD_CAMERA_INDEX);
    prevState = currentState;
    currentState = cameraState.BACKWARDS;
  }

  public void cargo() {
    LOGGER.debug("Setting to cargo camera at index {}", RobotMap.CARGO_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(RobotMap.CARGO_CAMERA_INDEX);
    prevState = currentState;
    currentState = cameraState.CARGO;
  }

  public void hatch() {
    LOGGER.debug("Setting to hatch camera at index {}", RobotMap.HATCH_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(RobotMap.HATCH_CAMERA_INDEX);
    prevState = currentState;
    currentState = cameraState.HATCH;
  }

  public void restart() {
    LOGGER.debug("Restarting Camera");
    resetNetworkTableEntry.setBoolean(true);
  }

  public void lock() {
    LOGGER.debug("Locking Camera");
    locked = true;
  }

  public void unlock() {
    LOGGER.debug("Unlocking Camera");
    locked = false;
  }

  public void autoSwitch(double speed) {
    if (!locked) {
      if (speed < 0) {
        forward();
      } else if (speed > 0) {
        backward();
      }
    }
  }
}