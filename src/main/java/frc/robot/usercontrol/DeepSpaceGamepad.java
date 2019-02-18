package frc.robot.usercontrol;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.buttons.Button;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;


public class DeepSpaceGamepad {
  private Joystick gamepad;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(DeepSpaceGamepad.class.getName());

  private boolean buttonA;
  private boolean buttonB;
  private boolean buttonC;
  private boolean toggle;
  private double turretoffset = 0.0;
  private double turretKnob;
  private static final double KNOB_RANGE = 315.0;
  private static final double TURRET_RANGE = 270.0; //Put this in robot map
  private static DeepSpaceGamepad instance = null;

  public DeepSpaceGamepad() {
    gamepad = new Joystick(0);
    buttonA = false;
    buttonB = false;
    buttonC = false;
    toggle = false;
    turretKnob = 0.0;
  }

  public static DeepSpaceGamepad getInstance() {
    if (instance == null) {
      instance = new DeepSpaceGamepad();
    }
    return instance;
  }

  public void read() {
    buttonA = gamepad.getRawButton(12);
    buttonB = gamepad.getRawButton(13);
    buttonC = gamepad.getRawButton(14);
    toggle = gamepad.getRawButton(15);

    turretKnob = gamepad.getRawAxis(0);

    if (toggle) {
      gamepad.setOutput(5, true);
      gamepad.setOutput(6, true);
    }
    else {
      gamepad.setOutput(5, false);
      gamepad.setOutput(6, false);
    }
  }

  public void initKnob() {
    turretoffset = gamepad.getRawAxis(0);
  }

  public double getDegrees() {
    double angle = RobotMap.NAV_TICK_MULTIPLIER * (turretKnob - turretoffset);
    LOGGER.debug("Turret knob: {}", turretKnob - turretoffset);
    return (RobotMap.NAV_TURRET_INVERTED)? -angle : angle; 
  }

  public boolean getButtonA() {
    return buttonA;
  }

  public boolean getButtonB() {
    return buttonB;
  }

  public boolean getButtonC() {
    return buttonC;
  }
}
