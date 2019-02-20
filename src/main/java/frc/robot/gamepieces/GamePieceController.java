package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeArmState;
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
   * This method would be used to override the mode for testing
   * by using Network Tables. It is private as that is the only
   * use method.
   * 
   * @param mode the mode for testing.
   */
  private void testMode(String mode) {
    this.mode = GamePieceMode.valueOf(mode);
  }

  public enum GamePieceMode {
    DEFENSE, 
    CARGO,
    HATCH
  }

  private GamePieceController() {
    driverStation = DriverStation467.getInstance();
    cargoIntake = CargoIntake.getInstance();
    cargoMech = CargoMech.getInstance();
    hatchMech = HatchMechanism.getInstance();
    turret = Turret.getInstance();
    camera = CameraSwitcher.getInstance();    
    visionController = VisionController.getInstance();

    cargoIntake.enabled(true);
    cargoMech.enabled(true);
    hatchMech.enabled(true);
    turret.enabled(true);
    mode = GamePieceMode.DEFENSE;
  }

  /**
   * Checks for states from driverStation.
   */
  public void periodic() {

    // Depending on driver input, camera view switches to front or back.
    // // Does not change the mode away from Hatch or Cargo, but does take camera.
    if (driverStation.getDriveCameraFront()) {
      camera.forward();
    } else if (driverStation.getDriveCameraBack()) {
      camera.backward();
    }

    if (driverStation.defenseMode()) { // gets action from driver input
      mode = GamePieceMode.DEFENSE;
      // TODO: LED Red
    } else if (driverStation.getHatchMode()) {
      mode = GamePieceMode.HATCH;
      camera.hatch();
    } else if (driverStation.getCargoMode()) {
      mode = GamePieceMode.CARGO;
      camera.cargo();
    }

    switch (mode) {

      case DEFENSE:
        if (makeTurretSafeToMove()) {
          turret.moveTurretToHome();
        } else if (turret.isHome()) {
          if (cargoIntake.arm() == CargoIntakeArmState.DOWN) {
            cargoIntake.arm(CargoIntakeArm.UP);
          }
          if (hatchMech.arm() == HatchArm.OUT) {
            hatchMech.arm(HatchArm.IN);
          }
        }
        break;

      case CARGO:
        if (driverStation.getTurretRight()) {
          if (makeTurretSafeToMove()) {
            turret.target(90.0);
          }
        } else if (driverStation.getTurretLeft()) {
          if (makeTurretSafeToMove()) {
            turret.target(-90.0);
          } 
        } else if (driverStation.getAcquireBall()) {
          if (!turret.isHome()) {
            if (makeTurretSafeToMove()) {
              turret.moveTurretToHome();
            }
          } else {
            if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
              cargoMech.wrist(CargoMechWrist.CARGO_BIN);
            } else {
              cargoIntake.roller(CargoIntakeRoller.REVERSE);
              cargoMech.claw(CargoMechClaw.REVERSE); // Suck ball into cargo arm mech
            }
          }
        } else if (driverStation.getCargoArmCargoShipPosition()) {
          cargoMech.claw(CargoMechClaw.STOP);
          cargoMech.wrist(CargoMechWrist.CARGO_SHIP);
          cargoIntake.roller(CargoIntakeRoller.STOP);
        } else if (driverStation.getCargoArmLowRocketShipPosition()) {
          cargoMech.claw(CargoMechClaw.STOP);
          cargoMech.wrist(CargoMechWrist.LOW_ROCKET);
          cargoIntake.roller(CargoIntakeRoller.STOP);
        } else if (driverStation.getFireCargo()) {
          cargoMech.claw(CargoMechClaw.FORWARD);
          cargoIntake.roller(CargoIntakeRoller.STOP);
        } else {
          cargoMech.claw(CargoMechClaw.STOP);
          cargoIntake.roller(CargoIntakeRoller.STOP);
        }
        break;

      case HATCH:
        if (driverStation.getTurretRight()) {
          if (makeTurretSafeToMove()) {
            turret.target(-90.0);
          }
        } else if (driverStation.getTurretLeft()) {
          if (makeTurretSafeToMove()) {
            turret.target(90.0);
          }
        } else {          
          if (driverStation.fireHatch()) {
            hatchMech.launcher(HatchLauncher.FIRE);
          } else {
            hatchMech.launcher(HatchLauncher.RESET);
          }
          if (driverStation.getAcquireHatch()) {
            hatchMech.arm(HatchArm.OUT);
          } else {
            hatchMech.arm(HatchArm.IN);
          }
        }
        break;

      default:
        LOGGER.error("Should always have a game piece mode.");
    }

    // Actions that apply in either cargo or hatch mode
    if ((mode == GamePieceMode.CARGO || mode == GamePieceMode.HATCH)) {
      visionController.navigatorFeedback();

      if (driverStation.getRejectBall() && cargoIntake.arm() == CargoIntakeArmState.DOWN) {
        /*
         * Works in cargo or hatch mode. Cargo intake reverses motor to spit cargo.
         */
        cargoIntake.roller(CargoIntakeRoller.FORWARD);
      }

      if (driverStation.getTurretHome()) {
        /*
         * //TODO: Move Turret Home Must be in hatch or cargo mode. Moves the turret to
         * zero. If arm is in low cargo acquire postion and move is required, move to
         * cargo arm to low rocket Must check that it is safe to move turret. Cancels
         * Target Lock
         */
        if (!turret.isHome()) {
          if (isSafeToMoveTurret()) {
            turret.moveTurretToHome();
          } else {
            cargoMech.wrist(CargoMechWrist.SAFE_TURRET);
          }
        }
      }


      /*
       * //Target Lock Turret Must be in hatch or cargo mode. Set target to
       * track mode. Should allow override if other turret move.
       */
      if (driverStation.getAutoTargetButtonPressed()) {
        turret.lockOnTarget();
      }

      /*
       * //Fine Adjust Turret Manually move the turret based on stick. Should
       * check for unsafe turret situations. Cancels target lock
       */
      if (true) {
        if (isSafeToMoveTurret()) {
          turret.manual(driverStation.getFineAdjustTurret()); // cancel target lock handled here
        } else {
          cargoMech.wrist(CargoMechWrist.SAFE_TURRET);
        }

      }
    } // End combined Hatch and Turret mode capabilities.

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
        && cargoIntake.arm() == CargoIntakeArmState.DOWN) ? true : false;
    LOGGER.debug("Safe to move turret? {}", isSafe);
    return isSafe;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addStringProperty(
        name + "Mode",
        mode::name, // Lambda called when updating network table
        // Lambda calls set enabled if changed in Network table
        (gamePieceMode) -> testMode(gamePieceMode)); 
  }

  private boolean makeTurretSafeToMove() {
    boolean isTurretSafeToMove = isSafeToMoveTurret();
    if (!isTurretSafeToMove) {
      if (cargoIntake.armCommand() == CargoIntakeArm.UP) {
        cargoIntake.arm(CargoIntakeArm.DOWN);
      }
      cargoMech.wrist(CargoMechWrist.SAFE_TURRET);
      hatchMech.arm(HatchArm.IN);
      isTurretSafeToMove = true;
    } 
    return isTurretSafeToMove;
  }
}