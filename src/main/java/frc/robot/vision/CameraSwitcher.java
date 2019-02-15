package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class CameraSwitcher {

  public static int FORWARD_CAMERA_INDEX = 0;
  public static int CARGO_CAMERA_INDEX = 1;
  public static int BACKWARD_CAMERA_INDEX = 2;
  public static int HATCH_CAMERA_INDEX = 3;

  private static CameraSwitcher instance = null;

  private double[] cameraOrder;
  private NetworkTableEntry cameraNetworkTableEntry;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(CameraSwitcher.class.getName());

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
    cameraOrder = table.getEntry("order").getDoubleArray(new double[] {0, 1, 2, 3});
    cameraNetworkTableEntry = table.getEntry("camera");
  }

  public void forward() {
    LOGGER.debug("Setting camera forward at index {}", FORWARD_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[FORWARD_CAMERA_INDEX]);
  }

  public void backward() {
    LOGGER.debug("Setting camera backward at index {}", BACKWARD_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[BACKWARD_CAMERA_INDEX]);
  }

  public void cargo() {
    LOGGER.debug("Setting to cargo camera at index {}", CARGO_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[CARGO_CAMERA_INDEX]);
  }

  public void hatch() {
    LOGGER.debug("Setting to hatch camera at index {}", HATCH_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[HATCH_CAMERA_INDEX]);
  }

}