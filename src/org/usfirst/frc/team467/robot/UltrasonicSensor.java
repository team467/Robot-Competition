package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.DigitalInput;

public class UltrasonicSensor {
	Ultrasonic us;
	static UltrasonicSensor instance;
	
	
	private static int pingChannel = 2;
	private static int echoChannel = 1;
	
	private UltrasonicSensor() {
		us = new Ultrasonic(pingChannel, echoChannel);
	}
	
	public void init() {
		us.setAutomaticMode(true);
	}
	
	public static UltrasonicSensor getInstance() {
		if(instance == null) {
			instance = new UltrasonicSensor();
		}
		
		return instance;
	}
	
	public double getDistance() {
		return us.getRangeInches();
	}

}
