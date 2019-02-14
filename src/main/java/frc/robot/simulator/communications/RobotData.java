package frc.robot.simulator.communications;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.drive.motorcontrol.pathplanning.AutonomousPlan;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.gui.Coordinate;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;

// import org.apache.commons.csv.CSVRecord;

public class RobotData {

  boolean isZeroed;
  
  ArrayList<RobotMapData> data = new ArrayList<RobotMapData>();
  RobotMapData dataRow = null;
    
  // Network Tables for getting info from robot
  private NetworkTableInstance tableInstance;
  private NetworkTable table;

  // Temp, should move to separate transmit function
  AutonomousPlan course = null;
  
  private static final Logger LOGGER = RobotLogManager.getMainLogger(RobotData.class.getName()); 
  
  private static RobotData instance = null;
  private CSVFile csvFile = new CSVFile();
  
  private RobotData() {
    // TODO: Fix when we figure out how to load native WPI lib modules
    tableInstance = NetworkTableInstance.getDefault();
    table = tableInstance.getTable("datatable").getSubTable("/robotmapdata");
    
    dataRow = new RobotMapData();
    isZeroed = false;

    addToHistory();
  }
  
  public static RobotData getInstance() {
    if (instance == null) {
      instance = new RobotData();
    }
    return instance;
  }
  
  private void addToHistory() {
    data.add(dataRow.clone());
  }

  public void startingLocation(double x, double y) {
    dataRow.startingLocation.x = x;
    dataRow.startingLocation.y = y;
  }

  public Coordinate startingLocation() {
    return dataRow.startingLocation;
  }
  
  public void zero() {
    dataRow.rightPosition = 0.0;
    dataRow.leftPosition = 0.0;
    dataRow.isZeroed = true;
    isZeroed = true;
  }
  
  /**
   * Update the position readings for determining the map position and heading.
   * 
   * @param leftDistance the robot left position reading in feet
   * @param rightDistance the robot right position reading in feet
   */
  public void updateDrivePosition(
      double leftDistance,
      double rightDistance) {
    dataRow.rightPosition = rightDistance;
    dataRow.leftPosition = leftDistance;
  }
 
  public RobotData course(AutonomousPlan course) {
    this.course = course;
    return this; // For chaining
  }

  public AutonomousPlan course() {
    return course;
  }

  public double leftDistance() {
    return dataRow.leftPosition;
  }
  
  public double rightDistance() {
    return dataRow.rightPosition;
  }
  
  public double heading() {
    return dataRow.headingAngle;
  }
  
  public void heading(double headingAngle) {
    dataRow.headingAngle = headingAngle;
  }

  public boolean isZeroed() {
    return (dataRow.isZeroed || isZeroed);
  }
  
  public void clearZeroed() {
    dataRow.isZeroed = false;
    isZeroed = false;
  }

  // Start section for handling Network Tables
  
  public void startServer() {
    tableInstance.startServer("networktables.ini", "127.0.0.1");
    tableInstance.setUpdateRate(0.01);
  }
  
  public void startClient() {
    tableInstance.startClient("127.0.0.1");
    tableInstance.setUpdateRate(0.01);
  }
  
  /**
   * Puts the data onto the network table.
   */
  public void send() {
    dataRow.send(table);
  }
  
  /**
   * Gets the information from the network table.
   */
  public void receive() {
    dataRow.receive(table);
    
    if (dataRow.isZeroed) {
      isZeroed = true; // Only can turn flag true. Flag must be cleared only on receiving side.
    }
  }

  public void receiveCSV(CSVFile data) {
    dataRow.startingLocation.x = Double.parseDouble(csvFile.get(0).toString());
    dataRow.startingLocation.y = Double.parseDouble(csvFile.get(1).toString());
    dataRow.rightPosition = Double.parseDouble(csvFile.get(2).toString());
    dataRow.leftPosition = Double.parseDouble(csvFile.get(3).toString());
    dataRow.isZeroed = Boolean.parseBoolean(csvFile.get(4).toString());
    dataRow.headingAngle = Double.parseDouble(csvFile.get(5).toString());
    dataRow.elevatorHeight = Double.parseDouble(csvFile.get(6).toString());
    dataRow.grabberHasCube = Boolean.parseBoolean(csvFile.get(7).toString());
    dataRow.visionSeesCube = Boolean.parseBoolean(csvFile.get(8).toString());
    dataRow.cubeMinDistance = Double.parseDouble(csvFile.get(9).toString());
    dataRow.cubeMaxDistance = Double.parseDouble(csvFile.get(10).toString());
    dataRow.angleToCube = Double.parseDouble(csvFile.get(11).toString());
    csvFile.currentRow++;
  }
  
}
