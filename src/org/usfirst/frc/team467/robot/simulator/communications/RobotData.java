/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.communications;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.RobotMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 *
 */
public class RobotData {

	private double prevRightPositionReading;
	private double prevLeftPositionReading;
	private double rightPositionReading;
	private double leftPositionReading;
	
	private double heading;
	private double absoluteHeading;
	private double x;
	private double y;
	private double absoluteX;
	private double absoluteY;
	
	ArrayList<RobotMapData> data = new ArrayList<RobotMapData>();
	RobotMapData dataRow = null;
		
	// Network Tables for getting info from robot
	private NetworkTableInstance tableInstance;
	private NetworkTable table;
	
	private static final Logger LOGGER = Logger.getLogger(RobotData.class); 
	private DecimalFormat df = new DecimalFormat("####0.00");
	
	private static RobotData instance = null;
	
	private RobotData() {
		LOGGER.setLevel(Level.INFO);
		
		tableInstance = NetworkTableInstance.getDefault();
		table = tableInstance.getTable("datatable").getSubTable("/robotmapdata");
		
		dataRow = new RobotMapData();
		dataRow.rightX = (RobotMap.WHEEL_BASE_WIDTH/2);
		dataRow.rightY = 0;
		dataRow.leftX = -1 * (RobotMap.WHEEL_BASE_WIDTH/2);
		dataRow.leftY = 0;
		dataRow.headingAngle = 0;

		addToHistory();
		
		rightPositionReading = 0;
		leftPositionReading = 0;
		prevRightPositionReading = 0;
		prevLeftPositionReading = 0;
		
		heading = 0;
		absoluteHeading = 0;
		x = 0;
		y = 0;
		absoluteX = 0;
		absoluteY = 0;
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

	public void startPosition(double x, double y) {
		dataRow.startPositionLeftX = x;
		dataRow.startPositionLeftY = y;
		dataRow.startPositionRightX = x;
		dataRow.startPositionRightY = y;	
	}

	public void zero() {
		rightPositionReading = 0;
		leftPositionReading = 0;
		prevRightPositionReading = 0;
		prevLeftPositionReading = 0;
		absoluteHeading += heading;
		heading = 0;
		absoluteX += x;
		absoluteY += y;
		x = 0;
		y = 0;
	}
	
	public void update(
			double rightPositionReading,
			double leftPositionReading) {
		this.prevLeftPositionReading = this.leftPositionReading;
		this.prevRightPositionReading = this.rightPositionReading;
		this.leftPositionReading = leftPositionReading;
		this.rightPositionReading = rightPositionReading;
		updateMapPosition((leftPositionReading-prevLeftPositionReading), (rightPositionReading-prevRightPositionReading));
		send();
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
	
	public void updateMapPosition(double leftDistance, double rightDistance) {
		
		double radius = (RobotMap.WHEEL_BASE_WIDTH / 2);
		double averageMove = (leftDistance + rightDistance) / 2;
		double leftArcLength = (leftDistance - averageMove);
		double rightArcLength = (rightDistance - averageMove);
		LOGGER.debug("Moves: Left = " + df.format(leftArcLength) + " Right = " + df.format(rightArcLength) + " Average = " + averageMove);
		double leftTheta = leftArcLength / radius;
		double rightTheta = rightArcLength / radius;
		double theta = (rightTheta - leftTheta) /2;
		LOGGER.debug("Thetas: Left = " + df.format(leftTheta) + " Right = " + df.format(rightTheta));
		double leftX = radius * Math.cos(theta);
		double leftY = radius * Math.sin(theta);
		double changeInHeading = -1 * Math.atan2((leftY), (leftX));
		String logMessage = ("Heading: " + df.format(Math.toDegrees(heading)));		
		heading += changeInHeading;
		logMessage += " + " + df.format(Math.toDegrees(changeInHeading)) + " = " + df.format(Math.toDegrees(heading));
		LOGGER.debug(logMessage);
		logMessage = "Position: (" + df.format(x) + "," + df.format(y) + ") + (";
		dataRow.headingAngle = heading + absoluteHeading;
		double addedX = averageMove * Math.sin(dataRow.headingAngle);
		double addedY = averageMove * Math.cos(dataRow.headingAngle);
		x += addedX;
		y += addedY;
		logMessage += df.format(addedX) + "," + df.format(addedY) +") = (" + df.format(x) + "," + df.format(y) + ")";
		LOGGER.debug(logMessage);
		
		// X & Y swapped on the screen
		dataRow.leftX = x + absoluteX + radius * Math.cos(dataRow.headingAngle);
		dataRow.leftY = y + absoluteY + radius * Math.sin(dataRow.headingAngle);
		dataRow.rightX = x + absoluteX + radius * Math.cos(dataRow.headingAngle);
		dataRow.rightY = y + absoluteY + -1 * radius * Math.sin(dataRow.headingAngle);
		LOGGER.debug("Screen Postion: [ " + df.format(Math.toDegrees(dataRow.headingAngle)) 
			+ ", (" + df.format(dataRow.leftX) + "," + df.format(dataRow.leftY) + "), ("
			+ df.format(dataRow.rightX) + "," + df.format(dataRow.rightY) + ")]");
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
