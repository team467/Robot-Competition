package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.Sendable;

public interface GamePiece extends Sendable {

  /**
   * Enables or disables the game piece. The game peice must be enabled to move. 
   * Passive sensors may stil work when disabled.
   */
  public void enabled(boolean enabled);

  /**
   * Gets the active status of this game piece.
   * 
   * @return true if the game piece is enabled
   */
  public boolean enabled();

  /**
   * Called once per robot iteration. This conducts any movement if enabled, 
   * and sends telemetry and state information in all cases.
   */
  public void periodic();

}