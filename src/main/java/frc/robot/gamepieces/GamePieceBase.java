package frc.robot.gamepieces;

import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;

import org.apache.logging.log4j.Logger;

public abstract class GamePieceBase implements GamePiece {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceBase.class.getName());

  protected String name = "Generic Game Piece";
  protected String subsystem = "Gamepieces";
  protected boolean enabled = false;

  protected GamePieceBase(String subsystem, String name) {
    this.subsystem = subsystem;
    this.name = name;
    registerMetrics();
    LOGGER.trace("Created base game piece");
  }

  @Override
  public void enabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public abstract void periodic();
  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addBooleanMetric(name + "Enabled", this::enabled);
  }

}