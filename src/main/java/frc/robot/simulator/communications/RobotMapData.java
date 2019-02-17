package frc.robot.simulator.communications;

import edu.wpi.first.networktables.NetworkTable;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.gui.Coordinate;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;

/**
 * Holds data from the robot, used for organizing the network table data.
 * Essentially a struct, so all public variables.
 */
public class RobotMapData implements Serializable, Cloneable {

  private static final long serialVersionUID = 1L;
  /**
   * The start positions on the 2D map of each middle wheel center.
   */
  Coordinate startingLocation = new Coordinate(0.0, 0.0);

  private static final Logger LOGGER = RobotLogManager.getMainLogger(RobotMapData.class.getName());
  /**
   * The robot position of the left and right middle wheels.
   */
  public double rightPosition = 0.0;
  public double leftPosition = 0.0;

  public boolean isZeroed = false;

  /**
   * The current robot heading.
   */
  public double headingAngle = 0.0;

  /**
   * Creates a deep copy of the current data.
   */
  public RobotMapData clone() {
    RobotMapData clone = new RobotMapData();
    clone.startingLocation = this.startingLocation;
    clone.rightPosition = this.rightPosition;
    clone.leftPosition = this.leftPosition;
    clone.isZeroed = this.isZeroed;
    return clone;
  }

  /**
   * Puts the data onto the network table.
   */
  public void send(NetworkTable table) {
    //if (table != null) {
      table.getEntry("/startingLocation/x").setDouble(startingLocation.x);
      table.getEntry("/startingLocation/y").setDouble(startingLocation.y);
      table.getEntry("/rightDistance").setDouble(rightPosition);
      table.getEntry("/leftDistance").setDouble(leftPosition);
      table.getEntry("/isZeroed").setBoolean(isZeroed);
      table.getEntry("/headingAngle").setDouble(headingAngle);
      table.getEntry("/LoggingFileExists").setBoolean(new File("/media/sda1/logging/log4j2.yaml").exists());
    //}
  }

  public void log(NetworkTable table){
    LOGGER.debug(table.getEntry("/startingLocation/x").getDouble(startingLocation.x));
    LOGGER.debug(table.getEntry("/startingLocation/y").getDouble(startingLocation.y));
    LOGGER.debug(table.getEntry("/rightDistance").getDouble(rightPosition));
    LOGGER.debug(table.getEntry("/leftDistance").getDouble(leftPosition));
    LOGGER.debug(table.getEntry("/isZeroed").getBoolean(isZeroed));
    LOGGER.debug(table.getEntry("/headingAngle").getDouble(headingAngle));
  }

  /**
   * Gets the information from the network table.
   */
  public void receive(NetworkTable table) {
    startingLocation.x = table.getEntry("/startingLocation/x").getDouble(startingLocation.x);
    startingLocation.y = table.getEntry("/startingLocation/y").getDouble(startingLocation.y);
    rightPosition = table.getEntry("/rightDistance").getDouble(rightPosition);
    leftPosition = table.getEntry("/leftDistance").getDouble(leftPosition);
    LOGGER.info("right: "+table.getEntry("/rightDistance").getDouble(rightPosition) + ", left: " + table.getEntry("/leftDistance").getDouble(leftPosition));
    isZeroed = table.getEntry("/isZeroed").getBoolean(false);
    headingAngle = table.getEntry("/headingAngle").getDouble(headingAngle);
  }

  public void flush(CSVFile csvFile) {
    DecimalFormat df = new DecimalFormat("###.####");
    csvFile.addRow();
    csvFile.pushVar(df.format(leftPosition));
    csvFile.pushVar(df.format(rightPosition));
    csvFile.pushVar(startingLocation.x);
    csvFile.pushVar(startingLocation.y);
    csvFile.pushVar(isZeroed);
  }

  public void load(CSVFile csvFile) {
    leftPosition = Double.parseDouble((String)csvFile.get(0));
    rightPosition = Double.parseDouble((String)csvFile.get(1));
    startingLocation.x = Double.parseDouble((String)csvFile.get(2));
    startingLocation.y = Double.parseDouble((String)csvFile.get(3));
    isZeroed = Boolean.parseBoolean((String)csvFile.get(4));
    csvFile.currentRow++;
  }
}
