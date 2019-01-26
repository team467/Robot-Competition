package frc.robot.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.gui.SimulatedData;

import org.apache.logging.log4j.Logger;

/** 
 * This class determines the robots position during the beginning of the game.
 *
 */
public class MatchConfiguration {

  private static MatchConfiguration instance;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(MatchConfiguration.class.getName());

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

  // private String[] autoList = {
  //   "None", 
  //   "Just_Go_Forward", 
  //   "Left_Switch_Only", 
  //   "Left_Basic", 
  //   "Left_Advanced", 
  //   "Left_Our_Side_Only",
  //   "Center", 
  //   "Center_Advanced", 
  //   "Right_Switch_Only", 
  //   "Right_Basic", 
  //   "Right_Advanced", 
  //   "Right_Our_Side_Only"
  // };

  private MatchConfiguration() {
    teamColor = TeamColor.UNKNOWN;
    redSwitch = Side.UNKNOWN;
    blueSwitch = Side.UNKNOWN;
    scale = Side.UNKNOWN;

    // TODO: Fix when we figure out how to load native WPI lib modules
    // NetworkTableInstance tableInstance = NetworkTableInstance.getDefault();
    // NetworkTable table  = tableInstance.getTable("SmartDashboard");
    // table.getEntry("Auto List").setStringArray(autoList);
  }

  public static MatchConfiguration getInstance() {
    if (instance == null) {
      instance = new MatchConfiguration();
    }
    return instance;
  }

  public void setAllianceColor() {
    Alliance color; 

    if (RobotMap.useSimulator) {
      color = SimulatedData.teamColor;
    } else {
      color = DriverStation.getInstance().getAlliance();
    }

    if (color == DriverStation.Alliance.Blue) {
      LOGGER.info("Alliance is blue");
      teamColor = TeamColor.BLUE;
    } else if (color == DriverStation.Alliance.Red) {
      LOGGER.info("Alliance is red");
      teamColor = TeamColor.RED;
    } else {
      LOGGER.info("Alliance not found");
      teamColor = TeamColor.UNKNOWN;
    } 
  }

  public void setAutoModeAndStartPosition() {

    if (RobotMap.useSimulator) {
      if (SimulatedData.autoMode != null) {
        autoMode = SimulatedData.autoMode;
      } else {
        autoMode = "None";
      }
    } else {
      autoMode = SmartDashboard.getString("Auto Selector", "None");
    }

    LOGGER.info("AutoMode: {} '", autoMode);
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

    LOGGER.debug("gameData: {}", gameData);

    // String will be three letters, such as 'LRL' or 'RRR' or 'RRL'
    if (gameData.length() > 0) {

      // Our switch
      if (gameData.charAt(0) == 'L') {
        LOGGER.debug("TeamColor: {}", teamColor);
        if (teamColor == TeamColor.BLUE) {
          blueSwitch = Side.LEFT;
          LOGGER.info("Our Switch Blue LEFT");
        } else {
          if (teamColor == TeamColor.RED) {
            redSwitch = Side.LEFT;
            LOGGER.info("Our Switch Red Left");
          }
        }
      }
      if (gameData.charAt(0) == 'R') {
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
    if (gameData.charAt(1) == 'L') {
      scale = Side.LEFT;
    } else {
      scale = Side.RIGHT;
    }

    // Their switch
    if (gameData.charAt(2) == 'L') {
      if (teamColor == TeamColor.BLUE) {
        redSwitch = Side.LEFT;
        LOGGER.info("Their Switch Red LEFT");
      } else {
        if (teamColor == TeamColor.RED) {
          blueSwitch = Side.LEFT;
          LOGGER.info("Their Switch Blue LEFT");
        }
      }
      if (gameData.charAt(2) == 'R') {
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
    
    if (autoMode.startsWith("Left") || autoMode.startsWith("Just_Go_Forward")) {
      Actions.startOnLeft();
    } else if (autoMode.startsWith("Right")) {
      Actions.startOnRight();
    } else {
      Actions.startInCenter();
    }

    switch (autoMode) {

      case "Just_Go_Forward": 
        LOGGER.debug("Just going forward.");
        autonomous = Actions.crossAutoLine();
        break;

      case "Center": 
        LOGGER.info("Entering Center");
        if (isMySwitchToTheRight()) {
          LOGGER.debug("The switch is to the right | CENTER");
          autonomous = Actions.centerBasicSwitchRight();
        } else {
          LOGGER.debug("The Switch is to the left | CENTER");
          autonomous = Actions.centerBasicSwitchLeft();
        }
        break;
        
      case "Center_Advanced":
        LOGGER.info("Entering Center_Advanced");
        if (isMySwitchToTheRight()) {
          LOGGER.debug("The switch is to the right | CENTER");
          autonomous = Actions.advancedCenterRightExchange();
        } else {
          LOGGER.debug("The Switch is to the left | CENTER");
          autonomous = Actions.advancedCenterLeftExchange();
        }
        break;

      case "Left_Switch_Only":
      case "Right_Switch_Only": 
        if (isSwitchOnSameSide()) {
          LOGGER.debug("Going for switch on our side.");
          autonomous = Actions.basicSwitchOurSide();
        } else if (isScaleOnSameSide()) {
          LOGGER.debug("Switch is on opposite side so going for scale on our side");
          autonomous = Actions.basicScaleOurSide();
        } else {
          LOGGER.debug("Switch and scale on opposite side, just going forward");
          autonomous = Actions.crossAutoLine();
        }
        break;

      case "Left_Basic":
      case "Right_Basic":
        if (isScaleOnSameSide()) {
          LOGGER.debug("Going for scale on our side.");
          autonomous = Actions.basicScaleOurSide();
        } else if (isSwitchOnSameSide()) {
          LOGGER.debug("Going for switch on our side.");
          autonomous = Actions.basicSwitchOurSide();
        } else {
          LOGGER.debug("Going for scale on opposite side.");
          autonomous = Actions.basicScaleOppositeSide();
        }
        break;

      case "Left_Advanced":
      case "Right_Advanced":
        if (isSwitchOnSameSide() && isScaleOnSameSide()) {
          LOGGER.debug("Switch and scale are on our side.");
          autonomous = Actions.advancedSwitchOurSideScaleOurSide();
        } else if (isSwitchOnSameSide() && !isScaleOnSameSide()) {
          LOGGER.debug("Switch is on our side, scale opposite.");
          autonomous = Actions.advancedSwitchOurSideScaleOpposite();
        } else if (!isSwitchOnSameSide() && isScaleOnSameSide()) {
          LOGGER.debug("Switch is on opposite side, scale our side.");
          autonomous = Actions.advancedSwitchOppositeScaleOurSide();
        } else if (!isScaleOnSameSide() && !isSwitchOnSameSide()) {
          LOGGER.debug("Switch and scale are on opposite side.");
          autonomous = Actions.advancedSwitchOppositeScaleOpposite();
        }
        break;

      case "Left_Our_Side_Only":
      case "Right_Our_Side_Only":
        if (isScaleOnSameSide()) {
          LOGGER.debug("Switch and scale are on our side.");
          autonomous = Actions.basicScaleOurSide();
        } else if (isSwitchOnSameSide()) {
          LOGGER.debug("Switch is on our side, scale opposite.");
          autonomous = Actions.basicSwitchOurSide();
        } else {
          LOGGER.debug("Switch and scale are on opposite side. Just go forward.");
          autonomous = Actions.crossAutoLine();
        }
        break;

      case "None":
      default:
        autonomous = Actions.crossAutoLine();
        LOGGER.info("DO NOTHING! ------------------------------------------------ {}",
            Actions.doNothing());
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
    LOGGER.info("Match Time= {}", time);
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