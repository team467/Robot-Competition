package frc.robot.utilities;

/**
 * Utility class for checking current limits over a number of periodic cycles.
 * This is required to avoid stopping a motor based on the initial current spike at motor start.
 */
public class CheckCurrentLimit {

  // private Queue<Double> currentReadings;
  // TODO Add additional required variables

  public CheckCurrentLimit() {
    // TODO Initialize a queue and other variables
  }

  /**
   * Averages the current readings over the specified period and checks to see if it is over 
   * the limit. This class should be called every periodic cycle where a current limited motor 
   * is used.
   *
   * @param current  the latest current sensor reading
   * @return true if over the current limit
   */
  public boolean isOverLimit(double current) {
    // TODO Add the current to the reading list, replacing one if neccessary. 
    // Then check the average against the limit and return true if over 
    return false; // Replace with check.
  }

}
