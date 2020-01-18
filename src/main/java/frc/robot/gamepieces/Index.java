package frc.robot.gamepieces;

import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Index extends GamePieceBase implements GamePiece {

  private static Index instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Index.class.getName());
//   /**
//    * Enables or disables the game piece. The game peice must be enabled to move. 
//    * Passive sensors may stil work when disabled.
//    */
  public static Index getInstance() {
    if (instance == null) {
      instance = new Index();
    }
    return instance;
  }

  public void enabled(boolean enabled) {

  }

//   /**
//    * Gets the active status of this game piece.
//    * 
//    * @return true if the game piece is enabled
//    */
//   //public boolean enabled();

//   /**
//    * Called once per robot iteration. This conducts any movement if enabled, 
//    * and sends telemetry and state information in all cases.
//    */

private Index() {
    super("Telemetry", "Index");
    // Initialize
}
  public void periodic(){

  }

}