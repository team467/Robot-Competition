/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.communications;

/**
 * Holds data from the robot, used for organizing the network table data. 
 * Essentially a struct, so all public variables.
 */
public class RobotMapData {

	/**
	 *  The start positions on the 2D map of each middle wheel (right & left)
	 */
	public double startPositionRightX;
	public double startPositionRightY;
	public double startPositionLeftX;
	public double startPositionLeftY;
	
	/**
	 * The robot position of the left and right middle wheels
	 * translated into the GUI coordinates.
	 */
	public double rightX;
	public double rightY;
	public double leftX;
	public double leftY;
	
	/**
	 * The current robot heading
	 */
	public double headingAngle;

}
