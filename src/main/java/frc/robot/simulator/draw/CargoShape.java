package frc.robot.simulator.draw;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CargoShape {

  // Cube Dimensions
  private static final double CARGO_DIAMETER = 13.0;

  // Cube Shapes
  private Circle CargoShape = null;
  private double positionY;
  private double positionX;

  public Circle shape() {
    return CargoShape;
  }

  public CargoShape (double x, double y) {
    positionX = x;
    positionY = y;
    CargoShape = new Circle(CARGO_DIAMETER/2.0, Color.ORANGE);
    CargoShape.relocate(FieldShape.FIELD_OFFSET_Y + positionY,
        (FieldShape.FIELD_OFFSET_X + positionX));
        CargoShape.setVisible(true);
  }

}

