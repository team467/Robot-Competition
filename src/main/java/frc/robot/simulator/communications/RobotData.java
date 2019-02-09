package frc.robot.simulator.communications;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.drive.motorcontrol.pathplanning.AutonomousPlan;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.gui.Coordinate;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

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
  
  private RobotData() {
    // TODO: Fix when we figure out how to load native WPI lib modules
    // tableInstance = NetworkTableInstance.getDefault();
    // table = tableInstance.getTable("datatable").getSubTable("/robotmapdata");
    
    dataRow = new RobotMapData();
    isZeroed = false;
    tableInstance = NetworkTableInstance.getDefault();
    table  = tableInstance.getTable("Telemetry");
    send();
    LOGGER.info("A---------------A");
    log();

    addToHistory();
    LOGGER.info("C---------------C");
    log();
  }
  
  public static RobotData getInstance() {
    if (instance == null) {
      LOGGER.info("D-------D-------D");
      instance = new RobotData();
    }
    LOGGER.info("B---------------B");
    instance.log();
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
    tableInstance.startClient("10.4.67.23");
    tableInstance.setUpdateRate(0.01);
  }

  /**
   * Puts the data onto the network table.
   */
  public void send() {
    dataRow.send(table);
  }
  public void log(){
    dataRow.log(table);
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
  public void flush(CSVFile file){
    dataRow.flush(file);
  }
  public void load(CSVFile file){
    dataRow.load(file);
  }
  
}
