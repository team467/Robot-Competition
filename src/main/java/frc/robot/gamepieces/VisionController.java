package frc.robot.gamepieces;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotMap;


public class VisionController {

    private NetworkTable vision;
    private double degreeValues;
    private NetworkTableEntry cameraNetworkTableEntry;

    private VisionController() {

    vision  = NetworkTableInstance.getDefault().getTable("vision");    
    }

    public double getAngle() {
      return vision.getEntry("angle").getDouble(-1000);
    }
    public void setLEDS() {
      double angle = getAngle();
      if (vision.getEntry("haveAngle").getBoolean(false) == true) {
      if (angle <= RobotMap.ON_TARGET && angle >= (-1 * RobotMap.ON_TARGET)) {
        //TODO: LED FUNCTION
      }
      else if (angle <= RobotMap.ANGLE_OFFSET_LEVEL_ONE && angle >= (-1 * RobotMap.ANGLE_OFFSET_LEVEL_ONE)) {
        //LED FUNCTION
      }

      else if (angle <= RobotMap.ANGLE_OFFSET_LEVEL_TWO && angle >= (-1 * RobotMap.ANGLE_OFFSET_LEVEL_TWO)) {
        //LED FUNCTION
      }

      else if (angle <= RobotMap.ANGLE_OFFSET_LEVEL_THREE && angle >= (-1 * RobotMap.ANGLE_OFFSET_LEVEL_THREE)) {
        //LED FUNCTION
      }

      else if (angle <= RobotMap.ANGLE_OFFSET_LEVEL_FOUR && angle >= (-1 * RobotMap.ANGLE_OFFSET_LEVEL_FOUR)) {
        //LED FUNCTION
      }

    }

  }

}