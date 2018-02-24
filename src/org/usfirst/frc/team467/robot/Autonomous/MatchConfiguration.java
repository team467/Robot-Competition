package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.RobotMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** 
 * This class determines the robots position during the beginning of the game
 *
 */
public class MatchConfiguration {

	// Simulator variables
	
	private String simulatedGameSpecificMessage = "LLL";

	private Alliance simulatedTeamColor = Alliance.Red;

	private String simulatedAutoMode = "Left";

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

	private ActionGroup autonomous;

	private MatchConfiguration() {
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
		Alliance color; 

		if (RobotMap.useSimulator) {
			color = simulatedTeamColor;
		} else {
			color = DriverStation.getInstance().getAlliance();
		}

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
		String autoMode; 

		if (RobotMap.useSimulator) {
			autoMode = simulatedAutoMode.toUpperCase();
		} else {
			autoMode = SmartDashboard.getString("Auto Selector", "none").toUpperCase();
		}

		LOGGER.info( "AutoMode: '" + autoMode + "'");

		switch (autoMode) {

		case "LEFT":
			LOGGER.debug("Set start position to left side");
			startPosition = StartPosition.LEFT;
			break;

		case "CENTER":
			LOGGER.debug("Set start position in the center");
			startPosition = StartPosition.CENTER;
			break;

		case "RIGHT":
			LOGGER.debug("Set start position to right side");
			startPosition = StartPosition.RIGHT;

			break;

		case "NONE":
		default:
			LOGGER.debug("Start position unknown");
			startPosition = StartPosition.UNKNOWN;
		}

	}

	public void setSides() {
		String gameData;

		if (RobotMap.USE_FAKE_GAME_DATA) {
			if (RobotMap.useSimulator) {
				gameData = simulatedGameSpecificMessage;
			} else {
				gameData = SmartDashboard.getString("DB/String 5", "LLL");
			}
		} else {
			gameData = DriverStation.getInstance().getGameSpecificMessage();
		}

		LOGGER.debug("gameData: " + gameData);

		// String will be three letters, such as 'LRL' or 'RRR' or 'RRL'
		if(gameData.length() > 0) {

			// Our switch
			if(gameData.charAt(0) == 'L') {
				LOGGER.debug("TeamColor: "+ teamColor);
				if (teamColor == TeamColor.BLUE ) {
					blueSwitch = Side.LEFT;
					LOGGER.info("Our Switch Blue LEFT");
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.LEFT;
						LOGGER.info("Our Switch Red Left");
					}
				}
			}
			if(gameData.charAt(0) == 'R') {
				if (teamColor == TeamColor.BLUE) {
					blueSwitch = Side.RIGHT;
					LOGGER.info("Our Switch Blue Right");
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.RIGHT;
						LOGGER.info("Our Switch Red Right");
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
				redSwitch = Side.LEFT;
				LOGGER.info("Their Switch Red LEFT");
			} else {
				if (teamColor == TeamColor.RED) {
					blueSwitch = Side.LEFT;
					LOGGER.info("Their Switch Blue LEFT");
				}
			}
			if(gameData.charAt(2) == 'R') {
				if (teamColor == TeamColor.BLUE) {
					redSwitch = Side.RIGHT;
					LOGGER.info("Their Switch Red Right");
				} else {
					if (teamColor == TeamColor.RED) {
						blueSwitch = Side.RIGHT;
						LOGGER.info("Their Switch Blue Right");
					}
				}
			}
		}
	}

	public ActionGroup autonomousDecisionTree() {

		LOGGER.debug("Entering decision tree");
		autonomous = Actions.doNothing();

		switch(startPosition) {

		case LEFT:
			if(isSwitchOnSameSide() && !isScaleOnSameSide()) {
				LOGGER.debug("Switch is on the same side and scale is on the opposite side | LEFT"); 
				autonomous = Actions.leftAdvancedSwitchRightScale();
			} else if (isSwitchOnSameSide() && isScaleOnSameSide()){
				LOGGER.debug("Switch is on same side and scale is on same side | LEFT");
				autonomous = Actions.leftAdvancedSwitch();
			} else if(isScaleOnSameSide() && !isSwitchOnSameSide()) {
				LOGGER.debug("Scale is on same sode and switch is on the opposite side | LEFT");
				autonomous = Actions.leftAdvancedSwitchRightScaleLeft();
			} else if(!isSwitchOnSameSide() && !isScaleOnSameSide()) {
				LOGGER.debug("Switch is on the opposite side and the scale is on the opposite side | LEFT");
				autonomous = Actions.leftAdvancedSwitchRightScaleRight();
			} 

			break;

		case CENTER: 
			LOGGER.info("Entering Center");
			if(isMySwitchToTheRight()) {
				LOGGER.debug("The switch is to the right | CENTER");
				autonomous = Actions.centerBasicSwitchRight();
			} else {
				LOGGER.debug("The Switch is to the left | CENTER");
				autonomous = Actions.centerBasicSwitchLeft();
			}
			break;

		case RIGHT: 
			if(isSwitchOnSameSide() && !isScaleOnSameSide()) {
				LOGGER.debug("Switch is on same side and scale is on opposite side | RIGHT");
				autonomous = Actions.rightAdvancedSwitchLeftScale();
			} else if (isSwitchOnSameSide() && isScaleOnSameSide()) {
				LOGGER.debug("Switch is on same side and scale is one same side | RIGHT");
				autonomous = Actions.rightAdvancedSwitch();
			} else if(isScaleOnSameSide() && !isSwitchOnSameSide()) {
				LOGGER.debug("Scale is on same side and switich on opposite side | RIGHT");
				autonomous = Actions.rightAdvancedSwitchLeftScaleRight();
			} else if(!isScaleOnSameSide() && !isSwitchOnSameSide()) {
				LOGGER.debug("Scale is on opposite side |RIGHT");
				autonomous = Actions.rightAdvancedSwitchLeftScaleLeft();
			}
			break;

		case UNKNOWN:
		default:
			autonomous = Actions.doNothing();
			LOGGER.info("DO NOTHING! ------------------------------------------------" + Actions.doNothing());
		}

		autonomous.enable();
		return autonomous;
	}

	public void load() {
		LOGGER.debug("Loading game info.");
		this.setAllianceColor();
		this.setSides();
		this.isScaleOnSameSide();
		this.setAutoModeAndStartPosition();
	}

	public double matchTime() {
		double time = DriverStation.getInstance().getMatchTime();
		LOGGER.info("Match Time=" + time);
		return time + 20;
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
				LOGGER.info("isSwitchOnSameSide Left and Right | Blue");
			}
		} else {
			if (teamColor == TeamColor.RED) {
				if (redSwitch == Side.LEFT && startPosition == StartPosition.LEFT || (redSwitch == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
					isOnSameSide = true;
					LOGGER.info("isSwitchOnSameSide Left and Right | Red");
				}
			}
		}
		return isOnSameSide;
	}

	public boolean isScaleOnSameSide() {
		boolean isOnSameSide = false; 
		if (teamColor == TeamColor.BLUE) {
			if ((scale == Side.LEFT && startPosition == StartPosition.LEFT) || (scale == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
				isOnSameSide = true;
				LOGGER.info("Scale is on Same side testing Blue");
			}
		} else if (teamColor == TeamColor.RED) {
			if ((scale == Side.LEFT && startPosition == StartPosition.LEFT) || (scale == Side.RIGHT && startPosition == StartPosition.RIGHT)) {
				isOnSameSide = true;
				LOGGER.info("Scale is on same side of testing Red");
			}
		}
		return isOnSameSide;
	}

	public boolean isMySwitchToTheRight() {
		boolean isOnRightSide = false;
		if (teamColor == TeamColor.BLUE && blueSwitch == Side.RIGHT) {
			isOnRightSide = true;
		} else if (teamColor == TeamColor.RED && redSwitch == Side.RIGHT) {
			isOnRightSide = true;
		} 
		return isOnRightSide;
	}
}