package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import javafx.geometry.Side;
/** 
 * This class determines the robots position during the beginning of the game
 *
 */
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

	private MatchConfiguration() {
		LOGGER.setLevel(Level.INFO);
		teamColor = TeamColor.UNKNOWN;
		redSwitch = Side.UNKNOWN;
		blueSwitch = Side.UNKNOWN;
		scale = Side.UNKNOWN;
		startPosition = StartPosition.UNKNOWN;

		String[] autoList = {"None", "Left", "Center", "Right"};

		NetworkTableInstance tableInstance = NetworkTableInstance.getDefault();
		NetworkTable table  = tableInstance.getTable("SmartDashboard");
		table.getEntry("Auto List").setStringArray(autoList);
	}

	public static MatchConfiguration getInstance() {
		if (instance == null) {
			instance = new MatchConfiguration();
		}
		return instance;
	}

	public void setAllianceColor(){
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

	public void setAutoModeAndStartPosition() {
		final String autoMode = SmartDashboard.getString("Auto Selector", "none");
		switch (autoMode) {

		case "Left":
			startPosition = StartPosition.LEFT;
			break;

		case "Center":
			startPosition = StartPosition.CENTER;
			break;

		case "Right":
			startPosition = StartPosition.RIGHT;

			break;

		case "None":
		default:
			startPosition = StartPosition.UNKNOWN;
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

	public void autonomousDecisionTree() {

		switch(startPosition) {
		case LEFT:
			if(isSwitchOnSameSide()) {
				//Load Left code if on same switch side true.
			} else if(isScaleOnSameSide()) {
				//Load Left code if is on same scale side true.
			} else {
				// Load Opposite scale code when the isScaleOnSameSide is false.
			}
			break;

		case CENTER:
			if(isMySwitchToTheRight()) {
				//Load code if switch is to the right in center position (true).
			}
			break;

		case RIGHT:
			if(isSwitchOnSameSide()) {
				//Load Right code if on same switch side true.
			} else if(isScaleOnSameSide()) {
				//Load Right code if is on same scale side true.
			} else {
				// Load Opposite scale code when the isScaleOnSameSide is false.
			}
			break;

		case UNKNOWN:
		default:
			// Load doNothing.
			break;
		}
	}

	public void load() {
		this.setSides();
		this.setAutoModeAndStartPosition();
		this.setAllianceColor();
	}

	public double matchTime(){
		double time = DriverStation.getInstance().getMatchTime();
		LOGGER.info("Match Time=" + time);
		return time;
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
		} else if (teamColor == TeamColor.RED) {
			if ((scale == Side.LEFT && startPosition == StartPosition.LEFT) || (scale == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
				isOnSameSide = true;
			}
		}
		return isOnSameSide;
	}

	public boolean isMySwitchToTheRight() {
		boolean isOnRightSide = false;
		if (teamColor == TeamColor.BLUE) {
			if(blueSwitch == Side.RIGHT) {
				isOnRightSide = true;
			} else if(teamColor == TeamColor.RED) {
				if(redSwitch == Side.RIGHT) {
					isOnRightSide = true;
				}
			}
		}
		return isOnRightSide;
	}
}