package frc.robot.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.logging.RobotLogManager;

public class PerfTimer {

  private static final TreeMap<String, PerfTimer> timers = new TreeMap<String, PerfTimer>();
  private static final Logger PERF_CSV = RobotLogManager.getPerfLogger();
  private static final double ROBOT_START_TIME = Timer.getFPGATimestamp(); 
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  private static final Date date = new Date();

  private long startTime;
  private ArrayList<Long> times;

  private int count;
  private long sum;
  private double mean;
  private double standardDeviation;
  private double median;
  private double percentile75;
  private double percentile99;
  private double percentile95;

  public static PerfTimer timer(String name) {
    if (!timers.containsKey(name)) {
      timers.put(name, new PerfTimer());
    } 
    return timers.get(name);
  }

  private PerfTimer() {
    count = 0;
    sum = 0;
    mean = 0.0;
    standardDeviation = 0.0;
    times = new ArrayList<Long>();
  }

  public void start() {
    if (Level.TRACE.isMoreSpecificThan(PERF_CSV.getLevel())) {
      startTime = RobotController.getFPGATime();
      // startTime = System.currentTimeMillis();
    }
  }

  public void end() {
    if (Level.TRACE.isMoreSpecificThan(PERF_CSV.getLevel())) {
      times.add(RobotController.getFPGATime() - startTime);
      // times.add(System.currentTimeMillis() - startTime);
    }
  }

  private void process() {
    if (times.size() > count) {
      count = times.size();
      Collections.sort(times);
      sum = 0;
      for(long time : times) {
          sum += time;
      }
      mean = (double) sum / (double) count;
      standardDeviation = 0.0;
      for(long time : times) {
          standardDeviation += Math.pow((double) time - mean, 2);
      }
      standardDeviation = Math.sqrt(standardDeviation / (double) count) / 1000.0;
      mean /= 1000.0;
      sum /= 1000.0;
      median = times.get((int) Math.round(((double) count) * 0.5)) / 1000.0;
      percentile75 = times.get((int) Math.round(((double) count) * 0.75)) / 1000.0;
      percentile95 = times.get((int) Math.round(((double) count) * 0.95)) / 1000.0;
      percentile99 = times.get((int) Math.round(((double) count) * 0.99)) / 1000.0;
    }
  }

  public static void print() {
    if (Level.TRACE.isMoreSpecificThan(PERF_CSV.getLevel())) {
      for (String name : timers.keySet()) {
        PerfTimer timer = timers.get(name);
        timer.process();
        PERF_CSV.trace("Ignored",
            dateFormat.format(date), 
            Math.round(Timer.getFPGATimestamp() - ROBOT_START_TIME),
            name, timer.count, timer.mean, 
            timer.standardDeviation, timer.sum, timer.median, 
            timer.percentile75, timer.percentile95, timer.percentile99);
      }
    }
  }

}