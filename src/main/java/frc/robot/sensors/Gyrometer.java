package frc.robot.sensors;

import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.robot.RobotMap;
import frc.robot.sensors.imu.Imu;

import com.analog.adis16448.frc.ADIS16448_IMU;

/*
 *  Simple wrapper class around a gyro. This is implemented as a singleton
 */
public class Gyrometer extends GyroBase implements Gyro {

  private ADIS16448_IMU imu = null;

  private static Gyrometer instance;

  /*
   * private constructor (singleton pattern)
   */
  private Gyrometer() {
    imu = new ADIS16448_IMU(); //TODO: gyro should not be null but we need to figure out the ADIS IMU before it goes into the code.
  }

  /**
   * Returns a single instance of the gyro object.
   */
  public static Gyrometer getInstance() {
    if (instance == null) {
      instance = new Gyrometer();
    }
    return instance;
  }

  /*
   * Reset gyro
   */
  public void reset() {
    imu.reset();
  }
  
  public void zero() {
    reset();
  }

  /*
   * Calibrate gyro
   */
  public void calibrate() {
    imu.calibrate();
  }

  /**
   * Returns the Z angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
   *
   * @return the gyro angle
   */
  public double getYawRadians() {
    if (RobotMap.HAS_GYRO) {
      return Math.toRadians(-imu.getGyroAngleX());
    } else {
      return 0;
    } 
  }

  /**
   * Returns the angle of the robot orientation in Degrees. Robot is assumed to be pointing 
   * forward at 0.0. Clockwise rotation is positive, counter clockwise rotation is negative.
   *
   * @return the robot angle
   */
  public double getYawDegrees() {
    return Math.toDegrees(getYawRadians());
  }

  /**
   * Returns the X angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
   *
   * @return the gyro angle
   */
  public double getRollRadians() {
    if (RobotMap.HAS_GYRO) {
      return Math.toRadians(-imu.getGyroAngleY());
    } else {
      return 0;
    } 
  }

  /**
   * Returns the X angle of the gyro in Degrees. Note, the IMU returns 1440 degrees per rotation.
   *
   * @return the gyro angle
   */
  public double getRollDegrees() {
    return Math.toDegrees(getRollRadians());
  }

  /**
   * Returns the Y angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
   *
   * @return the gyro angle
   */
  public double getPitchRadians() {
    if (RobotMap.HAS_GYRO) {
      return Math.toRadians(-imu.getGyroAngleZ());
    } else {
      return 0;
    } 
  }

  /**
   * Returns the Y angle of the gyro in Degrees. Note, the IMU returns 1440 degrees per rotation.
   *
   * @return the gyro angle
   */
  public double getPitchDegrees() {
    return Math.toDegrees(getPitchRadians());
  }

  @Override
  public double getAngle() {
    return getYawDegrees();
  }

  @Override
  public double getRate() {
    return imu.getRate();
  }

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub

  }
}