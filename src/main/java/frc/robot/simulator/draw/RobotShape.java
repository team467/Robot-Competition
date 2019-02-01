package frc.robot.simulator.draw;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.gamepieces.Elevator.Stops;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.communications.*;
import frc.robot.simulator.gui.Coordinate;
import frc.robot.simulator.gui.SimulatedData;

import java.io.File;
import java.text.DecimalFormat;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.apache.logging.log4j.Logger;

public class RobotShape {
  public static boolean RUN_LOCAL = true;
  public static boolean RUN_REPLAY = false;
  public static boolean LOG_REPLAY = false;
  public static File replaySource = new File("");
  public static File loggingPath = new File("");
  int not = 102;
  private Robot robot; // For local processing

  private static final Logger LOGGER = RobotLogManager.getMainLogger(RobotShape.class.getName());
  private DecimalFormat df = new DecimalFormat("####0.00");

  // Robot Shapes
  private Shape robotShape = null;
  private Rectangle chassisShape = null;
  private Rectangle hatchGrabber = null;
  private Rectangle ballGrabber = null;
  private Rectangle rollerShape = null;
  private Line aligner = null;
  private Group robotGroup = new Group();
  private Group turretGroup = new Group();

  private final double buffer = 100;
  // Network Tables
  RobotData data = RobotData.getInstance();
  CSVFile replaySrc = new CSVFile();
  CSVFile replayLog = new CSVFile();

  private Coordinate startingLocation = new Coordinate(0.0, 0.0);
  private double rightDistance;
  private double leftDistance;

  // Internal calculation values
  private double previousRightDistance = 0.0;
  private double previousLeftDistance = 0.0;
  private double heading = 0.0;
  private double absoluteHeading = 0.0;
  private Coordinate currentCoordinate = new Coordinate(0.0, 0.0);
  private Coordinate absoluteCoordinate = new Coordinate(0.0, 0.0);
  private boolean isZeroed = false;
  // Derived coordinates
  private Coordinate left = new Coordinate(-1 * (RobotMap.WHEEL_BASE_WIDTH / 2), 0);
  private Coordinate right = new Coordinate((RobotMap.WHEEL_BASE_WIDTH / 2), 0);
  private double mapHeadingAngle = 0.0;

  private double turretAngle = 0.0;
  private boolean rollerUp = false;
  private boolean hasHatch = false;
  private boolean hasBall = false;
  
  public RobotShape() {
    // Use run local for pure simulation. Remote is for observation of actual robot
    if (RUN_LOCAL | RUN_REPLAY) {
      Robot.enableSimulator();
      robot = new Robot();
      robot.robotInit();
    }
  }

  /**
   * Gets the chassis shape for drawing and other interactions.
   * 
   * @return the robot chassis shape for boundries
   */
  public Shape shape() {
    return robotShape; // The chassis is used for interactions
  }

  /**
   * Returns the group of robot shapes for drawwing.
   * 
   * @return the group of robot shapes
   */
  public Group group() {
    return robotGroup;
  }

  public void init() {
    LOGGER.info("logging working");
    if (RUN_REPLAY) {
      replaySrc.loadFromFile(replaySource);
    }
    if (RUN_LOCAL) {
      switch (SimulatedData.driveMode) {

      case "Teleop":
        robot.teleopInit();
        break;

      case "Autonomous":
        robot.autonomousInit();
        break;

      case "Test":
        robot.testInit();
        break;

      default:
      case "Disabled":
        robot.disabledInit();
      }
    } else if (RUN_REPLAY) {
      robot.disabledInit();
    } else {
      data.startClient();
    }
  }

  /**
   * Creates the robot and layers on the elevator.
   * 
   * @param observableList the list of stuff viewable on the drawn field
   */
  public void createRobotShape(ObservableList<Node> observableList) {

    chassisShape = new Rectangle(RobotMap.BUMPER_LENGTH * 12, RobotMap.BUMPER_WIDTH * 12, Color.LIGHTGRAY);
    chassisShape.relocate(FieldShape.FIELD_OFFSET_Y, FieldShape.FIELD_OFFSET_X);

    rollerShape = new Rectangle(1 * 12, 1 * 12, Color.CORAL);
    rollerShape.relocate(FieldShape.FIELD_OFFSET_Y + RobotMap.BUMPER_LENGTH * 12 - 12,
        FieldShape.FIELD_OFFSET_X + RobotMap.BUMPER_WIDTH * 6 - 6);

    hatchGrabber = new Rectangle(12, 12, Color.LIGHTYELLOW);
    hatchGrabber.relocate(0, 0);

    ballGrabber = new Rectangle(12, 12, Color.CORAL);
    ballGrabber.relocate(0, 12);

    turretGroup.relocate(FieldShape.FIELD_OFFSET_Y, FieldShape.FIELD_OFFSET_X - 12 + RobotMap.BUMPER_WIDTH * 6);

    aligner = new Line(-buffer, -buffer, RobotMap.BUMPER_LENGTH * 12+buffer, RobotMap.BUMPER_WIDTH * 12+buffer);
    aligner.relocate(FieldShape.FIELD_OFFSET_Y-buffer, FieldShape.FIELD_OFFSET_X-buffer);
    
    robotGroup.setBlendMode(BlendMode.SRC_OVER);
    robotGroup.getChildren().add(chassisShape);
    robotGroup.getChildren().add(rollerShape);
      turretGroup.getChildren().add(hatchGrabber);
      turretGroup.getChildren().add(ballGrabber);
    robotGroup.getChildren().add(turretGroup);
    robotGroup.getChildren().add(aligner);
    robotGroup.setVisible(true);
  }

  public void zero() {
    rightDistance = 0;
    leftDistance = 0;
    previousRightDistance = 0;
    previousLeftDistance = 0;
    absoluteHeading += heading;
    heading = 0;
    absoluteCoordinate.x += currentCoordinate.x;
    absoluteCoordinate.y += currentCoordinate.y;
    currentCoordinate.x = 0;
    currentCoordinate.y = 0;
    data.clearZeroed();
  }

  /**
   * Updates the heading and (x, y) position of the robot given the moves of the
   * left and right sides of the robot.
   *
   * @param leftDistance  the distance the left middle wheel moved
   * @param rightDistance the distance the right middle wheel moved
   */
  private void updateMapPosition(double leftDistance, double rightDistance) {
    double radius = (RobotMap.WHEEL_BASE_WIDTH / 2);
    double averageMove = (leftDistance + rightDistance) / 2;
    double leftArcLength = (leftDistance - averageMove);
    double rightArcLength = (rightDistance - averageMove);
    LOGGER.debug("Moves: Left = {} Right = {} Average = {}", df.format(leftArcLength), df.format(rightArcLength),
        averageMove);

    double leftTheta = leftArcLength / radius;
    double rightTheta = rightArcLength / radius;
    double theta = (rightTheta - leftTheta) / 2;
    LOGGER.debug("Thetas: Left = {} Right = {}", df.format(leftTheta), df.format(rightTheta));

    double tempLeftX = radius * Math.cos(theta);
    double tempLeftY = radius * Math.sin(theta);
    double changeInHeading = -1 * Math.atan2(tempLeftY, tempLeftX);
    String logMessage = ("Heading: " + df.format(Math.toDegrees(heading)));
    heading += changeInHeading;
    logMessage += " + " + df.format(Math.toDegrees(changeInHeading)) + " = " + df.format(Math.toDegrees(heading));
    LOGGER.debug(logMessage);

    logMessage = "Position: (" + df.format(currentCoordinate.x) + "," + df.format(currentCoordinate.y) + ") + (";
    mapHeadingAngle = heading + absoluteHeading;
    double addedX = averageMove * Math.sin(mapHeadingAngle);
    double addedY = averageMove * Math.cos(mapHeadingAngle);
    currentCoordinate.x += addedX;
    currentCoordinate.y += addedY;
    logMessage += df.format(addedX) + "," + df.format(addedY) + ") = " + currentCoordinate.x;
    LOGGER.debug(logMessage);

    // X & Y swapped on the screen
    left.x = currentCoordinate.x + absoluteCoordinate.x + -1 * radius * Math.cos(mapHeadingAngle);
    left.y = currentCoordinate.y + absoluteCoordinate.y + radius * Math.sin(mapHeadingAngle);
    right.x = currentCoordinate.x + absoluteCoordinate.x + radius * Math.cos(mapHeadingAngle);
    right.y = currentCoordinate.y + absoluteCoordinate.y + -1 * radius * Math.sin(mapHeadingAngle);
    LOGGER.debug("Screen Postion: [ {}, {}, {}]", df.format(Math.toDegrees(mapHeadingAngle)), left, right);
  }

  public double rightX() {
    return startingLocation.x + right.x;
  }

  public double rightY() {
    return startingLocation.y + right.y;
  }

  public double leftX() {
    return startingLocation.x + left.x;
  }

  public double leftY() {
    return startingLocation.y + left.y;
  }

  public void save() {
    replayLog.writeToFile(loggingPath);
  }

  private void loadData() {
    if (RUN_LOCAL) {

      switch (SimulatedData.driveMode) {

      case "Teleop":
        robot.teleopPeriodic();

        break;

      case "Autonomous":
        robot.autonomousPeriodic();
        break;

      case "Test":
        robot.testPeriodic();
        break;

      default:
      case "Disabled":
        robot.disabledPeriodic();
      }
    } else if (RUN_REPLAY) {
      robot.disabledPeriodic();
      data.load(replaySrc);
    } else {
      data.receive();
    }
    if (LOG_REPLAY) {
      data.flush(replayLog);
    }
    isZeroed = data.isZeroed();
    if (isZeroed) {
      zero();
    }
    previousLeftDistance = leftDistance;
    previousRightDistance = rightDistance;
    leftDistance = data.leftDistance();
    rightDistance = data.rightDistance();
    startingLocation = data.startingLocation();

    LOGGER.info("left: " + (leftDistance) + ", right: " + (rightDistance)+ ", zeroed" + isZeroed);
    updateMapPosition(leftDistance - previousLeftDistance, rightDistance - previousRightDistance);
  }

  private void updateShape() {
    turretGroup.setRotate(Math.toDegrees(turretAngle));
    // if true if false
    ballGrabber.setFill(hasBall ? Color.ORANGE : Color.CORAL);
    hatchGrabber.setFill(hasHatch ? Color.YELLOW : Color.LIGHTYELLOW);
    rollerShape.setFill(rollerUp ? Color.ORANGE : Color.CORAL);
  }

  /**
   * Loads the data and draws the robot.
   */
  public void draw() {

    loadData();
    updateShape();

    double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
    double x = radius * Math.cos(mapHeadingAngle);
    double y = -radius * Math.sin(mapHeadingAngle);
    turretAngle+=0.1;
    robotGroup.setRotate(Math.toDegrees(mapHeadingAngle));
    robotGroup.relocate((FieldShape.FIELD_OFFSET_Y + (leftY() + y) * 12-buffer),
        (FieldShape.FIELD_OFFSET_X + (leftX() + x) * 12-buffer));

  }

}
