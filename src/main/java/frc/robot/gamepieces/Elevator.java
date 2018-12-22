package frc.robot.gamepieces;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.usercontrol.DriverStation467;

import org.apache.logging.log4j.Logger;

public class Elevator {
  private static Elevator instance;
  
  private static final Logger LOGGER = RobotLogManager.getMainLogger(Elevator.class.getName());

  private WpiTalonSrxInterface heightController;

  private Stops targetHeight;
  private int previousHeight;

  private static final int ALLOWABLE_ERROR_TICKS = 3;
  private static final int LIMIT_BUFFER = 10;

  public enum Stops {
    // null if no stop is desired
    // height values measured empirically
    basement(RobotMap.ELEVATOR_BOTTOM),
    floor(RobotMap.ELEVATOR_FLOOR),
    fieldSwitch(RobotMap.ELEVATOR_SWITCH),
    lowScale(RobotMap.ELEVATOR_LOW_SCALE),
    highScale(RobotMap.ELEVATOR_TOP);

    /**
     * Height in sensor units.
     */
    public int height;

    Stops(double heightProportion) {
      height = heightTicksFromProportion(heightProportion);
    }
  }

  /**
   * 0.0 is the bottom, 1.0 is the top
   */
  private static int heightTicksFromProportion(double proportion) {
    return (int)((1.0 - proportion) * RobotMap.ELEVATOR_BOTTOM_TICKS 
        + proportion * RobotMap.ELEVATOR_TOP_TICKS);
  }

  /**
   * Get the single instance of the Elevator object.
   */
  public static Elevator getInstance() {
    if (instance == null) {
      instance = new Elevator(TalonProxy.create(RobotMap.ELEVATOR_MOTOR_CHANNEL));
    }
    return instance;
  }

  private Elevator(WpiTalonSrxInterface heightController) {
    if (!RobotMap.HAS_ELEVATOR || RobotMap.useSimulator) {
      return;
    }

    this.heightController = heightController;
    configMotorParameters();

    targetHeight = null;
  }

  public void configMotorParameters() {
    // Configure talon to be able to use the analog sensor.
    if (!RobotMap.useSimulator) {
      heightController.configSelectedFeedbackSensor(
          FeedbackDevice.Analog, 0, RobotMap.TALON_TIMEOUT);
      heightController.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0x00);
      heightController.setInverted(false);
      heightController.setSensorPhase(false);

      heightController.config_kP(0, 40.0, RobotMap.TALON_TIMEOUT);
      heightController.config_kI(0, 0.0, RobotMap.TALON_TIMEOUT);
      heightController.config_kD(0, 5.0, RobotMap.TALON_TIMEOUT);
      heightController.config_kF(0, 0.0, RobotMap.TALON_TIMEOUT);

      heightController.configAllowableClosedloopError(
          0, ALLOWABLE_ERROR_TICKS, RobotMap.TALON_TIMEOUT);
    }
  }

  private int getRawHeight() {
    if (!RobotMap.HAS_ELEVATOR || RobotMap.useSimulator) {
      return RobotMap.ELEVATOR_BOTTOM_TICKS;
    }
    return heightController.getSelectedSensorPosition(0);
  }

  public void moveToHeight(Stops target) {
    // targetHeight member variable used in periodic function to reach the height
    targetHeight = target;
    if (!RobotMap.useSimulator) {
      SmartDashboard.putString("Elevator/Most Recent Target", target.toString());
    }
  }

  private void automaticMove() {
    if (!RobotMap.HAS_ELEVATOR) {
      return;
    }

    // If we're in position, stop.
    final int error = targetHeight.height - heightController.getSelectedSensorPosition(0);
    if (!RobotMap.useSimulator) {
      if ((heightController.getControlMode() == ControlMode.MotionMagic 
          || heightController.getControlMode() == ControlMode.Position)
          && Math.abs(error) <= ALLOWABLE_ERROR_TICKS) {
        LOGGER.debug("automaticMove, clearing target,  trajectory = {} pos = {} err = {}", 
            targetHeight.height, heightController.getSelectedSensorPosition(0), error);
        targetHeight = null;
        heightController.disable();
        return;
      }
    }

    configMotorParameters();
    LOGGER.debug("Moving to height= {}", targetHeight.height);

    heightController.set(ControlMode.Position, targetHeight.height);
  }

  /**
   * Moves based on the Xbox controller analog input
   * 
   * @param speed The velocity. Shall be a value between -1 and 1.
   */
  public void move(double speed) {
    previousHeight = getRawHeight();
    SmartDashboard.putNumber("Elevator/Move Speed", speed);

    if (!RobotMap.HAS_ELEVATOR || RobotMap.useSimulator) {
      return;
    }

    // TODO Re-enable and test to see if it works
    // rumbleOnPresetHeights();

    if (Math.abs(speed) >= RobotMap.MIN_LIFT_SPEED) {
      // The controller is asking for elevator movement, cancel preset target and move.
      targetHeight = null;
      heightController.set(ControlMode.PercentOutput, speed);
    } else if (targetHeight != null) {
      // There is a target preset position, move there.
      automaticMove();
    } else {
      // Nothing to do, make sure we're not moving.
      heightController.disable();
    }

    limitCheck();
    telemetry();
  }

  public int getHeight() {
    return previousHeight;
  }

  public void rumbleOnPresetHeights() {
    double currentHeight = getRawHeight();

    for (Stops stop : Stops.values()) {
      if ((previousHeight < stop.height && currentHeight >= stop.height)
          || (previousHeight > stop.height && currentHeight <= stop.height)) {
        DriverStation467.getInstance().getNavRumbler().rumble(200, 0.8);
      }
    }
  }

  /**
   * Look for sensor slippage.
   */
  public void limitCheck() {
    if (!RobotMap.useSimulator) {
      final int position = heightController.getSelectedSensorPosition(0);
      if (position > RobotMap.ELEVATOR_BOTTOM_TICKS + LIMIT_BUFFER
          || position < RobotMap.ELEVATOR_TOP_TICKS - LIMIT_BUFFER) {
        LOGGER.info("HEIGHT SENSOR OUT OF EXPECTED RANGE ("
            + RobotMap.ELEVATOR_TOP_TICKS + " - "
            + RobotMap.ELEVATOR_BOTTOM_TICKS + "), found " + position);
      }
    }
  }

  public void telemetry() {
    SmartDashboard.putString("Elevator/Control Mode", 
        heightController.getControlMode().name());
    SmartDashboard.putNumber("Elevator/Closed Loop Error", 
        heightController.getClosedLoopError(0));
    SmartDashboard.putNumber("Elevator/Current Ticks", 
        heightController.getSelectedSensorPosition(0));
    SmartDashboard.putNumber("Elevator/Target Ticks", 
        targetHeight != null ? targetHeight.height : -1);
  }

}