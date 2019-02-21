package frc.robot.logging;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import java.io.File;
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

  private long startTime = -1;

  private PowerDistributionPanel pdp = new PowerDistributionPanel();

  private static Integer[] pins = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
  
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
      String csvFileName = "telemetry_" + System.currentTimeMillis() + ".csv";
      File csvFile = new File(RobotLogManager.getDirectory(), csvFileName);
      csvPrinter = new CSVPrinter(new FileWriter(csvFile),
          CSVFormat.DEFAULT.withAllowMissingColumnNames(false).withTrim().withTrailingDelimiter());
      LOGGER.debug("linked");
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
  static String[] keys = {
    "/startingLocation/x", 
    "/startingLocation/y", 
    "/rightDistance", 
    "/leftDistance", 
    "/isZeroed", 
    "/headingAngle",
    "Intake Arm Command",
    "Intake Roller Command",
    "Cargo Wrist Command",
    "Cargo Wrist State",
    "Cargo Wrist Height Proportion",
    "Cargo Claw Command",
    "Hatch Arm Command",
    "Hatch Launcher Command",
    "Turret Position",
    "Turret Target"
  };

  public void updateTable() {
    super.updateTable();
    if (csvPrinter != null) {
      try {
        NetworkTable table = super.getTable();
        if (!printedHeaders) {
          for (Object o: (Object[]) keys) {
            csvPrinter.print(o);
          }
          csvPrinter.print("voltage");
          for (Object o: (Object[]) pins) {
            csvPrinter.print(o);
          }
          csvPrinter.print("Time in millis");
          csvPrinter.println();
          printedHeaders = true;
          startTime = System.nanoTime();
        }
        for (String key : keys) {
          NetworkTableEntry entry = table.getEntry(key);
          NetworkTableType type = entry.getType();
          String text = "n/a";
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
        
        csvPrinter.print(String.format("%10.5f",pdp.getVoltage()));
        for (int pin: pins) {
          csvPrinter.print(String.format("%10.5f",pdp.getCurrent(pin)));
        }
        // csvPrinter.print(System.nanoTime() / 1000000.0);
        csvPrinter.print((System.nanoTime() - startTime) / 1000000.0);
        csvPrinter.println();
      } catch (IOException e) {
        LOGGER.error(e.getStackTrace());
        e.printStackTrace();
      }
    }
  }

  private void close() {
    try {
      if (csvPrinter != null) {
        csvPrinter.close(true);
      }
      instance = null;
    } catch (IOException e) {
      LOGGER.debug(e.getMessage());
    }
  }

}