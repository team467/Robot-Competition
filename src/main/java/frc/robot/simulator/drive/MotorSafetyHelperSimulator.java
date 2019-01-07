package frc.robot.simulator.drive;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.RobotState;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The MotorSafetyHelper object is constructed for every object that wants to implement the Motor
 * Safety protocol. This simulatator gives the expected responses so that the system thinks the 
 * simulated motors are responding.
 */
public final class MotorSafetyHelperSimulator {
  private double expiration;
  private boolean enabled;
  private double stopTime;
  private final Object thisMutex = new Object();
  private final MotorSafety safeObject;
  private static final Set<MotorSafetyHelperSimulator> helperList = new LinkedHashSet<>();
  private static final Object listMutex = new Object();

  /**
   * The constructor for a MotorSafetyHelper object. The helper object is constructed for every
   * object that wants to implement the Motor Safety protocol. The helper object has the code to
   * actually do the timing and call the simulated motors Stop() method when the timeout expires. 
   * The motor object is expected to call the Feed() method whenever the motors value is updated.
   *
   * @param safeObject a pointer to the motor object implementing MotorSafety. This is used to call
   *                   the Stop() method on the motor.
   */
  public MotorSafetyHelperSimulator(MotorSafety safeObject) {
    this.safeObject = safeObject;
    enabled = false;
    //expiration = MotorSafety.DEFAULT_SAFETY_EXPIRATION;
    stopTime = time();

    synchronized (listMutex) {
      helperList.add(this);
    }
  }

  private double time() {
    return (double) (System.currentTimeMillis() / 1000);
  }

  /**
   * Feed the motor safety object. Resets the timer on this object that is used to do the timeouts.
   */
  public void feed() {
    synchronized (thisMutex) {
      stopTime = time() + expiration;
    }
  }

  /**
   * Set the expiration time for the corresponding motor safety object.
   *
   * @param expirationTime The timeout value in seconds.
   */
  public void setExpiration(double expirationTime) {
    synchronized (thisMutex) {
      expiration = expirationTime;
    }
  }

  /**
   * Retrieve the timeout value for the corresponding motor safety object.
   *
   * @return the timeout value in seconds.
   */
  public double getExpiration() {
    synchronized (thisMutex) {
      return expiration;
    }
  }

  /**
   * Determine of the motor is still operating or has timed out.
   *
   * @return a true value if the motor is still operating normally and hasn't timed out.
   */
  public boolean isAlive() {
    synchronized (thisMutex) {
      return !enabled || stopTime > time();
    }
  }

  /**
   * Check if this motor has exceeded its timeout. This method is called periodically to determine
   * if this motor has exceeded its timeout value. If it has, the stop method is called, and the
   * motor is shut down until its value is updated again.
   */
  public void check() {
    boolean enabled;
    double stopTime;

    synchronized (thisMutex) {
      enabled = this.enabled;
      stopTime = this.stopTime;
    }

    if (!enabled || RobotState.isDisabled() || RobotState.isTest()) {
      return;
    }

    if (stopTime < time()) {
      DriverStation.reportError(safeObject.getDescription() + "... Output not updated often "
          + "enough.", false);

      safeObject.stopMotor();
    }
  }

  /**
   * Enable/disable motor safety for this device Turn on and off the motor safety option for this
   * PWM object.
   *
   * @param enabled True if motor safety is enforced for this object
   */
  public void setSafetyEnabled(boolean enabled) {
    synchronized (thisMutex) {
      this.enabled = enabled;
    }
  }

  /**
   * Return the state of the motor safety enabled flag Return if the motor safety is currently
   * enabled for this device.
   *
   * @return True if motor safety is enforced for this device
   */
  public boolean isSafetyEnabled() {
    synchronized (thisMutex) {
      return enabled;
    }
  }

  /**
   * Check the motors to see if any have timed out. This static method is called periodically to
   * poll all the motors and stop any that have timed out.
   */
  public static void checkMotors() {
    synchronized (listMutex) {
      for (MotorSafetyHelperSimulator elem : helperList) {
        elem.check();
      }
    }
  }

}