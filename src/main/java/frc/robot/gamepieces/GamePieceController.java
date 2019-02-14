package frc.robot.gamepieces;

import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.CargoMech.CargoMechArm;
import frc.robot.gamepieces.CargoMech.CargoMechArmState;
import frc.robot.gamepieces.HatchMechanism.HatchArm;
import frc.robot.logging.RobotLogManager;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;

import org.apache.logging.log4j.Logger;

public class GamePieceController {

  private static GamePieceController instance = null;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceController.class.getName());

  // Game Pieces
  private CargoIntake cargoIntake;
  private CargoMech cargoMech;
  private HatchMechanism hatchMech;
  private Turret turret;
  private CameraSwitcher camera;

  private DriverStation467 driverStation;

  private GamePieceMode gamePieceMode;

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetrGamePieceControllerthe telemetry builder instance
   */
  public static GamePieceController getInstance()  {
    if (instance == null) {
      instance = new GamePieceController();
    }
    return instance;
  }

  public enum GamePieceMode {
    DEFENSE,
    CARGO,
    HATCH;
  }

  private GamePieceController() {
    
    cargoIntake = CargoIntake.getInstance();
    cargoMech = CargoMech.getInstance();
    hatchMech = HatchMechanism.getInstance();
    turret = Turret.getInstance();
    driverStation = DriverStation467.getInstance();
    gamePieceMode = GamePieceMode.DEFENSE;

  }

  /**
   * checks for states.
   */
  public void periodic() {

    /*
     *  //TODO: Switch Camera:
     *  Switch to front or back camera. Does not change mode away from 
     *  Hatch or Cargo, but does take camera. 
     */

    /*
     *  Enter Defense Mode:
     *  Defence mode must first move the turret to home, 
     *  then lower the arm, and finally lift the roller.
     *  The LEDs should blink red while transitioning, 
     *  and be solid red when in defence mode.
     *  Must check that it is safe to move turret.
     *  Cancels Target Lock
     */

    if (true) { // TODO: driverStation.defenseMode()
      gamePieceMode = GamePieceMode.DEFENSE;
    }
    if (gamePieceMode == GamePieceMode.DEFENSE) {
      turret.moveTurretToHome();
      if (turret.isHome()) {
        if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
          cargoIntake.arm(CargoIntakeArm.UP);
        }
        if (hatchMech.arm() == HatchArm.OUT) {
          hatchMech.arm(HatchArm.IN);
        }
        if(cargoMech.arm() != CargoMechArmState.CARGO_BIN){
          cargoMech.arm(CargoMechArm.CARGO_BIN);
        }
      }
    }

    /*
     *  //TODO: Change to Cargo mode
     *  Switches to cargo mode. LEDs should change to blue. Camera changes to
     *  the cargo camera. Should change to cargo camera even if already in cargo mode.
     *  Cargo intake arm should go down.
     */
    if(true){
      gamePieceMode = GamePieceMode.CARGO;
    }
    if(gamePieceMode == GamePieceMode.CARGO){
      turret.moveTurretToHome();
      if(turret.isHome()){
        if(hatchMech.arm() == HatchArm.OUT){
          hatchMech.arm(HatchArm.IN);
        }
        if(cargoIntake.arm() == CargoIntakeArm.UP){
          cargoIntake.arm(CargoIntakeArm.DOWN);
        }
    }

    /*
     *  //TODO: Change to Hatch mode
     *  Switches to hatch  mode. LEDs should change to gold. Camera changes to
     *  the hatch camera. Should change to cargo camera even if already in hatch mode.
     *  Cargo intake arm should go down so that turret can move.
     */

    /*
     *  //TODO: Fire Hatch
     *  Must be in hatch mode. Pushes cargo arm forward for some count of cycles,
     *  then activates hatch solinoids. After trigger is released, moves both 
     *  hatch and arm solidoids to the retracted position. May need to be combined with
     *  acquire hatch to insure that the arm can move forward during acquisition. 
     *  Could just be before acquire hatch as moves happen at end.
     */

    /*
     *  Acquire Hatch:
     *  When acquireing a hatch, the arm should move forward,
     *  but the hatch launcher should not fire.
     */

    if (true) { // TODO: driverStation.acquireHatch()
      gamePieceMode = GamePieceMode.HATCH;
    }
    if (gamePieceMode == GamePieceMode.HATCH) {
      if (hatchMech.arm() == HatchMechArm.RETRACT) {
        hatchMech.launcher(HatchLauncher.FIRE); //?
      }
    }

    /*
     *  //TODO: Acquire Cargo:
     *  Must be in Cargo Mode and roller are must be down.
     *  Turn on roller, move turret to home, lower down, and turn on claw.
     *  Must check that it is safe to move turret.
     *  Cancels Target Lock
     */

    if (true) { // TODO: driverStation.acquireCargo()
      gamePieceMode = GamePieceMode.CARGO;
    }
    if (gamePieceMode == GamePieceMode.CARGO) {
      if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
        cargoIntake.roller(CargoIntakeRoller.REVERSE);
      }
    }


    /*
     *  //TODO: Load Cargo onto arm:
     *  Must be in Cargo Mode and roller are must be down.
     *  move turret to home, turn on claw, lower arm.
     *  Must check that it is safe to move turret.
     *  Cancels Target Lock
     */

    /*
     *  //TODO: Reject Cargo
     *  Works in cargo or hatch mode. Cargo intake reverses motor to spit cargo.
     */

    /*
     *  //TODO: Fire Cargo
     *  Must be in cargo mode. Reverses claw motor to throw cargo.
     */

    /*
     *  //TODO: Set Cargo Ship
     *  Must be in cargo mode. Stop claw. Move cargo arm to cargo ship height.
     */

    /*
     *  //TODO: Set Low Rocket
     *  Must be in cargo mode. Stop claw. Move cargo arm to low rocket height.
     */

    /*
     *  //TODO: Move Turret Home
     *  Must be in hatch or cargo mode. Moves the turret to zero.
     *  If arm is in low cargo acquire postion and move is required, 
     *  move to cargo arm to low rocket
     *  Must check that it is safe to move turret.
     *  Cancels Target Lock
     */

    /*
     *  //TODO: Move Turret Right
     *  Must be in hatch or cargo mode. Moves the turret to +90.
     *  If arm is in low cargo acquire postion and move is required, 
     *  move to cargo arm to low rocket
     *  Must check that it is safe to move turret.
     *  Cancels Target Lock
     */

    /*
     *  //TODO: Move Turret Left
     *  Must be in hatch or cargo mode. Moves the turret to -90.
     *  If arm is in low cargo acquire postion and move is required, 
     *  move to cargo arm to low rocket
     *  Must check that it is safe to move turret.
     *  Cancels Target Lock
     */

    /*
     *  //TODO: Fine Adjust Turret
     *  Manually move the turret based on stick. 
     *  Should check for unsafe turret situations.
     *  Cancels target lock
     */

    /*
     *  //TODO: On Target Cargo
     *  Must be in cargo mode. If vision reports on target, 
     *  blink blue LEDs and rumble nav controller
     */

    /*
     *  //TODO: On Target Hatch
     *  Must be in hatch mode. If vision reports on target, 
     *  blink gold LEDs and rumble nav controller
     */

    /*
     *  //TODO: Target Lock Turret
     *  Must be in hatch or cargo mode. Set target to track mode.
     *  Should allow override if other turret move.
     */

    // Update all systems
    cargoIntake.periodic();
    cargoMech.periodic();
    hatchMech.periodic();
    turret.periodic();
    
  }

  /**
   * Checks to see if the turret would hit something if moved.
   * 
   * @return boolean true if safe to move, false otherwise.
   */
  private boolean isSafeToMoveTurret() {
    boolean isSafe = false;
    // TODO: Implement checks
    return isSafe;
  }

}