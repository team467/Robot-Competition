package frc.robot.vision;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Utility class for checking current limits over a number of periodic cycles.
 * This is required to avoid stopping a motor based on the initial current spike at motor start.
 */
public class MovingAverage {

  private Queue<Double> currentReadings;

  private int numberOfReadings;

  private double sum;

  /**
   * Sets up a running average for the specified number of readings.
   *
   * @param numberOfReadings  the number of readings for the running average
   */
  public MovingAverage(int numberOfReadings) {
    this.numberOfReadings = numberOfReadings;
    currentReadings = new ConcurrentLinkedQueue<Double>();
    for (int i = 0; i < numberOfReadings; i++) {
      currentReadings.add(0.0);
    }
    sum = 0.0;
  }
  
  /**
   * Averages the current readings over the specified period and checks to see if 
   * it is over the limit. This class should be called every periodic cycle where 
   * a current limited motor is used.
   *
   * @param current  the latest current sensor reading
   * @return true if over the current limit
   */
  public double average(double current) {
    currentReadings.add(current);
    sum += current - currentReadings.remove();
    return (sum / (double) numberOfReadings);
  }
  
}