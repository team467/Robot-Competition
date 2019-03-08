package frc.robot.usercontrol;

import static org.apache.logging.log4j.util.Unbox.box;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

public class Rumbler {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Rumbler.class.getName());
  
  static final int ITERATION_TIME_MS = 20;
  
  private final XBoxJoystick467 controller;
  
  int durationMs;

  double intensity;

  Rumbler(XBoxJoystick467 controller) {
    this.controller = controller;
  }

  /**
   * @param durationMs Rumble duration in milliseconds
   * @param intensity Squared input
   */
  public void rumble(int durationMs, double intensity) {
    this.durationMs = durationMs;
    this.intensity = intensity * intensity;
    LOGGER.debug("rumble duration= {} rumble intensity= {}", box(durationMs), box(intensity));
  }

  public void periodic() {
    if (durationMs > 0) {
      controller.setRumble(intensity);
      durationMs -= ITERATION_TIME_MS;
      LOGGER.debug("periodic duration= {} intensity= {}", box(durationMs), box(intensity));
    } else { 
      controller.setRumble(0);
      intensity = 0;
    }
  }
}