/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package frc.robot.usercontrol;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.utilities.LookUpTable;
import frc.robot.utilities.MathUtils;

import java.lang.Math;
import java.util.EnumMap;

import org.apache.logging.log4j.Logger;


public class XBoxJoystick467 {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(XBoxJoystick467.class.getName());
  private XboxController xbox;
  private String name;
  private int pov = 0;
  private boolean wasPovLeft;
  private boolean wasPovRight;

  private static final double DEADZONE = 0.1;

  private static final double SENSITIVITY_MODIFIER = 0.6;

  public EnumMap<Button, Boolean> buttonDown = new EnumMap<>(Button.class);
  public EnumMap<Button, Boolean> previousButtonDown = new EnumMap<>(Button.class);

  public EnumMap<Axis, Double> axes = new EnumMap<>(Axis.class);

  public enum Button {
    a(1),
    b(2),
    x(3),
    y(4),
    BumperLeft(5),
    BumperRight(6),
    back(7),
    start(8),
    left(9),
    right(10);

    public final int channel;

    Button(int channel) {
      this.channel = channel;
    }
  }

  /**
   * Check if a specific button is being held down. Ignores first button press, 
   * but the robot loops too quickly for this to matter.
   *
   * @return
   */
  public boolean down(Button b) {
    boolean result = buttonDown.get(b);
    LOGGER.debug("Button: {} = {}", b.name(), result);
    return result;
  }

  /**
   * Check if a specific button has just been pressed. (Ignores holding.)
   *
   * @return
   */
  public boolean pressed(Button b) {
    boolean result = buttonDown.get(b) && !previousButtonDown.get(b);
    LOGGER.debug("Button: {} = {} ", b.name(), result);
    return result;
  }

  /**
   * Check if a specific button has just been released.
   *
   * @return
   */
  public boolean buttonReleased(Button b) {
    return !buttonDown.get(b) && !previousButtonDown.get(b);
  }

  private enum Axis {
    leftX(0),
    leftY(1),
    leftTrigger(2),
    rightTrigger(3),
    rightX(4),
    rightY(5);

    public final int channel;

    Axis(int channel) {
      this.channel = channel;
    }

  }

  /**
   * Implement a dead zone for Joystick centering - and a non-linear acceleration as 
   * the user moves away from the zero position.
   *
   * @param input
   * @return processed input
   */
  private static double accelerateJoystickInput(double input) {
    // Ensure that there is a dead zone around zero
    if (Math.abs(input) < DEADZONE) {
      return 0.0;
    }
    // Simply square the input to provide acceleration
    // ensuring that the sign of the input is preserved
    return (input * Math.abs(input));
  }

  private static double limitSensitivity(double input) {
    return input * SENSITIVITY_MODIFIER;
  }

  /**
   * Create a new joystick on a given channel.
   *
   * @param stick
   */
  public XBoxJoystick467(int stick, String name) {
    // TODO: Set a new joystick on the given channel
    xbox = new XboxController(stick);
    this.name = name;
  }

  /**
   * Returns the raw joystick object inside Joystick467.
   *
   * @return
   */
  public XboxController getJoystick() {
    // TODO: Get the joystick
    return xbox;
  }

  private void readButtons() {
    for (Button b : Button.values()) {
      previousButtonDown.put(b, buttonDown.get(b));
      buttonDown.put(b, xbox.getRawButton(b.channel));
    }
  }

  private void readAxes() {
    for (Axis axis : Axis.values()) {
      axes.put(axis, xbox.getRawAxis(axis.channel));
    }
  }

  /**
   * Read all inputs from the underlying joystick object.
   */
  public void read() {
    readButtons();
    readAxes();
    pov = xbox.getPOV(0);
  }

  public void logIdentity() {
    LOGGER.debug("{} Port: {}", name, xbox.getPort());
  }

  /**
   * Returns the drive speed, taking the turbo and slow triggers into account.
   */
  public double getAdjustedSpeed(double speed) {
    if (getLeftTrigger() > 0.0) {
      // For some reason, up stick is negative, so we flip it
      return turboFastSpeed(speed); 
    } else {
      return turboSlowSpeed(speed); 
    }
  }
  
  public double turboFastSpeed(double speed) {
    // Speed multiplied by acceleration determined by left trigger
    return speed * MathUtils.weightedAverage(
        RobotMap.NORMAL_MAX_SPEED, RobotMap.FAST_MAX_SPEED, getLeftTrigger());
  }

  public double turboSlowSpeed(double speed) {
    // Speed multiplied by deceleration determined by right trigger
    return speed * MathUtils.weightedAverage(
        RobotMap.NORMAL_MAX_SPEED, RobotMap.SLOW_MAX_SPEED, getRightTrigger());
  }

  /**
   * Returns the turn speed, which is slower when the robot is driving fast.
   */
  public double getAdjustedTurnSpeed() {
    return getAdjustedSpeed(getRightStickX()) * MathUtils.weightedAverage(
      RobotMap.NORMAL_TURN_MAX_SPEED, 
      RobotMap.SLOW_TURN_MAX_SPEED, 
      Math.abs(getAdjustedSpeed(-getLeftStickY())));
  }

  public double pov() {
    return pov;
  }

  public boolean povLeft() {
    return pov > 180 && pov < 360;
  }

  public boolean povLeftPressed() {
    boolean isLeft = povLeft();
    boolean isPressed = isLeft && !wasPovLeft;
    wasPovLeft = isLeft;

    return isPressed;
  }

  public boolean povRight() {
    return pov > 0 && pov < 180;
  }

  public boolean povRightPressed() {
    boolean isRight = povRight();
    boolean isPressed = isRight && !wasPovRight;
    wasPovRight = isRight;

    return isPressed;
  }

  /**
   * Calculate the distance of this stick from the center position.
   *
   * @return
   */
  public double getLeftStickDistance() {
    return Math.sqrt((getLeftStickX() * getLeftStickX()) + (getLeftStickY() * getLeftStickY()));
  }

  public double getRightStickDistance() {
    return Math.sqrt((getRightStickX() * getRightStickX()) + (getRightStickY() * getRightStickY()));
  }

  private double calculateStickAngle(double stickX, double stickY) {
    if (stickY == 0.0) {
      // In Y deadzone avoid divide by zero error
      return (stickX > 0.0) ? Math.PI / 2 : (-Math.PI) / 2;
    }

    // Return value in range -PI to PI
    double stickAngle = LookUpTable.getArcTan(stickX / -stickY);

    if (stickY > 0) {
      stickAngle += (stickX > 0) ? Math.PI : -Math.PI;
    }

    return (stickAngle);
  }

  public double getLeftStickY() {
    return axes.get(Axis.leftY);
  }

  public double getLeftStickX() {
    return axes.get(Axis.leftX);
  }

  public double getRightStickY() {
    return axes.get(Axis.rightY);
  }

  public double getRightStickX() {
    return axes.get(Axis.rightX);
  }

  public double getLeftTrigger() {
    return axes.get(Axis.leftTrigger);
  }

  public double getRightTrigger() {
    return axes.get(Axis.rightTrigger);
  }
  
  /**
   * Calculate the angle of this joystick.
   *
   * @return Joystick Angle in range -PI to PI
   */
  public double getLeftStickAngle() {
    return calculateStickAngle(getLeftStickX(), getLeftStickY());
  }

  public double getRightStickAngle() {
    // TODO Repeat for right stick
    return calculateStickAngle(getRightStickX(), getRightStickY());
  }

  public void leftRumble(double value) {
    xbox.setRumble(RumbleType.kLeftRumble, value);
  }

  public void rightRumble(double value) {
    xbox.setRumble(RumbleType.kRightRumble, value);
  }

  public void setRumble(double value) {
    xbox.setRumble(RumbleType.kLeftRumble, value);
    xbox.setRumble(RumbleType.kRightRumble, value);
  }
}
