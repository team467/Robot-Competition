/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.draw;

import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.simulator.Robot;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 */
public class RobotShape {
	
	// Robot Shapes
	private Group robotShape = new Group();
	private Rectangle chassisShape = null;
	private Rectangle elevatorShape = null;

	// Network Tables
	RobotData data = RobotData.getInstance();
	
	public static final boolean RUN_LOCAL = true;
	
	private Robot robot; // For local processing
	
	public RobotShape() {
		
		if (RUN_LOCAL) {
			robot = new Robot();
			robot.robotInit();
		}		
	}
	
	public void init() {
		if (RUN_LOCAL) {
			robot.autonomousInit();
		} else {
			data.startClient();
		}
	}

	public Group createRobotShape() {

		chassisShape = new Rectangle(RobotMap.WHEEL_BASE_LENGTH*12/2, RobotMap.WHEEL_BASE_WIDTH*12 , Color.DARKSLATEGREY);
		chassisShape.relocate(FieldShape.FIELD_OFFSET_Y, FieldShape.FIELD_OFFSET_X);

		elevatorShape = new Rectangle(RobotMap.WHEEL_BASE_LENGTH*12/2, (RobotMap.WHEEL_BASE_WIDTH*12 - 4), Color.WHITESMOKE);
		elevatorShape.relocate(FieldShape.FIELD_OFFSET_Y + (RobotMap.WHEEL_BASE_LENGTH/2) * 12, (FieldShape.FIELD_OFFSET_X + 2));
		
		robotShape.setBlendMode(BlendMode.SRC_OVER);
		robotShape.getChildren().add(chassisShape);
		robotShape.getChildren().add(elevatorShape);
		
		return robotShape;
	}
	
	public void draw() {
		if(RUN_LOCAL) {
			robot.autonomousPeriodic();
		} else {
			data.receive();			
		}
		robotShape.relocate((FieldShape.FIELD_OFFSET_Y + data.leftY() * 12),
				(FieldShape.FIELD_OFFSET_X + (data.leftX() + RobotMap.WHEEL_BASE_WIDTH/2) * 12));
		robotShape.setRotate(Math.toDegrees(data.heading()));
	}

}
