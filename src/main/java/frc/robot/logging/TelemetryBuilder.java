package frc.robot.logging;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.logging.log4j.Logger;

public class TelemetryBuilder extends SendableBuilderImpl implements SendableBuilder {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(TelemetryBuilder.class.getName());

  private static TelemetryBuilder instance = null;

  private boolean printedHeaders = false;
  private CSVPrinter csvPrinter = null;

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
    try {
      csvPrinter = new CSVPrinter(new FileWriter("csv.txt"),
          CSVFormat.DEFAULT.withAllowMissingColumnNames(false).withTrim().withTrailingDelimiter());
      LOGGER.info("linked");
    } catch (IOException e) {
      LOGGER.error(e.getStackTrace());
      e.printStackTrace();
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
         close();
      }

    });
  }

  /**
   * Update the network table values by calling the getters for all properties.
   * Extended to print out the values to the CSV.
   */
  public void updateTable() {
    super.updateTable();
    try {
      NetworkTable table = super.getTable();
      if (!printedHeaders) {
        csvPrinter.printRecord(table.getKeys());
        printedHeaders = true;
      }
      for (String key : table.getKeys()) {
        NetworkTableEntry entry = table.getEntry(key);
        NetworkTableType type = entry.getType();
        String text = "n/a";
        switch (type) {
        case kDouble: {
          text = String.valueOf(entry.getDouble(Double.NaN));
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
          text = "n/a";
          break;
        }
        csvPrinter.print(text);
        LOGGER.info(text);
      }
      csvPrinter.println();
    } catch (IOException e) {
      LOGGER.error(e.getStackTrace());
      e.printStackTrace();
      int hi = 1;
    }
  }

  public void close() {
    try {
      csvPrinter.close();
    } catch (IOException e) {
      LOGGER.info(e.getMessage());
    }
  }

}