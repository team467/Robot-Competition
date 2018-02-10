package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Rumbler;

import edu.wpi.first.wpilibj.DriverStation;

public class MatchConfiguration {

	private static MatchConfiguration instance;
	
	private static final Logger LOGGER = Logger.getLogger(MatchConfiguration.class);
	
	public enum Side {
		LEFT,
		RIGHT;
	}
	
	public enum StartPosition {
		LEFT,
		CENTER,
		RIGHT;
	}
	
	Side blueSwitch;
	
	public void temp() {
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	            if(gameData.length() > 0)
	            {
		  if(gameData.charAt(0) == 'L')
		  {
			//Put left auto code here
		  } else {
			//Put right auto code here
		  }
	            }
	}
	public static MatchConfiguration getInstance() {
		if (instance == null) {
			instance = new MatchConfiguration();
		}
		return instance;
	}
	
	private MatchConfiguration() {
		LOGGER.setLevel(Level.INFO);
	}
	
	
	
}
