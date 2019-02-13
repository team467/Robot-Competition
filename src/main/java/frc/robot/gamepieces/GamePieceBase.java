package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;

abstract class GamePieceBase implements GamePiece {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceBase.class.getName());

  protected String name = "Generic Game Piece";
  protected String subsystem = "Gamepieces";
  protected boolean enabled = false;

  protected GamePieceBase(String subsystem, String name) {
    this.subsystem = subsystem;
    this.name = name;
    initSendable(TelemetryBuilder.getInstance());
    LOGGER.trace("Created base game piece");
  }

  @Override
  public GamePiece enabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public abstract void periodic();

  // Sendable interface methods for updated the SmartDashboard
  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getSubsystem() {
    return subsystem;
  }

  @Override
  public void setSubsystem(String subsystem) {
    this.subsystem = subsystem;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addBooleanProperty(
        name + "Enabled", 
        this::enabled, // Lambda called when updating network table
        (enabled) -> enabled(enabled)); // Lambda calls set enabled if changed in Network table
  }

}