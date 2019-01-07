package frc.robot.drive.motorcontrol.pathtracking;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class FieldPositionTest {

  private static FieldPosition fieldPosition;

  @BeforeClass
  public static void initAll() {
    fieldPosition = FieldPosition.getInstance();
    fieldPosition.init(0, 0, 90.0, 0.0, 0, 0);
  }

  @Test
  public void positionAfter3FootForwardMovementShouldIncrement3FootOnYAxisTest() {
    double fakeSensorReadingLeft =  3;
    double fakeSensorReadingRight =  3;
    fieldPosition.update(fakeSensorReadingLeft, fakeSensorReadingRight);

    double tolerance = 0.01;
    assertEquals(0.0, fieldPosition.x1(), tolerance);
    assertEquals(3.0, fieldPosition.y1(), tolerance);
  }

    

}