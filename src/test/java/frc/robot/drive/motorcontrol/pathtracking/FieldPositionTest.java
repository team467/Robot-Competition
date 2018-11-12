package frc.robot.drive.motorcontrol.pathtracking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test ability to derive field position from sensor data ")
public class FieldPositionTest {

    FieldPosition fieldPosition;

    @Test
    @DisplayName("Test simple forward movement")
    public void positionAfter3FootForwardMovementShouldIncrement3FootOnYAxisTest() {
        FieldPosition position = FieldPosition.getInstance();
        position.init(0, 0, 90.0, 0.0, 0, 0);

        double fakeSensorReadingLeft =  3;
        double fakeSensorReadingRight =  3;
        position.update(fakeSensorReadingLeft, fakeSensorReadingRight);

        double tolerance = 0.01;
        assertEquals(0.0, position.x(), tolerance);
        assertEquals(3.0, position.y(), tolerance);
    }

}