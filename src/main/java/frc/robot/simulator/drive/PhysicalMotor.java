package frc.robot.simulator.drive;

import frc.robot.RobotMap;

class PhysicalMotor {

  // Simulation Information
  public static final double MAX_RPM = 821;

  // Iteration period is 20 ms
  public static final double TIME_SLICE_IN_MS = 20.0;

  private static final double MAX_SPEED_PER_PERIOD = MAX_RPM
  / (60.0 * 60.0 * 1000.0 / TIME_SLICE_IN_MS)
  * (RobotMap.WHEEL_CIRCUMFERENCE / 12);

  private static final double FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL = 2;

  private static double MAX_THEORETICAL_VOLTAGE = 12.0;

  private static double MAX_POWER_IN_WATTS = 480.0;

  void set(double input) {
    // TODO: Create physical simulator component
  }

}