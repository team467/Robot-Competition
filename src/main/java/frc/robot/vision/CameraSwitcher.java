package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class CameraSwitcher {

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
    LOGGER.debug("Setting camera forward at index {}", RobotMap.FORWARD_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[RobotMap.FORWARD_CAMERA_INDEX]);
  }

  public void backward() {
    LOGGER.debug("Setting camera backward at index {}", RobotMap.BACKWARD_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[RobotMap.BACKWARD_CAMERA_INDEX]);
  }

  public void cargo() {
    LOGGER.debug("Setting to cargo camera at index {}", RobotMap.CARGO_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[RobotMap.CARGO_CAMERA_INDEX]);
  }

  public void hatch() {
    LOGGER.debug("Setting to hatch camera at index {}", RobotMap.HATCH_CAMERA_INDEX);
    cameraNetworkTableEntry.setDouble(cameraOrder[RobotMap.HATCH_CAMERA_INDEX]);
  }

}