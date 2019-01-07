package frc.robot.utilities;

/**
 * Collection of useful math functions.
 */
public class MathUtils {

  /**
   * Returns the weighted average of a and b.
   * a when factor = 0;
   * b when factor = 1
   */
  public static double weightedAverage(double a, double b, double weight) {
    return a * (1 - weight) + b * weight;
  }
}
