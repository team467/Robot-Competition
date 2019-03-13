package frc.robot.logging;

import static org.apache.logging.log4j.util.Unbox.box;
import frc.robot.RobotMap;
import frc.robot.Robot.RobotMode;
import frc.robot.utilities.PerfTimer;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectArrayMessage;

public class Telemetry {

  private static final Logger LOGGER 
      = RobotLogManager.getLogger(Telemetry.class.getName());

  private PerfTimer perfTimer;

  private static final int BUFFER_SIZE = 60;
  private final ArrayList<Object> buffer;
  private static final Logger CSV 
      = RobotLogManager.getLogger("TELEMETRY");

  private static Telemetry instance = null;

  private final SortedMap<String, Supplier<String>> stringMetrics;
  private final SortedMap<String, BooleanSupplier> booleanMetrics;
  private final SortedMap<String, DoubleSupplier> doubleMetrics;

  private boolean printedHeaders = false;

  private long startTime = -1;
  private long lastIterationTime = -1;

  private RobotMode robotMode;

  private Timer timer;

  /**
  * Returns a singleton instance of the telemery handler.
  * 
  * @return TelemetryBuilder the telemetry instance
  */
  public static Telemetry getInstance() {
    if (instance == null) {
      instance = new Telemetry();
    }
    return instance;
  }

  /**
  * Creates the telemtry builder instance with the correct location for the CSV
  * output files.
  */
  private Telemetry() {
    robotMode = RobotMode.STARTED;
    stringMetrics = new TreeMap<String, Supplier<String>>();
    booleanMetrics = new TreeMap<String, BooleanSupplier>();
    doubleMetrics = new TreeMap<String, DoubleSupplier>();
    buffer = new ArrayList<Object>(BUFFER_SIZE);
    perfTimer = PerfTimer.timer("Telemetry");

    // Metrics to figure out
    // addMetric("/startingLocation/x"); 
    // addMetric("/startingLocation/y"); 
    // addMetric("/rightDistance"); 
    // addMetric("/leftDistance"); 
    // addMetric("/isZeroed");
    // addMetric("/headingAngle");
  }

  private class PrintTimer extends TimerTask {
    private Telemetry telemetry;

    private PrintTimer() {
      telemetry = Telemetry.getInstance();
    }

    @Override
    public void run() {
      perfTimer.start();
      telemetry.printCsvLine();
      perfTimer.end();
    }
  }

  /**
   * Add a boolean metric.
   *
   * @param key     metric name
   * @param getter  getter function (returns current value)
   */
  public void addBooleanMetric(String key, BooleanSupplier getter) {
    booleanMetrics.put(key, getter);
  }

  /**
   * Add a double property.
   *
   * @param key     property name
   * @param getter  getter function (returns current value)
   */
  public void addDoubleMetric(String key, DoubleSupplier getter) {
    doubleMetrics.put(key, getter);
  }

  /**
   * Add a string property.
   *
   * @param key     property name
   * @param getter  getter function (returns current value)
   */
  public void addStringMetric(String key, Supplier<String> getter) {
    stringMetrics.put(key, getter);
  }

  public void start() {
    if (!printedHeaders && RobotMap.ENABLE_TELEMETRY 
        && Level.DEBUG.isMoreSpecificThan(CSV.getLevel())) {
      printHeaders();
      timer = new Timer("Telemetry Timer", true);
      timer.scheduleAtFixedRate(new PrintTimer(), 0, RobotMap.TELEMETRY_TIMER_MS);
    }
  }

  private void printHeaders() {
    buffer.add("Time (ms)");
    buffer.add("Iteration Time (ms)");
    buffer.add("Robot Mode");
    for (String metricKey :  stringMetrics.keySet()) {
      buffer.add(metricKey);
    }
    for (String metricKey :  booleanMetrics.keySet()) {
      buffer.add(metricKey);
    }
    for (String metricKey :  doubleMetrics.keySet()) {
      buffer.add(metricKey);
    }
    CSV.debug("Ignored", new ObjectArrayMessage(buffer.toArray()).getParameters());
    startTime = System.currentTimeMillis();
    printedHeaders = true;
  }

  private void printCsvLine() {
    if (Level.DEBUG.isMoreSpecificThan(CSV.getLevel())) {
      long currentTime = System.currentTimeMillis() - startTime;
      buffer.clear();
      buffer.add(currentTime);
      buffer.add(currentTime - lastIterationTime);
      buffer.add(robotMode);
      for (Supplier<String> metricSupplier :  stringMetrics.values()) {
        buffer.add(metricSupplier.get());
      }
      for (BooleanSupplier metricSupplier :  booleanMetrics.values()) {
        buffer.add(metricSupplier.getAsBoolean());
      }
      for (DoubleSupplier metricSupplier :  doubleMetrics.values()) {
        buffer.add(metricSupplier.getAsDouble());
      }
      CSV.debug("Ignored", new ObjectArrayMessage(buffer.toArray()).getParameters());
      lastIterationTime = currentTime;
    }
  }

  public void robotMode(RobotMode robotMode) {
    this.robotMode = robotMode;
  }

}