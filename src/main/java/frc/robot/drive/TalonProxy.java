package frc.robot.drive;

import frc.robot.RobotMap;

public class TalonProxy {

  /**
   * Creates the proxy backed either by a physical or simulated Talon based on the 
   * useSimulator setting in the RobotMap.
   * 
   * @param deviceNumber the CAN channel ID
   */
  public static WpiTalonSrxInterface create(int deviceNumber) {
    if (RobotMap.useSimulator) {
      return new frc.robot.simulator.drive.WpiTalonSrx(deviceNumber);
    } else {
      return new frc.robot.drive.WpiTalonSrx(deviceNumber);
    }
  }

}