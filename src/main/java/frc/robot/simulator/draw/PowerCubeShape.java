package frc.robot.simulator.draw;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PowerCubeShape {

  // Cube Dimensions
  private static final double CUBE_BASE_LENGTH_INCHES = 13.0;

  // Cube Shapes
  private Group cubeShape = new Group();
  private Rectangle powerCubeShape = null;
  private double positionY;
  private double positionX;

  public PowerCubeShape (double x, double y) {
    positionX = x;
    positionY = y;
  }

  public Group createPowerCube() {
    powerCubeShape = new Rectangle(CUBE_BASE_LENGTH_INCHES, CUBE_BASE_LENGTH_INCHES, Color.YELLOW);
    powerCubeShape.relocate(FieldShape.FIELD_OFFSET_Y, FieldShape.FIELD_OFFSET_X);

    cubeShape.getChildren().add(powerCubeShape);
    cubeShape.setVisible(false);

    return cubeShape;
  }

  public void draw() {
    cubeShape.relocate(FieldShape.FIELD_OFFSET_Y + positionY,
        (FieldShape.FIELD_OFFSET_X + positionX));
    cubeShape.setVisible(true);
  }
}

