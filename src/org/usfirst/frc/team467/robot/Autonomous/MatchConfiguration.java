package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
/** 
 * This class determines the robots position during the beginning of the game
 *
 */
public class MatchConfiguration {
	//static final int ITERATION_TIME_MS = 10;	
	//int durationMS;

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

	public void allianceColor(){
		DriverStation.Alliance color;
		color = DriverStation.getInstance().getAlliance();
		if(color == DriverStation.Alliance.Blue) {
			LOGGER.info("Alliance is blue");
			teamColor = TeamColor.BLUE;
		} else if (color == DriverStation.Alliance.Red){
			LOGGER.info("Alliance is red");
			teamColor = TeamColor.RED;
		} else {
			LOGGER.info("Alliance not found");
			teamColor = TeamColor.UNKNOWN;
		} 
	}

	public void setSides() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		// String will be three letters, such as 'LRL' or 'RRR' or 'RRL'
		if(gameData.length() > 0) {

			// Our switch
			if(gameData.charAt(0) == 'L') {
				if (teamColor == TeamColor.BLUE ) {
					blueSwitch = Side.LEFT;
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.LEFT;
					}
				}
			}
			if(gameData.charAt(0) == 'R') {
				if (teamColor == TeamColor.BLUE) {
					blueSwitch = Side.RIGHT;
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.RIGHT;
					}
				}
			}	
		}

		// Scale
		if(gameData.charAt(1) == 'L') {
			scale = Side.LEFT;
		} else {
			scale = Side.RIGHT;
		}

		// Their switch
		if(gameData.charAt(2) == 'L') {
			if (teamColor == TeamColor.BLUE ) {
				blueSwitch = Side.LEFT;
			} else {
				if (teamColor == TeamColor.RED) {
					redSwitch = Side.LEFT;
				}
			}
			if(gameData.charAt(2) == 'R') {
				if (teamColor == TeamColor.BLUE) {
					blueSwitch = Side.RIGHT;
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.RIGHT;
					}
				}
			}
		}
	}

	//	public void matchTime(){
	//		double time;
	//	if(durationMS > 0)
	//			time = DriverStation.getInstance().getMatchTime();
	//		durationMS -= ITERATION_TIME_MS;
	//		LOGGER.info("Match Time=" + time);
	//	}

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

		String[] autoList = {"none", "go"};

		NetworkTableInstance tableInstance = NetworkTableInstance.getDefault();
		NetworkTable table  = tableInstance.getTable("SmartDashboard");
		table.getEntry("Auto List").setStringArray(autoList);
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