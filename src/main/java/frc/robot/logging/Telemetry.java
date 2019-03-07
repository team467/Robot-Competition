package frc.robot.logging;

import frc.robot.RobotMap;
import frc.robot.Robot.RobotMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.Logger;

public class Telemetry {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(Telemetry.class.getName());

  private static final Logger CSV 
      = RobotLogManager.getMainLogger("TELEMETRY");

  private static Telemetry instance = null;

  private SortedMap<String, Supplier<String>> stringMetrics;
  private SortedMap<String, BooleanSupplier> booleanMetrics;
  private SortedMap<String, DoubleSupplier> doubleMetrics;

  private boolean printedHeaders = false;

  private CSVPrinter csvPrinter = null;

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
  public Telemetry() {
    robotMode = RobotMode.STARTED;
    stringMetrics = new TreeMap<String, Supplier<String>>();
    booleanMetrics = new TreeMap<String, BooleanSupplier>();
    doubleMetrics = new TreeMap<String, DoubleSupplier>();

    // Metrics to figure out
    // addMetric("/startingLocation/x"); 
    // addMetric("/startingLocation/y"); 
    // addMetric("/rightDistance"); 
    // addMetric("/leftDistance"); 
    // addMetric("/isZeroed");
    // addMetric("/headingAngle");

    try {
      String csvFileName = "telemetry_" + System.currentTimeMillis() + ".csv";
      File csvFile = new File(RobotLogManager.getDirectory(), csvFileName);
      csvPrinter = new CSVPrinter(new FileWriter(csvFile),
          CSVFormat.DEFAULT.withAllowMissingColumnNames(false).withTrim().withTrailingDelimiter());
      LOGGER.debug("linked");
    } catch (IOException e) {
      LOGGER.debug(e.getStackTrace());
      e.printStackTrace();
    }
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        close();
      }
    });
  }

  private class PrintTimer extends TimerTask {
  private Telemetry telemetry;

  private PrintTimer() {
    telemetry = Telemetry.getInstance();
  }

  @Override
  public void run() {
    telemetry.printCsvLine();
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



  /**
  * After WPILib updates the Sendables on the network tables, this grabs the 
  * values and puts them into a row in a CSV telemetry file.
  */
  public void updateTable() {
    if (RobotMap.ENABLE_TELEMETRY && !printedHeaders) {
      printHeaders();
      // timer = new Timer("Telemetry Timer", true);
      // timer.scheduleAtFixedRate(new PrintTimer(), 0, RobotMap.TELEMETRY_TIMER_MS);
    }
    printCsvLine(); // Temp for timing
  }

  private void printHeaders() {
  if (csvPrinter != null) {
    try {
      csvPrinter.print("Time (ms)");
      csvPrinter.print("Iteration Time (ms)");
      csvPrinter.print("Robot Mode");
      for (String metricKey :  stringMetrics.keySet()) {
        csvPrinter.print(metricKey);
      }
      for (String metricKey :  booleanMetrics.keySet()) {
        csvPrinter.print(metricKey);
      }
      for (String metricKey :  doubleMetrics.keySet()) {
        csvPrinter.print(metricKey);
      }
      csvPrinter.println();
      startTime = System.currentTimeMillis();
      lastIterationTime = 0;
    } catch (IOException e) {
      LOGGER.debug(e.getStackTrace());
      e.printStackTrace();
    }
  }
  printedHeaders = true;
  }

  void printCsvLine() {
    if (csvPrinter != null) {
      try {
        long currentTime = System.currentTimeMillis() - startTime;
        csvPrinter.print(currentTime);
        csvPrinter.print(currentTime - lastIterationTime);
        lastIterationTime = currentTime;
        csvPrinter.print(robotMode);
        for (Supplier<String> metricSupplier :  stringMetrics.values()) {
          csvPrinter.print(metricSupplier.get());
        }
        for (BooleanSupplier metricSupplier :  booleanMetrics.values()) {
          csvPrinter.print(metricSupplier.getAsBoolean());
        }
        for (DoubleSupplier metricSupplier :  doubleMetrics.values()) {
          csvPrinter.print(metricSupplier.getAsDouble());
        }
        csvPrinter.println();
      } catch (IOException e) {
        LOGGER.error(e.getStackTrace());
        e.printStackTrace();
      }
    }
  }

  /**
  * For making sure data is written to csv. Call from robot init disabled.
  */
  public void flush() {
    if (csvPrinter != null) {
      try {
        csvPrinter.flush();
      } catch (IOException e) {
        LOGGER.debug(e.getStackTrace());
        e.printStackTrace();
      }
    }
  }

  public void robotMode(RobotMode robotMode) {
    this.robotMode = robotMode;
  }

  private void close() {
    try {
      if (csvPrinter != null) {
        boolean finalFlush = true;
        csvPrinter.close(finalFlush);
      }
      instance = null;
    } catch (IOException e) {
      LOGGER.debug(e.getMessage());
    }
  }

}