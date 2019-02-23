package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.CargoMech.CargoMechClaw;
import frc.robot.gamepieces.CargoMech.CargoMechWrist;
import frc.robot.gamepieces.CargoMech.CargoMechWristState;
import frc.robot.gamepieces.HatchMechanism.HatchArm;
import frc.robot.gamepieces.HatchMechanism.HatchLauncher;
import frc.robot.logging.RobotLogManager;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;
import frc.robot.vision.VisionController;

import org.apache.logging.log4j.Logger;

public class GamePieceController implements Sendable {

  private static GamePieceController instance = null;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceController.class.getName());

  protected String name = "Game Piece Controller";
  protected String subsystem = "Gamepieces";

  // Game Pieces
  private CargoIntake cargoIntake;
  private CargoMech cargoMech;
  private HatchMechanism hatchMech;
  private Turret turret;
  private CameraSwitcher camera;

  private DriverStation467 driverStation;
  private VisionController visionController;

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

  // This methods are required for the Sendable interface

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
    cargoMech = CargoMech.getInstance();
    LOGGER.debug("Initializing cargoMech");
    hatchMech = HatchMechanism.getInstance();
    LOGGER.debug("Initializing hatch mechanism");
    turret = Turret.getInstance();
    LOGGER.debug("Initializing turret");
    camera = CameraSwitcher.getInstance();
    LOGGER.debug("Initializing camera");
    visionController = VisionController.getInstance();
    LOGGER.debug("Initializing vision controller");

    // Enabling game pieces
    LOGGER.debug("Enabling the game pieces.");
    cargoIntake.enabled(true);
    cargoMech.enabled(true);
    hatchMech.enabled(true);
    turret.enabled(true);

    LOGGER.debug("Starting in DEFENSE mode.");
    mode = GamePieceMode.DEFENSE;
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
        driverStation.getDefenseMode(),
        driverStation.getHatchMode(),
        driverStation.getCargoMode(),
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
      boolean defenseMode,
      boolean hatchMode,
      boolean cargoMode,
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
        activateDefenseMode();
        break;

      case CARGO:
        cargoMode(
            acquireCargo,
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

  private boolean ensureSafeToMoveWrist() {
    boolean isSafe = (cargoIntake.arm() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move wrist? {}", isSafe);
    if (!isSafe) {
      LOGGER.debug("Moving cargo intake arm down so wrist can move.");
      cargoIntake.arm(CargoIntakeArm.DOWN);
    }
    return isSafe;
  }

  private boolean ensureTurretSafeToMove() {
    boolean isSafe = (cargoMech.isSafeToMoveTurret() 
        && cargoIntake.arm() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move turret? {}", isSafe);
    if (!isSafe) {
      if (ensureSafeToMoveWrist()) {
        LOGGER.debug("Moving wrist to safe position.");
        cargoMech.wrist(CargoMechWrist.SAFE_TURRET);
      }
      LOGGER.debug("Making sure hatch mechanism arm is in so turret can move.");
      hatchMech.arm(HatchArm.IN);
    } 
    return isSafe;
  }

  private boolean moveTurretHome() {
    boolean isHome = turret.isHome();
    if (!isHome) {
      LOGGER.debug("Moving turret home.");
      if (ensureTurretSafeToMove()) {
        turret.moveTurretToHome();
      }
    } else {
      LOGGER.debug("Turret doesn't need to move to be at home.");
    }
    return isHome;
  }

  void activateDefenseMode() {
    if (moveTurretHome()) {
      if (hatchMech.arm() == HatchArm.OUT) {
        LOGGER.debug("DEFENSE: Move hatch in.");
        hatchMech.arm(HatchArm.IN);
      }
      if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
        if (ensureSafeToMoveWrist()) {
          LOGGER.debug("DEFENSE: Move cargo wrist to bin.");
          cargoMech.wrist(CargoMechWrist.CARGO_BIN);
        }
      } else if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
        LOGGER.debug("DEFENSE: Moving cargo intake arm up, final step.");
        cargoIntake.arm(CargoIntakeArm.UP);
      }
    }
  }

  void cargoMode(
      boolean acquireCargo,
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
      LOGGER.debug("CARGO: Trying to acquire cargo");
      if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
        LOGGER.debug("CARGO: Cargo intake arm is down");
        if (moveTurretHome()) { // Overrides move turret right or left
          if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
            if (ensureSafeToMoveWrist()) {
              LOGGER.debug("CARGO: Moving wrist to bin");
              cargoMech.wrist(CargoMechWrist.CARGO_BIN);
            }
          } else {
            LOGGER.debug("CARGO: Putting rollers in reverse");
            cargoMech.claw(CargoMechClaw.INTAKE);
            cargoIntake.roller(CargoIntakeRoller.INTAKE); // Suck ball into cargo intake mech
          }
        } 
      } else {
        cargoIntake.arm(CargoIntakeArm.DOWN);
      }
    } else if (moveCargoWristToCargoShipPosition) {
      LOGGER.debug("CARGO: Stopping cargo roller and claw");
      cargoMech.claw(CargoMechClaw.STOP);
      cargoIntake.roller(CargoIntakeRoller.STOP);
      LOGGER.debug("CARGO: Move Cargo Mech wrist to the Cargo Ship height.");
      if (ensureSafeToMoveWrist()) {
        cargoMech.wrist(CargoMechWrist.CARGO_SHIP);
      }
    } else if (moveCargoWristToLowRocketPosition) {
      LOGGER.debug("CARGO: Stopping cargo roller and claw");
      cargoMech.claw(CargoMechClaw.STOP);
      cargoIntake.roller(CargoIntakeRoller.STOP);
      if (ensureSafeToMoveWrist()) {
        LOGGER.info("CARGO: Move Cargo Mech wrist to the Low Rocket height.");
        cargoMech.wrist(CargoMechWrist.LOW_ROCKET);
      }
    } else if (fireCargo) {
      LOGGER.debug("CARGO: FIRING BALL!");
      cargoMech.claw(CargoMechClaw.FIRE);
    } else {
      LOGGER.debug("CARGO: Stopping cargo roller and claw");
      cargoMech.claw(CargoMechClaw.STOP);
      cargoIntake.roller(CargoIntakeRoller.STOP);
    }

    /*
     * Manual Override of the wrist check to make sure the roller is dd
     */
    if (manualWristMove != 0.0) {
      if (ensureSafeToMoveWrist()) {
        LOGGER.debug("CARGO: Setting wrist speed to {} manually.", manualWristMove);
        cargoMech.manualWristMove(manualWristMove);
      }
    }

    // Turret moves are last as turret safety takes precidence over other wrist moves
    if (moveTurretRight) {
      if (ensureTurretSafeToMove()) {
        LOGGER.debug("CARGO: Moving turret to right of cargo side.");
        turret.target(-90.0);
      }
    } else if (moveTurretLeft) {
      if (ensureTurretSafeToMove()) {
        LOGGER.debug("CARGO: Moving turret to left of cargo side.");
        turret.target(90.0);
      }
    }

    eitherHatchOrCargoMode(
        rejectCargo, 
        intakeCargo, 
        moveTurretToHome, 
        enableAutoTargeting, 
        manualTurretMove);
  }

  void hatchMode(
      boolean acquireHatch,
      boolean fireHatch,
      boolean rejectCargo,
      boolean intakeCargo,
      boolean moveTurretRight,
      boolean moveTurretLeft,
      boolean moveTurretToHome,
      boolean enableAutoTargeting,
      double  manualTurretMove) {

    if (acquireHatch) {
      LOGGER.debug("HATCH: Moving the hatch arm out.");
      hatchMech.arm(HatchArm.OUT);
    } else {
      LOGGER.debug("HATCH: Moving hatch arm in.");
      hatchMech.arm(HatchArm.IN);
    }

    if (fireHatch) {
      LOGGER.info("HATCH: Fire hatch!");
      hatchMech.launcher(HatchLauncher.FIRE);
    } else {
      LOGGER.info("HATCH: Reset hatch launcher.");
      hatchMech.launcher(HatchLauncher.RESET);

      if (moveTurretRight) {
        LOGGER.debug("HATCH: Moving turret to right of hatch side.");
        if (ensureTurretSafeToMove()) {
          turret.target(90.0);
        }
      } else if (moveTurretLeft) {
        LOGGER.debug("HATCH: Moving turret to left of hatch side.");
        if (ensureTurretSafeToMove()) {
          turret.target(-90.0);
        }
      } 
    }
    eitherHatchOrCargoMode(
        rejectCargo, 
        intakeCargo, 
        moveTurretToHome, 
        enableAutoTargeting, 
        manualTurretMove);
  }

  void eitherHatchOrCargoMode(
      boolean rejectBall,
      boolean intakeBall,
      boolean moveTurretToHome,
      boolean enableAutoTargeting,
      double  manualTurretMove
  ) {
    visionController.navigatorFeedback();

    // Cargo arm needs to go down in both cargo and hatch modes.
    if (cargoIntake.arm() != CargoIntakeArm.DOWN) {
      cargoIntake.arm(CargoIntakeArm.DOWN);
    }

    if (rejectBall && cargoIntake.arm() == CargoIntakeArm.DOWN) {
      //Works in cargo or hatch mode. Cargo intake reverses motor to spit cargo.
      LOGGER.debug("HATCH or CARGO: Rejecting ball");
      cargoIntake.roller(CargoIntakeRoller.REJECT);
    }

    if (intakeBall && cargoIntake.arm() == CargoIntakeArm.DOWN) {
      LOGGER.debug("HATCH or CARGO: Grabbing ball");
      cargoIntake.roller(CargoIntakeRoller.INTAKE);
    }

    if (moveTurretToHome) {
      LOGGER.debug("HATCH or CARGO: Move turret to home");
      moveTurretHome();
    }

    if (enableAutoTargeting) {
      LOGGER.debug("HATCH or CARGO: Set target lock");
      turret.lockOnTarget();
    }

    if (manualTurretMove != 0.0) {
      LOGGER.debug("HATCH or CARGO: Manually move the turret.");
      if (ensureTurretSafeToMove()) {
        turret.manual(manualTurretMove);
      }
    }
  }

  GamePieceMode switchMode(
      boolean defenseMode,
      boolean hatchMode,
      boolean cargoMode) {

    if (defenseMode) { // gets action from driver input
      LOGGER.info("Changing game mode to DEFENSE");
      mode = GamePieceMode.DEFENSE;
      // TODO: LED Red
    } else if (hatchMode) {
      LOGGER.info("Changing game mode to HATCH");
      mode = GamePieceMode.HATCH;
      camera.hatch();
    } else if (cargoMode) {
      LOGGER.info("Changing game mode to CARGO");
      mode = GamePieceMode.CARGO;
      camera.cargo();
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

  @Override
  public void initSendable(SendableBuilder builder) {
    // Lambda called when updating network table
    builder.addStringProperty(name + "Mode", mode::name,
        // Lambda calls set enabled if changed in Network table
        (gamePieceMode) -> testMode(gamePieceMode));
  }

}