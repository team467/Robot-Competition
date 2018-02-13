package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DriverStation;

public class MatchConfiguration {

	private static MatchConfiguration instance;

	private static final Logger LOGGER = Logger.getLogger(MatchConfiguration.class);

	public enum TeamColor {
		UNKNOWN,
		RED,
		BLUE;
	}

	public enum Side {
		UNKNOWN,
		LEFT,
		RIGHT;
	}

	public enum StartPosition {
		UNKNOWN,
		LEFT,
		CENTER,
		RIGHT;
	}

	private TeamColor teamColor;

	private Side redSwitch;

	private Side blueSwitch;

	private Side scale;

	private StartPosition startPosition;

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
		teamColor = TeamColor.UNKNOWN;
		redSwitch = Side.UNKNOWN;
		blueSwitch = Side.UNKNOWN;
		scale = Side.UNKNOWN;
		startPosition = StartPosition.UNKNOWN;
	}

	public TeamColor teamColor() {
		return teamColor;
	}

	public Side redSwitch() {
		return redSwitch;
	}

	public Side blueSwitch() {
		return blueSwitch;
	}

	public Side scale() {
		return scale;
	}

	public StartPosition startPosition() {
		return startPosition;
	}

	public boolean isSwitchOnSameSide() {
		boolean isOnSameSide = false;
		if (teamColor == TeamColor.BLUE) {
			if ((blueSwitch == Side.LEFT && startPosition == StartPosition.LEFT) || (blueSwitch == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
				isOnSameSide = true;
			}
		} else {
			if (teamColor == TeamColor.RED) {
				if (redSwitch == Side.LEFT && startPosition == StartPosition.LEFT || (redSwitch == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
					isOnSameSide = true;
				}
			}
		}
		return isOnSameSide;
	}

	public boolean isScaleOnSameSide() {
		boolean isOnSameSide = false;
		if (teamColor== TeamColor.BLUE) {
			if ((scale == Side.LEFT && startPosition == StartPosition.LEFT) || (scale == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
				isOnSameSide = true;
			}
		} else {
			if (teamColor == TeamColor.RED) {
				if ((scale == Side.LEFT && startPosition == StartPosition.LEFT) || (scale == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
					isOnSameSide = true;
				}
			}
		}return isOnSameSide;
	}
}