package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.Sendable;

public interface GamePiece extends Sendable {

  /**
   * Enables or disables the game piece. The game peice must be enabled to move. 
   * Passive sensors may stil work when disabled.
   * 
   * @param enabled set true to enable this game piece, false to disable
   */
  public GamePiece enabled(boolean enabled);

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