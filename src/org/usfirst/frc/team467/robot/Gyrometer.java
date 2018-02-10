package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.imu.ADIS16448_IMU;
import org.usfirst.frc.team467.robot.imu.IMU;
import org.usfirst.frc.team467.robot.imu.LSM9DS1_IMU;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.GyroBase;

/*
 *  Simple wrapper class around a gyro. This is implemented as a singleton
 */
public class Gyrometer extends GyroBase implements Gyro {
	private IMU imu = null;
	private static Gyrometer instance;
	private double measuresPerDegree;

	/*
	 * private constructor (singleton pattern)
	 */
	private Gyrometer() {
		imu = new ADIS16448_IMU();
			measuresPerDegree = 4;
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
		if (RobotMap.robotID == RobotMap.RobotID.PreseasonBot) {
			return Math.toRadians(imu.getAngleZ());
		} else if (RobotMap.robotID == RobotMap.RobotID.Competition_1){
			return Math.toRadians(-imu.getAngleX());
		} else {
			return 0;
		} 
	}
	/**
	 * Returns the angle of the robot orientation in Degrees. Robot is assumed to be pointing forward at 0.0. Clockwise rotation is
	 * positive, counter clockwise rotation is negative
	 *
	 * @return the robot angle	 */
	public double getYawDegrees() {
		return Math.toDegrees(getYawRadians());
	}

	/**
	 * Returns the X angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
	public double getRollRadians() {
		if (RobotMap.robotID == RobotMap.RobotID.PreseasonBot) {
			return Math.toRadians(imu.getAngleX());
		} else if (RobotMap.robotID == RobotMap.RobotID.Competition_1){
			return Math.toRadians(-imu.getAngleY());
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
		if (RobotMap.robotID == RobotMap.RobotID.PreseasonBot) {
			return Math.toRadians(imu.getAngleY());
		} else if (RobotMap.robotID == RobotMap.RobotID.Competition_1){
			return Math.toRadians(-imu.getAngleZ());
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
		// TODO Auto-generated method stub
		return 0;
	}

}