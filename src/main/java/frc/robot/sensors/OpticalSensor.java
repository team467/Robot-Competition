package frc.robot.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.RobotMap;

public class OpticalSensor {
  private static OpticalSensor instance;
  private DigitalInput di;

  private OpticalSensor() {
    if (!RobotMap.useSimulator) {
      di = new DigitalInput(RobotMap.OPTICAL_CHANNEL);
    }
  }

  public static OpticalSensor getInstance() {
    if (instance == null) {
      instance = new OpticalSensor();
    } 

    return instance;
  }

  public boolean detectedTarget() {
    if (!RobotMap.useSimulator) {
      return !di.get();
    } else {
      return false;
    }
  }
}
