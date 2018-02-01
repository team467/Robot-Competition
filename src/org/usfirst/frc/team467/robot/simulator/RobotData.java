/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 *
 */
public class RobotData {

	private double prevRightPositionReading;
	private double prevLeftPositionReading;
	private double rightPositionReading;
	private double leftPositionReading;
	
	ArrayList<RobotMapData> data = new ArrayList<RobotMapData>();
	RobotMapData dataRow = null;
		
	// Network Tables for getting info from robot
	private NetworkTableInstance tableInstance;
	private NetworkTable table;
	
	private DecimalFormat df = new DecimalFormat("####0.00");
	
	private static RobotData instance = null;
	
	private RobotData() {
		tableInstance = NetworkTableInstance.getDefault();
		table = tableInstance.getTable("datatable").getSubTable("/robotmapdata");
		
		dataRow = new RobotMapData();
		dataRow.rightX = (Robot.WIDTH/2);
		dataRow.rightY = 0;
		dataRow.leftX = -1 * (Robot.WIDTH/2);
		dataRow.leftY = 0;
		dataRow.headingAngle = 0;
		addToHistory();
		
		zeroPosition();
	}
	
	public void startServer() {
		tableInstance.startServer("networktables.ini", "127.0.0.1");
		tableInstance.setUpdateRate(0.01);
	}
	
	public void startClient() {
		tableInstance.startClient("127.0.0.1");
		tableInstance.setUpdateRate(0.01);
	}
	
	public static RobotData getInstance() {
		if (instance == null) {
			instance = new RobotData();
		}
		return instance;
	}
	
	private void addToHistory() {
		RobotMapData historyEntry = new RobotMapData();
		historyEntry.startPositionRightX = dataRow.startPositionRightX;
		historyEntry.startPositionRightY = dataRow.startPositionRightY;
		historyEntry.startPositionLeftX = dataRow.startPositionLeftX;
		historyEntry.startPositionLeftY = dataRow.startPositionLeftY;		
		historyEntry.rightX = dataRow.rightX;
		historyEntry.rightY = dataRow.rightY;
		historyEntry.leftX = dataRow.leftX;
		historyEntry.leftY = dataRow.leftY;
		historyEntry.headingAngle = dataRow.headingAngle;
		data.add(historyEntry);
	}

	public void zeroPosition() {
		rightPositionReading = 0;
		leftPositionReading = 0;
		prevRightPositionReading = 0;
		prevLeftPositionReading = 0;
	}
	
	public void startPosition(double x, double y) {
		dataRow.startPositionLeftX = x - Robot.WIDTH / 2;
		dataRow.startPositionLeftY = y;
		dataRow.startPositionRightX = x - Robot.WIDTH /2;
		dataRow.startPositionRightY = y;	
	}
	
	public void update(
			double rightPositionReading,
			double leftPositionReading) {
		this.prevLeftPositionReading = this.leftPositionReading;
		this.prevRightPositionReading = this.rightPositionReading;
		this.leftPositionReading = leftPositionReading;
		this.rightPositionReading = rightPositionReading;
		updateMapPosition();
		send();
		receive();
	}
	
	public void send() {
		table.getEntry("/startPositionRightX").setDouble(dataRow.startPositionRightX);
		table.getEntry("/startPositionRightY").setDouble(dataRow.startPositionRightY);
		table.getEntry("/startPositionLeftX").setDouble(dataRow.startPositionLeftX);
		table.getEntry("/startPositionLeftY").setDouble(dataRow.startPositionLeftY);		
		table.getEntry("/rightX").setDouble(dataRow.rightX);
		table.getEntry("/rightY").setDouble(dataRow.rightY);
		table.getEntry("/leftX").setDouble(dataRow.leftX);
		table.getEntry("/leftY").setDouble(dataRow.leftY);
		table.getEntry("/headingAngle").setDouble(dataRow.headingAngle);
	}
	
	public void receive() {
		dataRow.startPositionRightX = table.getEntry("/startPositionRightX").getDouble(dataRow.startPositionRightX);
		dataRow.startPositionRightY = table.getEntry("/startPositionRightY").getDouble(dataRow.startPositionRightY);
		dataRow.startPositionLeftX = table.getEntry("/startPositionLeftX").getDouble(dataRow.startPositionLeftX);
		dataRow.startPositionLeftY = table.getEntry("/startPositionLeftY").getDouble(dataRow.startPositionLeftY);		
		dataRow.rightX = table.getEntry("/rightX").getDouble(dataRow.rightX);
		dataRow.rightY = table.getEntry("/rightY").getDouble(dataRow.rightY);
		dataRow.leftX = table.getEntry("/leftX").getDouble(dataRow.leftX);
		dataRow.leftY = table.getEntry("/leftY").getDouble(dataRow.leftY);
		dataRow.headingAngle = table.getEntry("/headingAngle").getDouble(dataRow.headingAngle);

	}

	public void updateMapPosition() {
		
		System.out.println("Old Right = (" + df.format(dataRow.rightX) + "," + df.format(dataRow.rightY) + ")");
		System.out.println("Old Left = (" + df.format(dataRow.leftX) + "," + df.format(dataRow.leftY) + ")");
		
		double leftDistanceMoved = leftPositionReading - prevLeftPositionReading;
		double rightDistanceMoved = rightPositionReading - prevRightPositionReading;

		dataRow.leftX 	+= leftDistanceMoved 	* Math.sin(dataRow.headingAngle);
		dataRow.rightX 	+= rightDistanceMoved 	* Math.sin(dataRow.headingAngle);
		dataRow.leftY 	+= leftDistanceMoved 	* Math.cos(dataRow.headingAngle);
		dataRow.rightY 	+= rightDistanceMoved 	* Math.cos(dataRow.headingAngle);
		
		System.out.println("Orig Heading: " + Math.toDegrees(dataRow.headingAngle));
		dataRow.headingAngle = -1 * Math.atan2((dataRow.rightY - dataRow.leftY), (dataRow.rightX - dataRow.leftX));
		System.out.println("New Heading: " + df.format(Math.toDegrees(dataRow.headingAngle)));
		
		System.out.println("New Right (x,y) = (" + df.format(dataRow.rightX) + "," + df.format(dataRow.rightY) + ")");
		System.out.println("New Left (x,y) = (" + df.format(dataRow.leftX) + "," + df.format(dataRow.leftY) + ")");
		System.out.println();
		
	}
	
	public double rightX() {
		return dataRow.startPositionRightX + dataRow.rightX;
	}
	
	public double rightY() {
		return dataRow.startPositionRightY + dataRow.rightY;
	}
	
	public double leftX() {
		return dataRow.startPositionLeftX + dataRow.leftX;
	}
	
	public double leftY() {
		return dataRow.startPositionLeftY + dataRow.leftY;
	}
	
	public double heading() {
		return dataRow.headingAngle;
	}

}
