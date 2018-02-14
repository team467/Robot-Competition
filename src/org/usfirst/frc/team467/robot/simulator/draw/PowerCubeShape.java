/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.draw;

import org.usfirst.frc.team467.robot.simulator.gui.MapController;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 */
public class PowerCubeShape {
	
	// Cube Dimensions
	private static final double CUBE_BASE_LENGTH_INCHES = 13.0;
	
	// Cube Shapes
	private Group cubeShape = new Group();
	private Rectangle powerCubeShape = null;
	double y_pos;
	double x_pos;
	
	public PowerCubeShape (double x, double y) {
		x_pos = x;
		y_pos = y;
		
	}
	
	public Group createPowerCube() {
		powerCubeShape = new Rectangle(CUBE_BASE_LENGTH_INCHES, CUBE_BASE_LENGTH_INCHES, Color.YELLOW);
		powerCubeShape.relocate(FieldShape.FIELD_OFFSET_Y, FieldShape.FIELD_OFFSET_X);
		
		cubeShape.getChildren().add(powerCubeShape);
		
		return cubeShape;
	}
	
	public void draw() {
			cubeShape.relocate(FieldShape.FIELD_OFFSET_Y + y_pos,
					(FieldShape.FIELD_OFFSET_X + x_pos));
	}
}

