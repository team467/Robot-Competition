package frc.robot.drive.motorcontrol.pathtracking;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.utilities.Utils;

import frc.robot.RobotMap;

public class FieldPosition {

    	// Single instance of this class
	private static FieldPosition instance = null;

    private static double RADIUS = RobotMap.WHEEL_BASE_WIDTH / 2;

    private static DecimalFormat df = new DecimalFormat("#0.0");

    Logger LOGGER = LogManager.getLogger(FieldPosition.class);

    // State variables
    private double x = 0.0;
    public double x() { return x; }
    private double y = 0.0;
    public double y() { return x; }
    private double heading = 0.0;
    public double heading() { return heading; }
    private double changeInHeading = 0.0;
    public double changeInHeading() { return changeInHeading; }
    private double velocity = 0.0;
    public double velocity() { return velocity; }
    private double accelleration = 0.0;
    public double accelleration() { return accelleration; }
    private long timestamp;
    public double timestamp() { return (timestamp - baseTime) / 1000; }

    private long baseTime;
    private double leftX = 0.0;
    private double leftY = 0.0;
    private double rightX = 0.0;
    private double rightY = 0.0;
    private double leftWheelPosition = Math.PI / 2.0;
    private double rightWheelPosition = -Math.PI / 2.0;

    private double lastLeftSensorReading = 0.0;

    private double lastRightSensorReading = 0.0;

    public static FieldPosition getInstance() {
        if (instance == null) {
            instance = new FieldPosition();
        }
        return instance;
    }

    public FieldPosition() {
        heading = Math.toRadians(90.0);
        changeInHeading = 0.0;
        x = 0.0;
        y = 0.0;
        leftX = x + RADIUS * Math.cos(heading + leftWheelPosition);
        leftY = y + RADIUS * Math.sin(heading + leftWheelPosition);
        rightX = x + RADIUS * Math.cos(heading + rightWheelPosition);
        rightY = y + RADIUS * Math.sin(heading + rightWheelPosition);
        velocity = 0.0;
        accelleration = 0.0;
        lastLeftSensorReading = 0.0;
        lastRightSensorReading = 0.0;
        baseTime = timestamp = System.currentTimeMillis();
    }

    public void init(
        double initialXPosition, 
        double initialYPosition, 
        double initialHeading, 
        double initialVelocity, 
        double leftSensorReading, 
        double rightSensorReading) {
        heading = Math.toRadians(initialHeading);
        x = initialXPosition;
        y = initialYPosition;
        leftX = x + RADIUS * Math.cos(heading + leftWheelPosition);
        leftY = y + RADIUS * Math.sin(heading + leftWheelPosition);
        rightX = x + RADIUS * Math.cos(heading + rightWheelPosition);
        rightY = y + RADIUS * Math.sin(heading + rightWheelPosition);
        velocity = initialVelocity;
        accelleration = 0.0;
        lastLeftSensorReading = leftSensorReading;
        lastRightSensorReading = rightSensorReading;
        baseTime = timestamp = System.currentTimeMillis();
    }

    public void zeroSensors() {
        this.lastLeftSensorReading = 0.0;
        this.lastRightSensorReading = 0.0;
    }

    public void update(double leftSensorReading, double rightSensorReading) {
        long currentTime = System.currentTimeMillis();
        double period = (currentTime - timestamp);

        double leftMove = (leftSensorReading - lastLeftSensorReading);
        double rightMove = (rightSensorReading - lastRightSensorReading);
        changeInHeading = Utils.normalizeAngle((rightMove - leftMove) / (2 * RADIUS));
        // System.out.println("Change in Heading: " + df.format(Math.toDegrees(changeInHeading)));
        // System.out.println("Original Position: (" + df.format(leftX) + ", " + df.format(leftY) + "), (" + df.format(rightX) + ", " + df.format(rightY) + ")" );

        double currentVelocity = (leftMove + rightMove) / (2*period); // inches per ms
        accelleration = (currentVelocity - velocity) / period;
        // System.out.println("Previous Velocity: " + df.format(1000/12*velocity) + " Velocity: " + df.format(1000/12*currentVelocity) + " Accelleration: " + df.format(1000/12*accelleration) + " period " + period);

        if (changeInHeading == 0) {
            leftX = leftX - leftMove * Math.cos(heading + leftWheelPosition);
            leftY = leftY - leftMove * Math.sin(heading + leftWheelPosition);
            rightX = rightX + leftMove * Math.cos(heading + rightWheelPosition);
            rightY = rightY + leftMove * Math.sin(heading + rightWheelPosition);
        } else {
            double newHeading = heading + changeInHeading;
            double turnRadius = leftMove / changeInHeading;
            double centerX = leftX + Math.cos(heading + leftWheelPosition) * turnRadius;
            double centerY = leftY + Math.sin(heading + leftWheelPosition) * turnRadius;;
            leftX = centerX - turnRadius * Math.cos(newHeading + leftWheelPosition);
            leftY = centerY - turnRadius * Math.sin(newHeading + leftWheelPosition);
            // System.out.println("Left Turn Circle: center = (" + df.format(centerX) + ", " + df.format(centerY) + ") radius = " + df.format(turnRadius));
    
            turnRadius = rightMove / changeInHeading;
            centerX = rightX - Math.cos(heading + rightWheelPosition) * turnRadius;
            centerY = rightY - Math.sin(heading + rightWheelPosition) * turnRadius;;
            rightX = centerX + turnRadius * Math.cos(newHeading + rightWheelPosition);
            rightY = centerY + turnRadius * Math.sin(newHeading + rightWheelPosition);
            // System.out.println("Right Turn Circle: center = (" + df.format(centerX) + ", " + df.format(centerY) + ") radius = " + df.format(turnRadius));
            // System.out.println("New Position: (" + df.format(leftX) + ", " + df.format(leftY) + "), (" + df.format(rightX) + ", " + df.format(rightY) + ")" );

            heading = newHeading;
        }
        // double width = Math.sqrt(Math.pow((rightX - leftX), 2) + Math.pow((rightY - leftY), 2));
        // System.out.println("Width : " + df.format(width));
        // System.out.println();

        lastLeftSensorReading = leftSensorReading;
        lastRightSensorReading = rightSensorReading;
        velocity = currentVelocity;
        timestamp = currentTime;
        x = (leftX + rightX) / 2;
        y = (leftY + rightY) / 2;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("State at time ");
        output.append(df.format((timestamp - baseTime)/1000));
        output.append("\nPosition (feet): (");
        output.append(df.format(x));
        output.append(", ");
        output.append(df.format(y));
        output.append(")\nHeading (degrees): ");
        output.append(df.format(Math.toDegrees(heading)));
        output.append("\nVelocity (ft/s): ");
        output.append(df.format(1000*velocity));
        output.append("\nAcceleration (ft/s2): ");
        output.append(df.format(1000*1000*accelleration));
        return output.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        FieldPosition position = FieldPosition.getInstance();
        position.init(0, 0, 90.0, 0.0, 0, 0);
        double leftPosition =  Math.PI;
        double rightPosition =  Math.PI/2;
        Thread.sleep(20);
        position.update(leftPosition, rightPosition);
        leftPosition +=  Math.PI/2;
        rightPosition +=  Math.PI;
        Thread.sleep(20);
        position.update(leftPosition, rightPosition);
        System.out.println(position);
    }

}