package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.simulator.gui.SimulatedData;

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

	private TeamColor teamColor;

	private Side redSwitch;

	private Side blueSwitch;

	private Side scale;

	private String autoMode = "None"; 

	private ActionGroup autonomous;

	private String[] autoList = {"None", "Left_Switch_Only", "Left_Basic", "Left_Advanced", 
			"Center", "Right_Switch_Only", "Right_Basic", "Right_Advanced"};

	private MatchConfiguration() {
		teamColor = TeamColor.UNKNOWN;
		redSwitch = Side.UNKNOWN;
		blueSwitch = Side.UNKNOWN;
		scale = Side.UNKNOWN;

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
			color = SimulatedData.teamColor;
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

		if (RobotMap.useSimulator) {
			autoMode = SimulatedData.autoMode;
		} else {
			autoMode = SmartDashboard.getString("Auto Selector", "None");
		}

		LOGGER.info( "AutoMode: '" + autoMode + "'");

	}

	public void setSides() {
		String gameData;

		if (RobotMap.USE_FAKE_GAME_DATA) {
			if (RobotMap.useSimulator) {
				gameData = SimulatedData.gameSpecificMessage.toUpperCase();
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

		switch(autoMode) {

		case "Left_Basic":
			if(isSwitchOnSameSide() && !isScaleOnSameSide()) {
				LOGGER.debug("Switch is on the same side and scale is on the opposite side | LEFT"); 
				autonomous = Actions.leftBasicSwitchLeft();
			} else if (isSwitchOnSameSide() && isScaleOnSameSide()){
				LOGGER.debug("Switch is on same side and scale is on same side | LEFT");
				autonomous = Actions.leftBasicScaleLeft();
			} else if(!isSwitchOnSameSide() && !isScaleOnSameSide()) {
				LOGGER.debug("Switch is on the opposite side and the scale is on the opposite side | LEFT");
				autonomous = Actions.leftBasicScaleRight();
			} 
			break;

		case "Left_Switch_Only":
			if(isSwitchOnSameSide()) {
				LOGGER.debug("Switch is on the same side | LEFT"); 
				autonomous = Actions.leftBasicSwitchLeft();
			} else {
				LOGGER.debug("Switch is on the opposite side | LEFT");
				autonomous = Actions.goStraight();
			}
			break;

		case "Left_Advanced":
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

		case "Center": 
			LOGGER.info("Entering Center");
			if(isMySwitchToTheRight()) {
				LOGGER.debug("The switch is to the right | CENTER");
				autonomous = Actions.centerBasicSwitchRight();
			} else {
				LOGGER.debug("The Switch is to the left | CENTER");
				autonomous = Actions.centerBasicSwitchLeft();
			}
			break;

		case "Right_Basic": 
			if(isSwitchOnSameSide() && !isScaleOnSameSide()) {
				LOGGER.debug("Switch is on same side and scale is on opposite side | RIGHT");
				autonomous = Actions.rightBasicSwitchRight();
			} else if (isSwitchOnSameSide() && isScaleOnSameSide()) {
				LOGGER.debug("Switch is on same side and scale is one same side | RIGHT");
				autonomous = Actions.rightBasicScaleRight();
			} else if(isScaleOnSameSide() && !isSwitchOnSameSide()) {
				LOGGER.debug("Scale is on same side and switich on opposite side | RIGHT");
				autonomous = Actions.rightBasicScaleLeft();
			}
			break;

		case "Right_Switch_Only": 
			if(isSwitchOnSameSide()) {
				LOGGER.debug("Switch is on same side| RIGHT");
				autonomous = Actions.rightBasicSwitchRight();
			} else {
				LOGGER.debug("Switch is on opposite side| RIGHT");
				autonomous = Actions.goStraight();
			}
			break;

		case "Right_Advanced":
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

		case "None":
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

	public boolean isSwitchOnSameSide() {
		boolean isOnSameSide = false;
		boolean onLeft = false;
		boolean onRight = false;
		
		if (autoMode.startsWith("Left")) {
			onLeft = true;
		}
		
		if (autoMode.startsWith("Right")) {
			onRight = true;
		}
		
		if (teamColor == TeamColor.BLUE) {
			if ((blueSwitch == Side.LEFT && onLeft) || (blueSwitch == Side.RIGHT && onRight)) {
				isOnSameSide = true;
				LOGGER.info("isSwitchOnSameSide Left and Right | Blue");
			}
		} else {
			if (teamColor == TeamColor.RED) {
				if ((redSwitch == Side.LEFT && onLeft) || (redSwitch == Side.RIGHT && onRight)) {
					isOnSameSide = true;
					LOGGER.info("isSwitchOnSameSide Left and Right | Red");
				}
			}
		}
		return isOnSameSide;
	}

	public boolean isScaleOnSameSide() {
		boolean isOnSameSide = false; 
		boolean onLeft = false;
		boolean onRight = false;
		
		if (autoMode.startsWith("Left")) {
			onLeft = true;
		}
		
		if (autoMode.startsWith("Right")) {
			onRight = true;
		}
		
		if (teamColor == TeamColor.BLUE) {
			if ((scale == Side.LEFT && onLeft) || (scale == Side.RIGHT && onRight)) {
				isOnSameSide = true;
				LOGGER.info("Scale is on Same side testing Blue");
			}
		} else if (teamColor == TeamColor.RED) {
			if ((scale == Side.LEFT && onLeft) || (scale == Side.RIGHT && onRight)) {
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