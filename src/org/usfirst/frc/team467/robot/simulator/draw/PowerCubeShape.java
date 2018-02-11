/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.draw;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 */
public class PowerCubeShape {
	
	// Cube Dimensions
	private static final double CUBE_BASE_LENGTH_INCHES = 13.0;
	

	
	// Robot Shapes
	private Group cubeShape = new Group();
	private Rectangle powerCubeShape = null;
	
	public PowerCubeShape() {
		
	}
	
	
	public Group createPowerCube() {
		powerCubeShape = new Rectangle(CUBE_BASE_LENGTH_INCHES, CUBE_BASE_LENGTH_INCHES, Color.YELLOW);
		powerCubeShape.relocate(FieldShape.FIELD_OFFSET_Y, FieldShape.FIELD_OFFSET_X);
		
		cubeShape.getChildren().add(powerCubeShape);
		
		return cubeShape;
	}
	
	public void draw() {
		cubeShape.relocate(FieldShape.FIELD_OFFSET_Y +120,
				(FieldShape.FIELD_OFFSET_X+120));
	}

}
