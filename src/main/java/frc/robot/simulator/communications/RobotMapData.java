package frc.robot.simulator.communications;

import edu.wpi.first.networktables.NetworkTable;

import frc.robot.simulator.gui.Coordinate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

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
    if (table != null) {
      table.getEntry("/startingLocation/x").setDouble(startingLocation.x);
      table.getEntry("/startingLocation/y").setDouble(startingLocation.y);
      table.getEntry("/rightDistance").setDouble(rightPosition);
      table.getEntry("/leftDistance").setDouble(leftPosition);
      table.getEntry("/isZeroed").setBoolean(isZeroed);
      table.getEntry("/headingAngle").setDouble(headingAngle);
    }
  }

  /**
   * Gets the information from the network table.
   */
  public void receive(NetworkTable table) {
    startingLocation.x = table.getEntry("/startingLocation/x").getDouble(startingLocation.x);
    startingLocation.y = table.getEntry("/startingLocation/y").getDouble(startingLocation.y);
    rightPosition = table.getEntry("/rightDistance").getDouble(rightPosition);
    leftPosition = table.getEntry("/leftDistance").getDouble(leftPosition);
    isZeroed = table.getEntry("/isZeroed").getBoolean(isZeroed);
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
