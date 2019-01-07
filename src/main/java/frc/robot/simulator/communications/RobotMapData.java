package frc.robot.simulator.communications;

import edu.wpi.first.networktables.NetworkTable;

import frc.robot.simulator.gui.Coordinate;

import java.io.Serializable;

/**
 * Holds data from the robot, used for organizing the network table data. 
 * Essentially a struct, so all public variables.
 */
public class RobotMapData implements Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  /**
   *  The start positions on the 2D map of each middle wheel center.
   */
  Coordinate startingLocation = new Coordinate(0.0, 0.0);
  
  /**
   * The robot position of the left and right middle wheels.
   */
  public double rightPosition = 0.0;
  public double leftPosition = 0.0;

  public boolean isZeroed = false;
  
  public double elevatorHeight = 0.0;
  public boolean grabberHasCube = false;
  
  public boolean visionSeesCube = false;
  public double cubeMinDistance = 0.0;
  public double cubeMaxDistance = 0.0;
  public double angleToCube = 0.0;
  
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
    clone.elevatorHeight = this.elevatorHeight;
    clone.grabberHasCube = this.grabberHasCube;
    clone.visionSeesCube = this.visionSeesCube;
    clone.cubeMinDistance = this.cubeMinDistance;
    clone.cubeMaxDistance = this.cubeMaxDistance;
    clone.angleToCube = this.angleToCube;
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
      table.getEntry("/elevatorHeight").setDouble(elevatorHeight);
      table.getEntry("/grabberHasCube").setBoolean(grabberHasCube);
      table.getEntry("/visionSeesCube").setBoolean(visionSeesCube);
      table.getEntry("/cubeMinDistance").setDouble(cubeMinDistance);
      table.getEntry("/cubeMaxDistance").setDouble(cubeMaxDistance);
      table.getEntry("/angleToCube").setDouble(angleToCube);
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
    elevatorHeight = table.getEntry("/elevatorHeight").getDouble(elevatorHeight);
    grabberHasCube = table.getEntry("/grabberHasCube").getBoolean(grabberHasCube);
    visionSeesCube = table.getEntry("/visionSeesCube").getBoolean(visionSeesCube);
    cubeMinDistance = table.getEntry("/cubeMinDistance").getDouble(cubeMinDistance);
    cubeMaxDistance = table.getEntry("/cubeMaxDistance").getDouble(cubeMaxDistance);
    angleToCube = table.getEntry("/angleToCube").getDouble(angleToCube);
  }

}
