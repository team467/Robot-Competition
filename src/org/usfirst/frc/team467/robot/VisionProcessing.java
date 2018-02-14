package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionProcessing {
	private static final Logger LOGGER = Logger.getLogger(VisionProcessing.class);
	private static VisionProcessing instance;

	private NetworkTable table;
	private Gyrometer gyro;

	private double targetAngle, x, y, distance, width, height = 0.0;
	private boolean seeTwo = false;

	private VisionProcessing() {
		table = NetworkTable.getTable("Vision Table");
		table.putNumber("CamToCenterWidth", RobotMap.CamToCenterWidthInches);
		table.putNumber("CamToCenterLength", RobotMap.CamToCenterLengthInches);
		gyro = Gyrometer.getInstance();
		update();
	}

	public static VisionProcessing getInstance() {
		if (instance == null) {
			instance = new VisionProcessing();
		}
		return instance;
	}

	public void update() {
		seeTwo = table.getBoolean("seeTwo", false);
		targetAngle = table.getNumber("angle", 0.0);
		x = table.getNumber("x", 0.0);
		y = table.getNumber("y", 0.0);
		distance = table.getNumber("distance", 0.0);
		width = table.getNumber("w", 0.0);
		height = table.getNumber("h", 0.0);
		table.putNumber("gyro", gyro.getYawDegrees());

		// LOGGER.debug("Can see two contours = " + String.valueOf(seeTwo));
	}

	public double getTargetAngle() {
		return targetAngle;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getDistance() {
		return distance;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public boolean canSeeTwo() {
		return seeTwo;
	}

	public void logDistanceValues() {
		LOGGER.info("distance," + getHeight() + "," + 3580 / getHeight() + "," + getHeight() * getWidth());
	}

	@Override
	public String toString() {
		return "VisionProcessing [targetAngle=" + targetAngle + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height
				+ ", seeTwo=" + seeTwo + "]";
	}
}
