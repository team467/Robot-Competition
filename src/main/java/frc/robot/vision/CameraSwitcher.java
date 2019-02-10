package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class CameraSwitcher {
  public static void update(int camera) {
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("camera");
    NetworkTableEntry cameraNumber = table.getEntry("camera");
    NetworkTableEntry cameraOrder = table.getEntry("order");
        
    cameraNumber.setDouble(cameraOrder.getDoubleArray(new double[] {0, 1, 2, 3})[camera]);
  }
}