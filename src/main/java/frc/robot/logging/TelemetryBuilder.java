package frc.robot.logging;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import frc.robot.RobotMap;
import frc.robot.Robot.RobotMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.Logger;

public class TelemetryBuilder extends SendableBuilderImpl implements SendableBuilder {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(TelemetryBuilder.class.getName());

  private static TelemetryBuilder instance = null;

  /**
   * Update the network table values by calling the getters for all properties.
   * Extended to print out the values to the CSV.
   */
  private TreeSet<String> metrics;

  private boolean printedHeaders = false;

  private CSVPrinter csvPrinter = null;

  private long startTime = -1;
  private long lastIterationTime = -1;

  private RobotMode robotMode;

  private Timer timer;

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */
  public static TelemetryBuilder getInstance() {
    if (instance == null) {
      instance = new TelemetryBuilder();
    }

    return instance;
  }

  /**
   * Creates the telemtry builder instance with the correct location for the CSV
   * output files.
   */
  public TelemetryBuilder() {
    super();
    super.setTable(NetworkTableInstance.getDefault().getTable("Telemetry"));
    robotMode = RobotMode.STARTED;

    // metrics = new ArrayList<String>();
    // addMetric("/startingLocation/x"); 
    // addMetric("/startingLocation/y"); 
    // addMetric("/rightDistance"); 
    // addMetric("/leftDistance"); 
    // addMetric("/isZeroed");
    // addMetric("/headingAngle");

    // try {
    //   String csvFileName = "telemetry_" + System.currentTimeMillis() + ".csv";
    //   File csvFile = new File(RobotLogManager.getDirectory(), csvFileName);
    //   csvPrinter = new CSVPrinter(new FileWriter(csvFile),
    //       CSVFormat.DEFAULT.withAllowMissingColumnNames(false).withTrim().withTrailingDelimiter());
    //   LOGGER.debug("linked");
    // } catch (IOException e) {
    //   LOGGER.debug(e.getStackTrace());
    //   e.printStackTrace();
    // }
    // Runtime.getRuntime().addShutdownHook(new Thread() {

    //   @Override
    //   public void run() {
    //     close();
    //   }

    // });
    
  }

  private class PrintTimer extends TimerTask {
    private TelemetryBuilder telemetry;

    private PrintTimer() {
      telemetry = TelemetryBuilder.getInstance();
    }

    @Override
    public void run() {
      telemetry.printCsvLine();
    }

  }

  /**
   * After WPILib updates the Sendables on the network tables, this grabs the 
   * values and puts them into a row in a CSV telemetry file.
   */
  public void updateTable() {
    super.updateTable();
    // if (RobotMap.ENABLE_TELEMETRY && !printedHeaders) {
    //   printHeaders();
    //   timer = new Timer("Telemetry Timer", true);
    //   timer.scheduleAtFixedRate(new PrintTimer(), 0, RobotMap.TELEMETRY_TIMER_MS);
    // }
  }

  private void printHeaders() {
    metrics = new TreeSet<String>();
    NetworkTable table = super.getTable();
    if (csvPrinter != null) {
      try {
        for (String entry : table.getKeys()) {
          metrics.add(entry);
        }
        csvPrinter.print("Time (ms)");
        csvPrinter.print("Iteration Time (ms)");
        csvPrinter.print("Robot Mode");
        for (String metric:  metrics) {
          csvPrinter.print(metric);
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
        NetworkTable table = super.getTable();
        long currentTime = System.currentTimeMillis() - startTime;
        csvPrinter.print(currentTime);
        csvPrinter.print(currentTime - lastIterationTime);
        lastIterationTime = currentTime;
        csvPrinter.print(robotMode);
        for (String metric:  metrics) {
          NetworkTableEntry entry = table.getEntry(metric);
          NetworkTableType type = entry.getType();
          String text;
          switch (type) {
            case kDouble: {
              text = String.format("%10.5f", entry.getDouble(Double.NaN));
              break;
            }
            case kString: {
              text = entry.getString("n/a");
              break;
            }
            case kBoolean: {
              text = String.valueOf(entry.getBoolean(false));
              break;
            }
            default:
              text = String.valueOf(entry.getType().toString());
              break;
          }
          csvPrinter.print(text);
          LOGGER.debug(text);
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