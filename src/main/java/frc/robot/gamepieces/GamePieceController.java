package frc.robot.gamepieces;

import static org.apache.logging.log4j.util.Unbox.box;
import frc.robot.RobotMap;
import frc.robot.gamepieces.mechanisms.CargoIntake;
import frc.robot.gamepieces.mechanisms.CargoWrist;
import frc.robot.gamepieces.mechanisms.HatchMechanism;
import frc.robot.gamepieces.mechanisms.Turret;
import frc.robot.gamepieces.mechanisms.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.mechanisms.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.mechanisms.CargoWrist.CargoMechClaw;
import frc.robot.gamepieces.mechanisms.CargoWrist.CargoWristControlStates;
import frc.robot.gamepieces.mechanisms.CargoWrist.CargoMechWristState;
import frc.robot.gamepieces.mechanisms.HatchMechanism.HatchArm;
import frc.robot.gamepieces.mechanisms.HatchMechanism.HatchLauncher;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.sensors.LedI2C;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;
import frc.robot.vision.VisionController;
import org.apache.logging.log4j.Logger;

public class GamePieceController {

  private static GamePieceController instance = null;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceController.class.getName());

  protected String name = "Game Piece Controller";
  protected String subsystem = "Gamepieces";

  // Game Pieces
  private CargoIntake cargoIntake;
  private CargoWrist cargoMech;
  private HatchMechanism hatchMech;
  private Turret turret;
  private CameraSwitcher camera;

  private DriverStation467 driverStation;
  private VisionController visionController;
  private LedI2C led;

  private GamePieceMode mode;

  /**
   * Returns a singleton instance of the game piece controller.
   * 
   * @return GamePieceController the singleton instance
   */
  public static GamePieceController getInstance() {
    if (instance == null) {
      instance = new GamePieceController();
    }
    return instance;
  }

  /**
   * This method would be used to override the mode for testing by using Network
   * Tables. It is private as that is the only use method.
   * 
   * @param mode the mode for testing.
   */
  private void testMode(String mode) {
    this.mode = GamePieceMode.valueOf(mode);
  }

  public enum GamePieceMode {
    DEFENSE, CARGO, HATCH
  }

  private GamePieceController() {
    driverStation = DriverStation467.getInstance();
    LOGGER.debug("Initializing driverstation");
    cargoIntake = CargoIntake.getInstance();
    LOGGER.debug("Initializing cargoIntake");
    cargoMech = CargoWrist.getInstance();
    LOGGER.debug("Initializing cargoMech");
    hatchMech = HatchMechanism.getInstance();
    LOGGER.debug("Initializing hatch mechanism");
    turret = Turret.getInstance();
    LOGGER.debug("Initializing turret");
    camera = CameraSwitcher.getInstance();
    LOGGER.debug("Initializing camera");
    visionController = VisionController.getInstance();
    LOGGER.debug("Initializing vision controller");
    led = LedI2C.getInstance();

    // Enabling game pieces
    LOGGER.debug("Enabling the game pieces.");
    cargoIntake.enabled(true);
    cargoMech.enabled(true);
    hatchMech.enabled(true);
    turret.enabled(true);

    LOGGER.debug("Starting in DEFENSE mode.");

    mode = GamePieceMode.DEFENSE;

    registerMetrics();
  }

  /**
   * Checks for states from driverStation.
   */
  public void periodic() {

    // Separate reading from driver station from processing state 
    // so that tests can manually feed inputs.
    processGamePieceState(
        driverStation.getDriveCameraFront(),
        driverStation.getDriveCameraBack(),
        driverStation.getDisableSafety(),
        driverStation.getDefenseMode(),
        driverStation.getHatchMode(),
        driverStation.getCargoMode(),
        driverStation.getIntakeUp(),
        driverStation.getIntakeDown(),
        driverStation.getAcquireBall(),
        driverStation.getFireCall(),
        driverStation.getCargoWristCargoShipPosition(),
        driverStation.getCargoWristLowRocketPosition(),
        driverStation.getAcquireHatch(),
        driverStation.getFireHatch(),
        driverStation.getRejectBall(),
        driverStation.getIntakeBall(),
        driverStation.getTurretRight(),
        driverStation.getTurretLeft(),
        driverStation.getTurretHome(),
        driverStation.getAutoTargetButtonPressed(),
        driverStation.getManualWristMove(),
        driverStation.getManualTurretMove()
    );

  }

  void processGamePieceState(
      boolean driveCameraFront,
      boolean driveCameraRear,
      boolean disableSafety,
      boolean defenseMode,
      boolean hatchMode,
      boolean cargoMode,
      boolean intakeUp,
      boolean intakeDown,
      boolean acquireCargo,
      boolean fireCargo,
      boolean moveCargoWristToCargoShipPosition,
      boolean moveCargoWristToLowRocketPosition,
      boolean acquireHatch,
      boolean fireHatch,
      boolean rejectCargo,
      boolean intakeCargo,
      boolean moveTurretRight,
      boolean moveTurretLeft,
      boolean moveTurretToHome,
      boolean enableTargetLock,
      double  manualWristMove,
      double  manualTurretMove) {

    // Depending on driver input, camera view switches to front or back.
    // Does not change the mode away from Hatch or Cargo, but does take camera.
    if (driveCameraFront) {
      LOGGER.debug("Forward Camera");
      camera.forward();
    } else if (driveCameraRear) {
      LOGGER.debug("Backward Camera");
      camera.backward();
    }

    mode = switchMode(defenseMode, hatchMode, cargoMode);

    switch (mode) {

      case DEFENSE:
        activateDefenseMode(disableSafety);
        break;

      case CARGO:
        cargoMode(
            disableSafety,
            acquireCargo,
            intakeUp,
            intakeDown,
            fireCargo,
            moveCargoWristToCargoShipPosition,
            moveCargoWristToLowRocketPosition,
            rejectCargo,
            intakeCargo,
            moveTurretRight,
            moveTurretLeft,
            moveTurretToHome,
            enableTargetLock,
            manualWristMove,
            manualTurretMove);
        break;

      case HATCH:
        hatchMode(
            disableSafety,
            acquireHatch,
            fireHatch,
            rejectCargo,
            intakeCargo,
            moveTurretRight,
            moveTurretLeft,
            moveTurretToHome,
            enableTargetLock,
            manualTurretMove);
        break;

      default:
        LOGGER.error("Should always have a game piece mode.");
    }

    updateGamePieces();
  }

  private boolean ensureSafeToMoveWrist(boolean disableSafety) {
    if (disableSafety) {
      return true;
    }
    boolean isSafe = (cargoIntake.arm() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move wrist? {}", box(isSafe));
    if (!isSafe) {
      LOGGER.debug("Moving cargo intake arm down so wrist can move.");
      cargoIntake.arm(CargoIntakeArm.DOWN);
    }
    return isSafe;
  }

  private boolean ensureTurretSafeToMove(boolean disableSafety) {
    if (disableSafety) {
      return true;
    }
    boolean isSafe = (cargoMech.isSafeToMoveTurret() 
        && cargoIntake.arm() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move turret? {}", box(isSafe));
    if (!isSafe) {
      if (ensureSafeToMoveWrist(disableSafety)) {
        LOGGER.debug("Moving wrist to safe position.");
        cargoMech.wrist(CargoWristControlStates.LOW_ROCKET);
      }
      LOGGER.debug("Making sure hatch mechanism arm is in so turret can move.");
      hatchMech.arm(HatchArm.IN);
    } 
    return isSafe;
  }

  private boolean moveTurretHome(boolean disableSafety) {
    boolean isHome = turret.isHome();
    if (!isHome) {
      LOGGER.debug("Moving turret home.");
      if (ensureTurretSafeToMove(disableSafety)) {
        LOGGER.error("Safe to move, moving");
        turret.moveTurretToHome();
      }
    } else {
      LOGGER.debug("Turret doesn't need to move to be at home.");
    }
    return isHome;
  }

  void activateDefenseMode(boolean disableSafety) {
    if (moveTurretHome(disableSafety)) {
      if (hatchMech.arm() == HatchArm.OUT) {
        LOGGER.debug("DEFENSE: Move hatch in.");
        hatchMech.arm(HatchArm.IN);
      }
      if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
        if (ensureSafeToMoveWrist(disableSafety)) {
          LOGGER.debug("DEFENSE: Move cargo wrist to bin.");
          cargoMech.wrist(CargoWristControlStates.CARGO_BIN);
        }
      } else if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
        LOGGER.debug("DEFENSE: Moving cargo intake arm up, final step.");
        cargoIntake.arm(CargoIntakeArm.UP);
      }
    }
  }

  void cargoMode(
      boolean disableSafety,
      boolean acquireCargo,
      boolean intakeUp,
      boolean intakeDown,
      boolean fireCargo,
      boolean moveCargoWristToCargoShipPosition,
      boolean moveCargoWristToLowRocketPosition,
      boolean rejectCargo,
      boolean intakeCargo,
      boolean moveTurretRight,
      boolean moveTurretLeft,
      boolean moveTurretToHome,
      boolean enableAutoTargeting,
      double  manualWristMove,
      double  manualTurretMove) {

    if (acquireCargo) {
      if (disableSafety) {
        moveTurretHome(disableSafety);
        cargoIntake.arm(CargoIntakeArm.DOWN);
        cargoIntake.roller(CargoIntakeRoller.INTAKE); // Suck ball into cargo intake mech
        cargoMech.wrist(CargoWristControlStates.CARGO_BIN);
        cargoMech.claw(CargoMechClaw.INTAKE);
        intakeCargo = true; // Set to true so that it isn't stopped
      } else {
        LOGGER.debug("CARGO: Trying to acquire cargo");
        if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
          LOGGER.debug("CARGO: Cargo intake arm is down");
          if (moveTurretHome(disableSafety)) { // Overrides move turret right or left
            if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
              if (ensureSafeToMoveWrist(disableSafety)) {
                LOGGER.debug("CARGO: Moving wrist to bin");
                cargoMech.wrist(CargoWristControlStates.CARGO_BIN);
              }
            } else {
              LOGGER.debug("CARGO: Putting rollers in reverse");
              cargoMech.claw(CargoMechClaw.INTAKE);
              intakeCargo = true; // Set to true so that it isn't stopped
              cargoIntake.roller(CargoIntakeRoller.INTAKE); // Suck ball into cargo intake mech
            }
          } 
        } else {
         cargoIntake.arm(CargoIntakeArm.DOWN);
        }
      }
    } else if (moveCargoWristToCargoShipPosition) {
      LOGGER.debug("CARGO: Stopping cargo roller and claw");
      cargoMech.claw(CargoMechClaw.STOP);
      cargoIntake.roller(CargoIntakeRoller.STOP);
      LOGGER.debug("CARGO: Move Cargo Mech wrist to the Cargo Ship height.");
      if (cargoMech.wrist() != CargoMechWristState.CARGO_SHIP && ensureSafeToMoveWrist(disableSafety)) {
        cargoMech.wrist(CargoWristControlStates.CARGO_SHIP);
      }
    } else if (moveCargoWristToLowRocketPosition) {
      LOGGER.debug("CARGO: Stopping cargo roller and claw");
      cargoMech.claw(CargoMechClaw.STOP);
      cargoIntake.roller(CargoIntakeRoller.STOP);
      if (cargoMech.wrist() != CargoMechWristState.LOW_ROCKET && ensureSafeToMoveWrist(disableSafety)) {
        LOGGER.debug("CARGO: Move Cargo Mech wrist to the Low Rocket height.");
        cargoMech.wrist(CargoWristControlStates.LOW_ROCKET);
      }
    } else if (fireCargo) {
      LOGGER.debug("CARGO: FIRING BALL!");
      cargoMech.claw(CargoMechClaw.FIRE);
    } else {
      LOGGER.debug("CARGO: Stopping cargo roller and claw");
      cargoMech.claw(CargoMechClaw.STOP);
      cargoIntake.roller(CargoIntakeRoller.STOP);
    }

    if (intakeCargo && cargoIntake.arm() == CargoIntakeArm.DOWN) {
      LOGGER.debug("CARGO: Grabbing ball");
      cargoIntake.roller(CargoIntakeRoller.INTAKE);
    } // Reject and stop are handled in the eitherHatchOrCargoMode method.

    if (intakeUp) {
      if (cargoMech.wrist() == CargoMechWristState.CARGO_BIN || disableSafety) {
        cargoIntake.arm(CargoIntakeArm.UP);
      }
    } else if (intakeDown) {
      if (cargoMech.wrist() == CargoMechWristState.CARGO_BIN || disableSafety) {
        cargoIntake.arm(CargoIntakeArm.DOWN);
      }
    }

    /*
     * Manual Override of the wrist check to make sure the roller is dd
     */
    if (manualWristMove != 0.0) {
      if (ensureSafeToMoveWrist(disableSafety)) {
        LOGGER.debug("CARGO: Setting wrist speed to {} manually.", box(manualWristMove));
        cargoMech.manualWristMove(manualWristMove);
      }
    } else {
      cargoMech.manualWristMove(0);
    }

    // Turret moves are last as turret safety takes precidence over other wrist moves
    if (moveTurretRight) {
      if (ensureTurretSafeToMove(disableSafety)) {
        LOGGER.debug("CARGO: Moving turret to right of cargo side.");
        turret.target(-90.0);
      }
    } else if (moveTurretLeft) {
      if (ensureTurretSafeToMove(disableSafety)) {
        LOGGER.debug("CARGO: Moving turret to left of cargo side.");
        turret.target(90.0);
      }
    }

    if (manualTurretMove != 0.0) {
      LOGGER.debug("CARGO: Manually move the turret.");
      if (true) { //ensureTurretSafeToMove()
        LOGGER.warn("turret is safe to move, manualTurret move: {}", box(manualTurretMove));
        turret.manual(manualTurretMove);
      }
    } else if (turret.isOveride()) {
      turret.manual(0);
    }


    eitherHatchOrCargoMode(
        disableSafety,
        rejectCargo, 
        intakeCargo, 
        moveTurretToHome, 
        enableAutoTargeting, 
        manualTurretMove);
  }

  void hatchMode(
      boolean disableSafety,
      boolean acquireHatch,
      boolean fireHatch,
      boolean rejectCargo,
      boolean intakeCargo,
      boolean moveTurretRight,
      boolean moveTurretLeft,
      boolean moveTurretToHome,
      boolean enableAutoTargeting,
      double  manualTurretMove) {
      
    disableSafety = true;

    if (acquireHatch) {
      LOGGER.debug("HATCH: Moving the hatch arm out.");
      hatchMech.arm(HatchArm.OUT);
    } else {
      LOGGER.debug("HATCH: Moving hatch arm in.");
      hatchMech.arm(HatchArm.IN);
    }

    if (fireHatch) {
      LOGGER.debug("HATCH: Fire hatch!");
      hatchMech.launcher(HatchLauncher.FIRE);
    } else {
      LOGGER.debug("HATCH: Reset hatch launcher.");
      hatchMech.launcher(HatchLauncher.RESET);

      if (moveTurretRight) {
        LOGGER.debug("HATCH: Moving turret to right of hatch side.");
        if (ensureTurretSafeToMove(disableSafety)) {
          turret.target(90.0);
        }
      } else if (moveTurretLeft) {
        LOGGER.debug("HATCH: Moving turret to left of hatch side.");
        if (ensureTurretSafeToMove(disableSafety)) {
          turret.target(-90.0);
        }
      } 
      if (manualTurretMove != 0.0) {
        LOGGER.debug("HATCH: Manually move the turret.");
        if (true) { //ensureTurretSafeToMove()
          LOGGER.warn("turret is safe to move, manualTurret move: {}", box(manualTurretMove));
          turret.manual(RobotMap.INVERT_TURRET_FOR_HATCHMODE * manualTurretMove);
        }
      } else {
        turret.manual(0);
      }
    }

    eitherHatchOrCargoMode(
        disableSafety,
        rejectCargo, 
        intakeCargo, 
        moveTurretToHome, 
        enableAutoTargeting, 
        manualTurretMove);
  }

  void eitherHatchOrCargoMode(
      boolean disableSafety,
      boolean rejectCargo,
      boolean intakeCargo,
      boolean moveTurretToHome,
      boolean enableAutoTargeting,
      double  manualTurretMove
  ) {
    visionController.navigatorFeedback();

    if (rejectCargo && cargoIntake.arm() == CargoIntakeArm.DOWN) {
      //Cargo intake reverses motor to spit cargo.
      LOGGER.debug("HATCH or CARGO: Rejecting ball");
      cargoIntake.roller(CargoIntakeRoller.REJECT);
    } else if (!intakeCargo) {
      LOGGER.debug("HATCH or CARGO: Stopping roller");
      cargoIntake.roller(CargoIntakeRoller.STOP);
    }

    if (moveTurretToHome) {
      LOGGER.debug("HATCH or CARGO: Move turret to home");
      moveTurretHome(disableSafety);
    }

    if (enableAutoTargeting) {
      LOGGER.debug("HATCH or CARGO: Set target lock");
      turret.lockOnTarget();
    }

    if (manualTurretMove == 0.0) {
      turret.manual(0);
    }
  }

  GamePieceMode switchMode(
      boolean defenseMode,
      boolean hatchMode,
      boolean cargoMode) {

    if (defenseMode) { // gets action from driver input
      LOGGER.debug("Changing game mode to DEFENSE");
      cargoIntake.arm(CargoIntakeArm.UP);
      mode = GamePieceMode.DEFENSE;
      camera.unlock();
      led.defensiveMode();
    } else if (hatchMode) {
      LOGGER.info("Changing game mode to HATCH");
      mode = GamePieceMode.HATCH;
      if (camera.totalCameras() >= 4) {
        camera.hatch();
        camera.lock();
      }
      led.hatchMode();
    } else if (cargoMode) {
      LOGGER.info("Changing game mode to CARGO");
      mode = GamePieceMode.CARGO;
      if (camera.totalCameras() >= 4) {
        camera.cargo();
        camera.lock();
      }
      led.cargoMode();
    }

    return mode;
  }

  void updateGamePieces() {
    // Update all systems
    cargoIntake.periodic();
    cargoMech.periodic();
    hatchMech.periodic();
    turret.periodic();
  }

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addStringMetric(name + " Mode", mode::name);
  }

  public void runOnTeleopInit(){
    mode = GamePieceMode.DEFENSE;
  }
}
