package frc.robot.simulator.communications;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import frc.robot.Robot;
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

  private static NetworkTableEntry testEntry;
  private static NetworkTableEntry startingLocationXEntry;
  private static NetworkTableEntry startingLocationYEntry;
  private static NetworkTableEntry rightDistanceEntry;
  private static NetworkTableEntry leftDistanceEntry;
  private static NetworkTableEntry zeroedEntry;
  private static NetworkTableEntry headingAngleEntry;
  
  public static void initNetworkTable(NetworkTable table) {
    testEntry = table.getEntry("/test");
    startingLocationXEntry = table.getEntry("/startingLocation/x");
    startingLocationYEntry = table.getEntry("/startingLocation/y");
    rightDistanceEntry = table.getEntry("/rightDistance");
    leftDistanceEntry = table.getEntry("/leftDistance");
    zeroedEntry = table.getEntry("/isZeroed");
    headingAngleEntry = table.getEntry("/headingAngle");
  }

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
  static int ticks = 1;
  static long testTime = 0;
  static long startXTime = 0;
  static long startYTime = 0;
  static long rightDistanceTime = 0;
  static long leftDistanceTime = 0;
  static long zeroTime = 0;
  static long headingAngleTime = 0;
  public void send(NetworkTable table) {
    // LOGGER.debug("avg time since last send:\t {} uS", Robot.dt/1000);
    startingLocationXEntry.setDouble(1000);

    testTime += Robot.dt;
    // LOGGER.error("time for test push:\t {} uS",testTime/ticks/1000);
    startingLocationXEntry.setDouble(startingLocation.x);

    startXTime += Robot.dt;
    // LOGGER.error("time for startX push:\t {} uS",startXTime/ticks/1000);
    startingLocationYEntry.setDouble(startingLocation.y);

    startYTime += Robot.dt;
    // LOGGER.error("time for startY push:\t {} uS",startYTime/ticks/1000);
    rightDistanceEntry.setDouble(rightPosition);

    rightDistanceTime += Robot.dt;
    // LOGGER.error("time for rightdistance push:\t {} uS",rightDistanceTime/ticks/1000);
    leftDistanceEntry.setDouble(leftPosition);

    leftDistanceTime += Robot.dt;
    // LOGGER.error("time for leftdistance push:\t {} uS",leftDistanceTime/ticks/1000);
    zeroedEntry.setBoolean(isZeroed);

    zeroTime += Robot.dt;
    // LOGGER.error("time for isZeroed push:\t {} uS",zeroTime/ticks/1000);
    headingAngleEntry.setDouble(headingAngle);

    headingAngleTime += Robot.dt;
    // LOGGER.error("time for headingangle push:\t {} uS",headingAngleTime/ticks/1000);
    // LOGGER.error("----------------------=---------");
    ticks++;
    // table.getEntry("/LoggingFileExists").setBoolean(new File("/media/sda1/logging/log4j2.yaml").exists());
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
    LOGGER.debug("right: " + table.getEntry("/rightDistance").getDouble(rightPosition) 
        + ", left: " + table.getEntry("/leftDistance").getDouble(leftPosition));
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
