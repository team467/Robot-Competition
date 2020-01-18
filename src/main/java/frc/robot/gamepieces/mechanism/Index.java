// package frc.robot.gamepieces;

// public class Index extends GamePieceBase implements GamePiece {

//   private static Index instance = null;

//   private static final Logger LOGGER = RobotLogManager.getMainLogger(Index.class.getName());
//   /**
//    * Enables or disables the game piece. The game peice must be enabled to move. 
//    * Passive sensors may stil work when disabled.
//    */
//   public static Intake getInstance() {
//     if (instance == null) {
//       instance = new Intake();
//     }
//     return instance;
//   }

//   public void enabled(boolean enabled);

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
//   public void periodic();

// }