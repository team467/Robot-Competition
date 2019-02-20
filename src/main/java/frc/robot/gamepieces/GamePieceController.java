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
    LOGGER.info("Initializing driverstation");
    cargoIntake = CargoIntake.getInstance();
    LOGGER.info("Initializing cargoIntake");
    cargoMech = CargoMech.getInstance();
    LOGGER.info("Initializing cargoMech");
    hatchMech = HatchMechanism.getInstance();
    LOGGER.info("Initializing turret");
    turret = Turret.getInstance();
    camera = CameraSwitcher.getInstance();
    LOGGER.info("Initializing camera");
    visionController = VisionController.getInstance();
    LOGGER.info("Initializing visionController");

    // Enabling game pieces
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
    // Does not change the mode away from Hatch or Cargo, but does take camera.
    if (driverStation.getDriveCameraFront()) {
      LOGGER.debug("Forward Camera");
      camera.forward();
    } else if (driverStation.getDriveCameraBack()) {
      LOGGER.debug("Backward Camera");
      camera.backward();
    }

    if (driverStation.defenseMode()) { // gets action from driver input
      LOGGER.info("Changing game mode to DEFENSE");
      mode = GamePieceMode.DEFENSE;
      // TODO: LED Red
    } else if (driverStation.getHatchMode()) {
      LOGGER.info("Changing game mode to HATCH");
      mode = GamePieceMode.HATCH;
      camera.hatch();
    } else if (driverStation.getCargoMode()) {
      LOGGER.info("Changing game mode to CARGO");
      mode = GamePieceMode.CARGO;
      camera.cargo();
    }

    switch (mode) {

      case DEFENSE:
        if (moveTurretHome()) {
          if (hatchMech.arm() == HatchArm.OUT) {
            LOGGER.info("Moving hatch arm in");
            hatchMech.arm(HatchArm.IN);
          }
          if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
            if (ensureSafeToMoveWrist()) {
              cargoMech.wrist(CargoMechWrist.CARGO_BIN);
            }
          } else if (cargoIntake.armCommand() == CargoIntakeArm.DOWN) {
            LOGGER.info("Moving cargo intake arm up");
            cargoIntake.arm(CargoIntakeArm.UP);
          }
        }
        break;

      case CARGO:
        if (driverStation.getTurretRight()) {
          if (ensureTurretSafeToMove()) {
            turret.target(-90.0);
          }
        } else if (driverStation.getTurretLeft()) {
          if (ensureTurretSafeToMove()) {
            turret.target(90.0);
          }
        } 

        if (driverStation.getAcquireBall()) {
          LOGGER.info("Trying to acquire ball");
          if (cargoIntake.armCommand() == CargoIntakeArm.DOWN) {
            LOGGER.info("Cargo intake arm is down");
            if (moveTurretHome()) { // Overrides move turret right or left
              if (cargoMech.wrist() != CargoMechWristState.CARGO_BIN) {
                if (ensureSafeToMoveWrist()) {
                  LOGGER.info("Moving wrist to bin");
                  cargoMech.wrist(CargoMechWrist.CARGO_BIN);
                }
              } else {
                LOGGER.info("Putting rollers in reverse");
                cargoMech.claw(CargoMechClaw.REVERSE);
                cargoIntake.roller(CargoIntakeRoller.REVERSE); // Suck ball into cargo intake mech
              }
            } 
          }
        } else if (driverStation.getCargoArmCargoShipPosition()) {
          LOGGER.debug("Stop the claw.");
          cargoMech.claw(CargoMechClaw.STOP);
          cargoIntake.roller(CargoIntakeRoller.STOP);
          LOGGER.info("Move Cargo Mech wrist to the Cargo Ship height.");
          if (ensureSafeToMoveWrist()) {
            cargoMech.wrist(CargoMechWrist.CARGO_SHIP);
          }
        } else if (driverStation.getCargoArmLowRocketShipPosition()) {
          LOGGER.info("Stop the claw.");
          cargoMech.claw(CargoMechClaw.STOP);
          cargoIntake.roller(CargoIntakeRoller.STOP);
          if (ensureSafeToMoveWrist()) {
            LOGGER.info("Move Cargo Mech wrist to the Low Rocket height.");
            cargoMech.wrist(CargoMechWrist.LOW_ROCKET);
          }
        } else if (driverStation.getFireCargo()) {
          LOGGER.info("FIRING BALL.");
          cargoMech.claw(CargoMechClaw.FORWARD);
        } else {
          cargoMech.claw(CargoMechClaw.STOP);
          cargoIntake.roller(CargoIntakeRoller.STOP);
        }

        /*
         * Manual Override of the wrist check to make sure the roller is dd
         */
        if (driverStation.getWristManualOverride() != 0.0) {
          if (ensureSafeToMoveWrist()) {
            LOGGER.debug("Setting wrist speed to {}.", driverStation.getWristManualOverride());
            cargoMech.overrideArm(driverStation.getWristManualOverride());
          }
        }

        break;

      case HATCH:
        if (driverStation.getAcquireHatch()) {
          LOGGER.debug("Moving the hatch arm out.");
          hatchMech.arm(HatchArm.OUT);
        } else {
          LOGGER.debug("Moving hatch arm in.");
          hatchMech.arm(HatchArm.IN);
        }

        if (driverStation.fireHatch()) {
          LOGGER.info("Fire hatch!");
          hatchMech.launcher(HatchLauncher.FIRE);
        } else {
          LOGGER.info("Reset hatch launcher.");
          hatchMech.launcher(HatchLauncher.RESET);

          if (driverStation.getTurretRight()) {
            if (ensureTurretSafeToMove()) {
              turret.target(90.0);
            }
          } else if (driverStation.getTurretLeft()) {
            if (ensureTurretSafeToMove()) {
              turret.target(-90.0);
            }
          } 
        }    
        break;

      default:
        LOGGER.error("Should always have a game piece mode.");
    }

    // Actions that apply in either cargo or hatch mode
    if ((mode == GamePieceMode.CARGO || mode == GamePieceMode.HATCH)) {
      visionController.navigatorFeedback();

      if (driverStation.getRejectBall() && cargoIntake.armCommand() == CargoIntakeArm.DOWN) {
        /*
         * Works in cargo or hatch mode. Cargo intake reverses motor to spit cargo.
         */
        LOGGER.info("Rejecting ball");
        cargoIntake.roller(CargoIntakeRoller.FORWARD);
      }
  
      if (driverStation.getIntakeBall() && cargoIntake.armCommand() == CargoIntakeArm.DOWN) {
        LOGGER.info("Grabbing ball");
        cargoIntake.roller(CargoIntakeRoller.REVERSE);
      }

      if (driverStation.getTurretHome()) {
        moveTurretHome();
      }

      if (driverStation.getAutoTargetButtonPressed()) {
        turret.lockOnTarget();
      }

      if (driverStation.getFineAdjustTurret() != 0.0) {
        if (ensureTurretSafeToMove()) {
          turret.manual(driverStation.getFineAdjustTurret()); // cancel target lock handled here
        }
      }

    } // End if ((mode == GamePieceMode.CARGO || mode == GamePieceMode.HATCH))

    // Update all systems
    cargoIntake.periodic();
    cargoMech.periodic();
    hatchMech.periodic();
    turret.periodic();
    
  }

  private boolean ensureSafeToMoveWrist() {
    boolean isSafe = (cargoIntake.armCommand() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move wrist? {}", isSafe);
    if (!isSafe) {
      cargoIntake.arm(CargoIntakeArm.DOWN);
    }
    return isSafe;
  }

  private boolean ensureTurretSafeToMove() {
    boolean isSafe = (cargoMech.isSafeToMoveTurret() 
        && cargoIntake.armCommand() == CargoIntakeArm.DOWN) ? true : false;
    LOGGER.debug("Safe to move turret? {}", isSafe);
    if (!isSafe) {
      if (ensureSafeToMoveWrist()) {
        LOGGER.info("Moving wrist to safe position.");
        cargoMech.wrist(CargoMechWrist.SAFE_TURRET);
      }
      hatchMech.arm(HatchArm.IN);
    } 
    return isSafe;
  }

  private boolean moveTurretHome() {
    boolean isHome = turret.isHome();
    if (!isHome) {
      if (ensureTurretSafeToMove()) {
        LOGGER.info("Moving Turret Home.");
        turret.moveTurretToHome();
      }
    }
    return isHome;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    // Lambda called when updating network table
    builder.addStringProperty(name + "Mode", mode::name,
        // Lambda calls set enabled if changed in Network table
        (gamePieceMode) -> testMode(gamePieceMode));
  }

}