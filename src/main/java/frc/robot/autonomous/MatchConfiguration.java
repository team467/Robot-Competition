package frc.robot.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.logging.RobotLogManager;

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

  private String autoMode = "None"; 

  private ActionGroup autonomous;

  private String[] autolist= {"None","Shoot Basic"};

  private SendableChooser<String> chooser = new SendableChooser<String>();

  private MatchConfiguration() {
    teamColor = TeamColor.UNKNOWN;

    for(String s : autolist) {
      chooser.addOption(s,s);
    }

    SmartDashboard.putData("Auto Chooser", chooser);  

  }

  public static MatchConfiguration getInstance() {
    if (instance == null) {
      instance = new MatchConfiguration();
    }
    return instance;
  }

  public void setAllianceColor() {
    Alliance color; 

    color = DriverStation.getInstance().getAlliance();

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
    autoMode = chooser.getSelected();
    LOGGER.info("AutoMode: {} '", autoMode);
  }

  public ActionGroup AutoDecisionTree() {
    autonomous = Actions.doNothing();
		
		// if (autoMode.startsWith("Left")) {
		// 	Actions.startOnLeft();
		// } else if (autoMode.startsWith("Right")) {
		// 	Actions.startOnRight();
		// } else {
		// 	Actions.startInCenter();
		// }


		switch(autoMode) {
      case "None":
        autonomous = Actions.doNothing();
          break;
      case "Shoot Basic":
        autonomous = Actions.shootGroup();
        break;
      default:
       Actions.doNothing();
    }

    autonomous.enable();

    return autonomous;
  } 


  public void load() {
    LOGGER.debug("Loading game info.");
    this.setAllianceColor();
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

}