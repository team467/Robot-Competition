package frc.robot.drive.motorcontrol.pathtracking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test ability to derive field position from sensor data ")
public class FieldPositionTest {

    private static FieldPosition fieldPosition;

    @BeforeAll
    @DisplayName("Initialize field position for test")
    public static void initAll() {
        fieldPosition = FieldPosition.getInstance();
        fieldPosition.init(0, 0, 90.0, 0.0, 0, 0);
    }

    @Test
    @DisplayName("Test simple forward movement")
    public void positionAfter3FootForwardMovementShouldIncrement3FootOnYAxisTest() {
        double fakeSensorReadingLeft =  3;
        double fakeSensorReadingRight =  3;
        fieldPosition.update(fakeSensorReadingLeft, fakeSensorReadingRight);

        double tolerance = 0.01;
        assertEquals(0.0, fieldPosition.x(), tolerance);
        assertEquals(3.0, fieldPosition.y(), tolerance);
    }

    

}