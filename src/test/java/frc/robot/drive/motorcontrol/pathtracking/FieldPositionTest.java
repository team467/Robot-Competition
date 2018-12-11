package frc.robot.drive.motorcontrol.pathtracking;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

// @DisplayName("Test ability to derive field position from sensor data ")
public class FieldPositionTest {

  private static FieldPosition fieldPosition;

  @BeforeClass
  // @DisplayName("Initialize field position for test")
  public static void initAll() {
    fieldPosition = FieldPosition.getInstance();
    fieldPosition.init(0, 0, 90.0, 0.0, 0, 0);
  }

  // @DisplayName("Test simple forward movement")
  @Test
  public void positionAfter3FootForwardMovementShouldIncrement3FootOnYAxisTest() {
    double fakeSensorReadingLeft =  3;
    double fakeSensorReadingRight =  3;
    fieldPosition.update(fakeSensorReadingLeft, fakeSensorReadingRight);

    double tolerance = 0.01;
    assertEquals(0.0, fieldPosition.fieldX(), tolerance);
    assertEquals(3.0, fieldPosition.fieldY(), tolerance);
  }

    

}