package org.usfirst.frc.team467.robot.Autonomous;
// changes 
import org.apache.log4j.Level;
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
	private String simulatedGameSpecificMessage = "LRL";
	
	private Alliance simulatedTeamColor = Alliance.Blue;
	
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
			autoMode = simulatedAutoMode;
		} else {
			autoMode = SmartDashboard.getString("Auto Selector", "none");
		}
		
		LOGGER.info( "AutoMode: '" + autoMode + "'");

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
		String gameData;
		
		if (RobotMap.useSimulator) {
			gameData = simulatedGameSpecificMessage;
		} else {
			gameData = DriverStation.getInstance().getGameSpecificMessage();
		}
		
		LOGGER.debug("gameData: " + gameData);
 			
		// String will be three letters, such as 'LRL' or 'RRR' or 'RRL'
		if(gameData.length() > 0) {

			// Our switch
			if(gameData.charAt(0) == 'L') {
				LOGGER.debug("HI: "+ teamColor);
				if (teamColor == TeamColor.BLUE ) {
					blueSwitch = Side.LEFT;
					LOGGER.info("Our Switch Blue LEFT");
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.LEFT;
						LOGGER.info("Our Swich Red Left");
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
				blueSwitch = Side.LEFT;
				LOGGER.info("Their Switch BlueSwitch LEFT");
			} else {
				if (teamColor == TeamColor.RED) {
					redSwitch = Side.LEFT;
					LOGGER.info("Their Switch RedSwitch LEFT");
				}
			}
			if(gameData.charAt(2) == 'R') {
				if (teamColor == TeamColor.BLUE) {
					blueSwitch = Side.RIGHT;
					LOGGER.info("Their Switch BlueSwitch Right");
				} else {
					if (teamColor == TeamColor.RED) {
						redSwitch = Side.RIGHT;
						LOGGER.info("Their Switch RedSwitch Right");
					}
				}
			}
		}
	}

	public ActionGroup autonomousDecisionTree() {
		
		autonomous = Actions.doNothing();

		switch(startPosition) {
		
		case LEFT:
			if(isSwitchOnSameSide()) {
				autonomous = Actions.leftBasicSwitch();
				LOGGER.debug("isSwitchOnSameSide Left True -----------------------");
			} else if(isScaleOnSameSide()) {
				//Load Left code if is on same scale side true.
				LOGGER.debug("LEFT SCALE ------------------------------ TRUE");
			} else {
				// Load Opposite scale code when the isScaleOnSameSide is false.
				LOGGER.debug("LEFT Scale ----------------------------- False");
			}
			break;

		case CENTER:
			if(isMySwitchToTheRight()) {
				//Load code if switch is to the right in center position (true).
				LOGGER.debug("IsMySwitchToTheRight----------------------------Center True");
			}
			break;

		case RIGHT: 
			if(isSwitchOnSameSide()) {
				//Load Right code if on same switch side true.
				LOGGER.debug("Right isSwitchOnSameSide-----------------------------true ");
			} else if(isScaleOnSameSide()) {
				//Load Right code if is on same scale side true.
				LOGGER.debug("Scale is on same side");
			} else {
				// Load Opposite scale code when the isScaleOnSameSide is false.
				LOGGER.debug("Scale is on opposite side");
			}
			break;

		case UNKNOWN:
		default:
			autonomous = Actions.doNothing();
			LOGGER.info("DO NOTHING! ---------------------------------------------------------" + Actions.doNothing());
		}
		
		autonomous.enable();
		return autonomous;
	}

	public void load() {
		LOGGER.debug("Loading game info.");
		this.setAllianceColor();
		this.setSides();
		this.setAutoModeAndStartPosition();
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