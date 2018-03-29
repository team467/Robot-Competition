/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.communications;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.usfirst.frc.team467.robot.Elevator;
import org.usfirst.frc.team467.robot.Elevator.Stops;
import org.usfirst.frc.team467.robot.simulator.gui.Coordinate;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 *
 */
public class RobotData {

	boolean isZeroed;
	
	ArrayList<RobotMapData> data = new ArrayList<RobotMapData>();
	RobotMapData dataRow = null;
		
	// Network Tables for getting info from robot
	private NetworkTableInstance tableInstance;
	private NetworkTable table;
	
	private static final Logger LOGGER = LogManager.getLogger(RobotData.class); 
	
	private static RobotData instance = null;
	
	private RobotData() {		
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

	public void zero() {
		dataRow.rightPosition = 0.0;
		dataRow.leftPosition = 0.0;
		dataRow.isZeroed = true;
		isZeroed = true;
	}
	
	/**
	 * Update the position readings for determining the map position and heading.
	 * 
	 * @param rightPositionReading the robot right position reading in feet
	 * @param leftPositionReading the robot left position reading in feet
	 */
	public void updateDrivePosition(
			double rightDistance,
			double leftDistance) {
		dataRow.rightPosition = rightDistance;
		dataRow.leftPosition = leftDistance;
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

	public Coordinate startingLocation() {
		return dataRow.startingLocation;
	}
	
	public boolean isZeroed() {
		return (dataRow.isZeroed || isZeroed);
	}
	
	public void clearZeroed() {
		dataRow.isZeroed = false;
		isZeroed = false;
	}

	public void elevatorHeight(Stops stop) {
		dataRow.elevatorHeight = stop.height;
	}
	
	public void elevatorheight(double height) {
		dataRow.elevatorHeight = height;
	}
	
	public Elevator.Stops elevatorStop() {
		Stops stop = Stops.floor;
		if (dataRow.elevatorHeight < Stops.fieldSwitch.height) {
			stop = Elevator.Stops.floor;
		} else if (dataRow.elevatorHeight < Stops.lowScale.height) {
			stop = Elevator.Stops.lowScale;
		} else {
			stop = Stops.highScale;
		}
		return stop;
	}
	
	public double elevatorHeight() {
		return dataRow.elevatorHeight;
	}
	
	public void grabberHasCube(boolean grabberHasCube) {
		dataRow.grabberHasCube = grabberHasCube;
	}
	
	public boolean grabberHasCube() {
		return dataRow.grabberHasCube;
	}
	
	public boolean visionSeesCube = false;
	public double cubeMinDistance = 0.0;
	public double cubeMaxDistance = 0.0;
	public double angleToCube = 0.0;

	public void cubeSpotted(double minDistance, double maxDistance, double angle) {
		dataRow.visionSeesCube = true;
		dataRow.cubeMinDistance = minDistance;
		dataRow.cubeMaxDistance = maxDistance;
		dataRow.angleToCube = angle;
	}
	
	public void lostCube() {
		dataRow.visionSeesCube = false;
	}
	
	public boolean canSeeCube() {
		return dataRow.visionSeesCube;
	}
	
	public double cubeAngle() {
		return dataRow.angleToCube;
	}
	
	public double cubeCenter() {
		return (dataRow.cubeMinDistance + dataRow.cubeMaxDistance) / 2;
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
	
}
