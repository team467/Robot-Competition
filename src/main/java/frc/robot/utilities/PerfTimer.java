package frc.robot.utilities;

import java.util.ArrayList;

public class PerfTimer {

  private long startTime;
  private ArrayList<Long> times;

  private int count;
  private long sum;
  private double mean;
  private double standardDeviation;

  public PerfTimer() {
    count = 0;
    sum = 0;
    mean = 0.0;
    standardDeviation = 0.0;
    times = new ArrayList<Long>();
  }

  public void startIteration() {
    startTime = System.currentTimeMillis();
  }

  public void endIteration() {
    times.add(System.currentTimeMillis() - startTime);
  }

  private void process() {
    if (times.size() > count) {
      count = times.size();
      sum = 0;
      for(long time : times) {
          sum += time;
      }
      mean = (double) sum / (double) count;
      standardDeviation = 0.0;
      for(long time : times) {
          standardDeviation += Math.pow((double) time - mean, 2);
      }
      standardDeviation = Math.sqrt(standardDeviation / (double) count) / 1;
      mean /= 1;
      sum /= 1;
    }
  }

  public int count() {
    process();
    return count;
  }

  public long sum() {
    process();
    return sum;
  }

  public double mean() {
    process();
    return mean;
  }

  public double standardDeviation() {
    process();
    return standardDeviation;
  }


}