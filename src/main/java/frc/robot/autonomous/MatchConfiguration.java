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
      = RobotLogManager.getLogger(MatchConfiguration.class.getName());

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

  private MatchConfiguration() {
    teamColor = TeamColor.UNKNOWN;
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