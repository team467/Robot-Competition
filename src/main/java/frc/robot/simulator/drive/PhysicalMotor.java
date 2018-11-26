package frc.robot.simulator.drive;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.RobotMap;

public class PhysicalMotor implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(PhysicalMotor.class);
  
  private static final DecimalFormat df = new DecimalFormat("#0.0000");

  static ArrayList<PhysicalMotor> motors = new ArrayList<PhysicalMotor>();
  static ArrayList<Thread> threads = new ArrayList<Thread>();

  // Simulation Information
  public static final double MAX_RPM = 100000000;//821;


  public static final int MOTOR_TIME_SLICE_IN_MS = 10;

  // Iteration period is 20 ms
  public static final double ROBOT_TIME_SLICE_IN_MS = 20.0;

  private static final double MAX_SPEED_PER_PERIOD = MAX_RPM
  / (60.0 * 60.0 * 1000.0 / MOTOR_TIME_SLICE_IN_MS)
  * (RobotMap.WHEEL_CIRCUMFERENCE / 12);

  private static final double ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL = 2;

  // Divide the fastest speed by the number of time slices to get to full speed
  private static final double FASTEST_RAMP_PER_PERIOD = MAX_SPEED_PER_PERIOD 
    / (ROBOT_FASTEST_RAMP_RATE_IN_SECONDS_TO_FULL 
    * 1000.0 / MOTOR_TIME_SLICE_IN_MS);

  public static double ROBOT_MAX_THEORETICAL_VOLTAGE = 12.0;

  private static double ROBOT_MAX_POWER_IN_WATTS = 480.0;

  // TODO: Move to battery simulator. Shoud eventually measure current and reduce
  private static double batteryVoltage = 12.0;

  private boolean enabled;

  private double voltage;

  private double targetSpeed;

  private double speed;

  // Sensor stuff
  private double revolutions = 0.0; // keep track of partial ticks
  private static final int NUM_SENSORS = 2;
  private int[] sensors = new int[NUM_SENSORS];
  private int selectedSensor = 0;

  private int id = 0;

  static PhysicalMotor createMotor(int id) {
//    System.err.println("Creating motor ID: " + id);
    PhysicalMotor motor = new PhysicalMotor(id);
    motors.add(motor);

    Thread motorThread = new Thread(motor);
    motorThread.start();
    threads.add(motorThread);

    return motor;
  }

  PhysicalMotor(int id) {
    this.id = id;
    voltage = 0.0;
    speed = 0.0;
    targetSpeed = 0.0;
    for (int i = 0; i < NUM_SENSORS; i++) {
      sensors[i] = 0;
    }
    revolutions = 0.0;
    enabled = true;
  }

  static void enableMotors() {
    for (PhysicalMotor motor : motors) {
      motor.enabled = true;
    }
    for (Thread thread : threads) {
      thread.start();
    }
  }

  static void shutdown() {
    for (PhysicalMotor motor : motors) {
      // Need to disable the motors for their threads to exit.
      motor.enabled = false;
    }

    for (Thread motorThread : threads) {
      try {
        // Joins are the best way to shutdown threads as they wait for locks to be freed on their own.
        motorThread.join();
      } catch (InterruptedException e) {
        LOGGER.error("SIMULATOR|DRIVE", e);
        e.printStackTrace();
      }
    }

    motors.clear();
    threads.clear();
  }
  
  /**
   * Sets the voltage level of the motor. 
   * 
   * @param voltage the output level of the motor
   * @return the motor for chaining commands
   */
  PhysicalMotor voltage(double voltage) {
    if (voltage < batteryVoltage) {
      this.voltage = voltage;
    } else {
      voltage = batteryVoltage;
    }
    targetSpeed = (voltage / ROBOT_MAX_THEORETICAL_VOLTAGE) * MAX_SPEED_PER_PERIOD;
    // System.err.println("Set voltage: " + voltage
    //     + " ROBOT_MAX_THEORETICAL_VOLTAGE: " + ROBOT_MAX_THEORETICAL_VOLTAGE
    //     + " MAX_SPEED_PER_PERIOD: " + MAX_SPEED_PER_PERIOD 
    //     + " target speed = " + targetSpeed);
    return this;
  }

  /**
   * Gets the current voltage setting of the motor.
   * 
   * @return the power level in voltage
   */
  double voltage() {
    return voltage;
  }

  /**
   * Returns the speed of the motor and attached simulated wheels or equipment.
   * Normally this should be attached to a sensor simulator for feedback.
   * 
   * @return the speed of the motor. Assumes ungeared.
   */
  double speed() {
    return speed;
  }

  int sensorPosition(int sensorIndex) {
    return sensors[sensorIndex];
  }

  /**
   * Simulates the motor, including current speed.
   */
  @Override
  public void run() {
//    System.err.println("Running thread.");
    while(enabled) {
      try {
        double speedDiffFromTarget = targetSpeed - speed;

        // System.err.printf("Target Speed: %s Current Speed: %s Diff: %s\n", 
        // df.format(targetSpeed), df.format(speed), df.format(speedDiffFromTarget));
        LOGGER.debug("SIMULATOR|DRIVE", "Target Speed: {} Current Speed: {} Diff: {}", 
            df.format(targetSpeed), df.format(speed), df.format(speedDiffFromTarget));
        
        if (Math.abs(speedDiffFromTarget) > FASTEST_RAMP_PER_PERIOD) {
          speed += Math.signum(speedDiffFromTarget) * FASTEST_RAMP_PER_PERIOD;
        } else {
          speed = targetSpeed;
        }

        // Speed is in rotations, as are sensor ticks
        revolutions += (speed / (double) RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION);
        sensors[selectedSensor] = (int) revolutions; // truncation is OK
//        System.err.println("Analog revs: " + revolutions + " digital ticks: " + sensors[selectedSensor]);

        // Make sure it's not in the middle of a change call
        synchronized(this) {
          Thread.sleep(MOTOR_TIME_SLICE_IN_MS);
        }
      } catch (InterruptedException e) {
        LOGGER.error("SIMULATOR|DRIVE", e);
        e.printStackTrace();
      }
    }
  }

}