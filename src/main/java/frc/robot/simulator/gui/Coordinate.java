package frc.robot.simulator.gui;

import java.text.DecimalFormat;

/**
 * Stores coordinats together.
 */
public class Coordinate {
  private DecimalFormat df = new DecimalFormat("####0.00");

  public double x = 0.0;
  public double y = 0.0;

  public Coordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "(" + df.format(x) + "," + df.format(y) + ")";
  }
}
