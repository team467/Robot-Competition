package frc.robot.gamepieces;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.CargoMech.CargoMechArm;
import frc.robot.gamepieces.CargoMech.CargoMechArmState;
import frc.robot.gamepieces.CargoMech.CargoMechClaw;
import frc.robot.gamepieces.HatchMechanism.HatchArm;
import frc.robot.gamepieces.HatchMechanism.HatchLauncher;
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
  private NetworkTable vision;

  private GamePieceMode gamePieceMode;

  /**
   * Returns a singleton instance of the game piece controller.
   * 
   * @return GamePieceController the singleton instance
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
    camera = CameraSwitcher.getInstance();
    vision  = NetworkTableInstance.getDefault().getTable("Vision");

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
    if (driverStation.getDriveCameraFront()) {
      camera.forward();
    } else if (driverStation.getDriveCameraBack()) {
      camera.backward();
    }

    // Check for mode changes
    if (driverStation.defenseMode()) {
      /*
      *  Enter Defense Mode:
      *  Defence mode must first move the turret to home, 
      *  then lower the arm, and finally lift the roller.
      *  The LEDs should blink red while transitioning, 
      *  and be solid red when in defence mode.
      *  Must check that it is safe to move turret.
      *  Cancels Target Lock
      */
      gamePieceMode = GamePieceMode.DEFENSE;
    } else if (driverStation.getHatchMode()) {
      /*
      *  //TODO: Change to Hatch mode
      *  Switches to hatch  mode. LEDs should change to gold. Camera changes to
      *  the hatch camera. Should change to cargo camera even if already in hatch mode.
      *  Cargo intake arm should go down so that turret can move.
      */
      gamePieceMode = GamePieceMode.HATCH;
      camera.hatch();
    } else if (true) { // TODO Need enter cargo mode
      /*
      *  //TODO: Change to Cargo mode
      *  Switches to cargo mode. LEDs should change to blue. Camera changes to
      *  the cargo camera. Should change to cargo camera even if already in cargo mode.
      *  Cargo intake arm should go down.
      */
      gamePieceMode = GamePieceMode.CARGO;
      camera.cargo();
    }

    switch (gamePieceMode) {

      case DEFENSE:
        turret.moveTurretToHome();
        if (turret.isHome()) {
          if (cargoIntake.arm() == CargoIntakeArm.DOWN)
            cargoIntake.arm(CargoIntakeArm.UP);
          if (hatchMech.arm() == HatchArm.OUT) 
            hatchMech.arm(HatchArm.IN);
        }
        break;

      case CARGO:
        if (driverStation.getAcquireBall()) {
          /*
          *  //TODO: Acquire Cargo:
          *  Must be in Cargo Mode and roller arm must be down.
          *  Turn on roller, move turret to home, lower down, and turn on claw.
          *  Must check that it is safe to move turret.
          *  Cancels Target Lock
          */
          if (cargoIntake.arm() == CargoIntakeArm.DOWN) { // If cargo intake arm is down
            cargoIntake.roller(CargoIntakeRoller.REVERSE); // suck in ball
            if (turret.isHome() == false && isSafeToMoveTurret()) {
              turret.moveTurretToHome();
            }
            else { // if cargo intake arm is up
              cargoMech.arm(CargoMechArm.CARGO_BIN); //
              cargoMech.claw(CargoMechClaw.REVERSE); 
            }
          }
        } else if (driverStation.setCargoPos()) {
          /*
           *  Must be in cargo mode. Stop claw. Move cargo arm to cargo ship height.
           */
          cargoMech.claw(CargoMechClaw.STOP);
          cargoMech.arm(CargoMechArm.CARGO_SHIP);
        } else if (driverStation.setCargoPos()) {
          /*
           *  //TODO: Set Low Rocket
           *  Must be in cargo mode. Stop claw. Move cargo arm to low rocket height.
           */
          cargoMech.claw(CargoMechClaw.STOP);
          cargoMech.arm(CargoMechArm.LOW_ROCKET);
        } else if (driverStation.getFireCargo()) {
          /*
           *  Must be in cargo mode. Reverses claw motor to throw cargo.
           */
          cargoMech.claw(CargoMechClaw.FORWARD);
        } else {
          cargoMech.claw(CargoMechClaw.STOP);
        }

          /*
          *  //TODO: Load Cargo onto arm:
          *  Must be in Cargo Mode and roller are must be down.
          *  move turret to home, turn on claw, lower arm.
          *  Must check that it is safe to move turret.
          *  Cancels Target Lock
          */
        break;

      case HATCH:
        if (driverStation.fireHatch()) {
        /*
        *  //TODO: Fire Hatch
        *  Must be in hatch mode. Pushes cargo arm forward for some count of cycles,
        *  then activates hatch solenoids. After trigger is released, moves both 
        *  hatch and arm soledoids to the retracted position. May need to be combined with
        *  acquire hatch to insure that the arm can move forward during acquisition. 
        *  Could just be before acquire hatch as moves happen at end.
        */
          gamePieceMode = GamePieceMode.HATCH;
          if (gamePieceMode == GamePieceMode.HATCH){
            if (hatchMech.arm() != HatchArm.IN){
              hatchMech.arm(HatchArm.OUT);
            }
            if (hatchMech.arm() == HatchArm.OUT){
              hatchMech.launcher(HatchLauncher.FIRE);
            }
          }

        } else if (driverStation.getAcquireHatch()) {
        /*
        *  Acquire Hatch:
        *  When acquireing a hatch, the arm should move forward,
        *  but the hatch launcher should not fire.
        */
          if(gamePieceMode == GamePieceMode.HATCH){
            if (hatchMech.arm() == HatchArm.IN){
              hatchMech.arm(HatchArm.OUT);
            }
            if (hatchMech.launcher() == HatchLauncher.FIRE){
              hatchMech.launcher(HatchLauncher.RESET);
            }
          }
        }
        else {
          hatchMech.arm(HatchArm.IN);
          hatchMech.launcher(HatchLauncher.RESET);
        }

        break;

      default:
        LOGGER.error("Should always have a game piece mode.");
    }

    // Actions that apply in either cargo or hatch mode
    if ((gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH)) {
      if (driverStation.getRejectBall() && cargoIntake.arm() == CargoIntakeArm.DOWN) {
        /*
         *  Works in cargo or hatch mode. Cargo intake reverses motor to spit cargo.
         */
        cargoIntake.roller(CargoIntakeRoller.FORWARD);
      }

      if (true) {
        /*
        *  //TODO: Move Turret Home
        *  Must be in hatch or cargo mode. Moves the turret to zero.
        *  If arm is in low cargo acquire postion and move is required, 
        *  move to cargo arm to low rocket
        *  Must check that it is safe to move turret.
        *  Cancels Target Lock
        */
        if (!turret.isHome()) {
          if (isSafeToMoveTurret()) {
            turret.moveTurretToHome();
          } else {
            cargoMech.arm(CargoMechArm.LOW_ROCKET);
            turret.moveTurretToHome();
          }
        }
      }

      /*
      *  //TODO: Move Turret Right
      *  Must be in hatch or cargo mode. Moves the turret to +90.
      *  If arm is in low cargo acquire postion and move is required, 
      *  move to cargo arm to low rocket
      *  Must check that it is safe to move turret.
      *  Cancels Target Lock
      */

      if (true) {
        if (gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH){
          if (cargoMech.arm() != CargoMechArmState.LOW_ROCKET){
            cargoMech.arm(CargoMechArm.LOW_ROCKET);
            if (isSafeToMoveTurret()) {
              turret.target(90.0);
            }
          }
        }
      }

      /*
      *  //TODO: Move Turret Left
      *  Must be in hatch or cargo mode. Moves the turret to -90.
      *  If arm is in low cargo acquire postion and move is required, 
      *  move to cargo arm to low rocket
      *  Must check that it is safe to move turret.
      *  Cancels Target Lock
      */

      if (true) {
        if (gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH){
          if (cargoMech.arm() != CargoMechArmState.LOW_ROCKET){
            cargoMech.arm(CargoMechArm.LOW_ROCKET);
            if (isSafeToMoveTurret()) {
              turret.target(-90.0);
            }
          }
        }
      }

      /*
      *  //TODO: Target Lock Turret
      *  Must be in hatch or cargo mode. Set target to track mode.
      *  Should allow override if other turret move.
      */
      if (gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH) {
        // set target to track mode.
      }

      /*
      *  //TODO: Fine Adjust Turret
      *  Manually move the turret based on stick. 
      *  Should check for unsafe turret situations.
      *  Cancels target lock
      */
      if (driverStation.getFineAdjustTurret() != 0.0) {
        if (isSafeToMoveTurret()) {
          turret.manual(driverStation.getFineAdjustTurret());
        }
      }

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

    }
    
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
    boolean isSafe = (cargoMech.isSafeToMoveTurret() 
        && cargoIntake.arm() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move turret? {}", isSafe);
    return isSafe;
  }

}