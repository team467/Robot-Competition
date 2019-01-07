package frc.robot.utilities;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Utility class for checking current limits over a number of periodic cycles.
 * This is required to avoid stopping a motor based on the initial current spike at motor start.
 */
public class MovingAverage {
  private Queue<Double> readings;
  private int numberOfReadings;
  private double sum;

  /**
   * Sets up a running average for the specified number of readings.
   *
   * @param numberOfReadings  the number of readings for the running average
   */
  public MovingAverage(int numberOfReadings) {
    this.numberOfReadings = numberOfReadings;
    readings = new ConcurrentLinkedQueue<Double>();
    for (int i = 0; i < numberOfReadings; i++) {
      readings.add(0.0);
    }
    sum = 0.0;
  }
  
  /**
   * Averages the current readings over the specified period and checks to see if it 
   * is over the limit. This class should be called every periodic cycle where a 
   * sensor reading is averaged, such as when a motor is current limited.
   *
   * @param reading  the latest sensor reading
   * @return the current average across the window
   */
  public double average(double reading) {
    readings.add(reading);
    sum += reading - readings.remove();
    return average();
  }

  /**
   * Returns the average across a sliding window of reading values.
   * 
   * @return the avarage across the reading window
   */
  public double average() {
    return (sum / (double) numberOfReadings);
  }
}