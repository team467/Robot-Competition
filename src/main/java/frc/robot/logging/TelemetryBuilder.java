package frc.robot.logging;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.Logger;

public class TelemetryBuilder extends SendableBuilderImpl implements SendableBuilder {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(TelemetryBuilder.class.getName());

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
   * Creates the telemtry builder instance with the correct location for the CSV output files.
   */
  public TelemetryBuilder() {
    super();
    super.setTable(NetworkTableInstance.getDefault().getTable("Telemetry"));
    try {
      csvPrinter = new CSVPrinter(new FileWriter("csv.txt"), 
          CSVFormat.DEFAULT
          .withAllowMissingColumnNames(false)
          .withTrim()
          .withTrailingDelimiter());
    } catch (IOException e) {
      LOGGER.error(e.getStackTrace());
      e.printStackTrace();
    }
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
        csvPrinter.print(table.getEntry(key));
      }
      csvPrinter.println();
    } catch (IOException e) {
      LOGGER.error(e.getStackTrace());
      e.printStackTrace();
    }
  }


}