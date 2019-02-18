package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.RobotMap;
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

  private GamePieceMode gamePieceMode;

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

  private double getMode() {
    return this.gamePieceMode.getModeNumber();
  }

  private void setMode(double gamePieceMode) {
    if (gamePieceMode == 0.0) {
      this.gamePieceMode = GamePieceMode.DEFENSE;
    } else if (gamePieceMode == 1.0) {
      this.gamePieceMode = GamePieceMode.CARGO;
    } else if (gamePieceMode == 1.0) {
      this.gamePieceMode = GamePieceMode.HATCH;
    } else {
      this.gamePieceMode = GamePieceMode.DEFENSE;
    }
  }

  public enum GamePieceMode {
    DEFENSE(0.0), CARGO(1.0), HATCH(2.0);

    private double mode;

    GamePieceMode(double mode) {
      this.mode = mode;
    }

    public double getModeNumber() {
      return this.mode;
    }
  }

  private GamePieceController() {
    driverStation = DriverStation467.getInstance();
      cargoIntake = CargoIntake.getInstance();
      cargoMech = CargoMech.getInstance();
      hatchMech = HatchMechanism.getInstance();
      turret = Turret.getInstance();
      camera = CameraSwitcher.getInstance();
    
    gamePieceMode = GamePieceMode.DEFENSE;
    visionController = VisionController.getInstance();
  }

  /**
   * Checks for states from driverStation.
   */
  public void periodic() {

    // Depending on driver input, camera view switches to front or back.
    // // Does not change the mode away from Hatch or Cargo, but does take camera.
    // if (driverStation.getDriveCameraFront()) {
    //   camera.forward();
    // } else if (driverStation.getDriveCameraBack()) {
    //   camera.backward();
    // }

    if (driverStation.defenseMode()) { // gets action from driver input
      /*
       * Enter Defense Mode: Moves turret to home, then lower the arm, and finally
       * lift the roller. The LEDs should blink red while transitioning, and be solid
       * red when in defence mode. Must check that it is safe to move turret. Cancels
       * Target Lock
       */
      gamePieceMode = GamePieceMode.DEFENSE;
      visionController.navigatorFeedback();
    } else if (driverStation.getHatchMode()) {
      /*
       * 1. Switches to hatch mode. 2. LEDs should change to gold. 3. Camera changes
       * to hatch view. Should change to hatch view even if already in hatch mode. 4.
       * Cargo intake arm should go down so that turret can move.
       */
      gamePieceMode = GamePieceMode.HATCH;
      camera.hatch();
      visionController.navigatorFeedback();
    } else if (true) {
      /*
       * 1. Switches to cargo mode. 2. LEDs should change to blue. 3. Camera changes
       * to cargo view. Should change to cargo camera even if already in cargo mode.
       * 4. Cargo intake arm should go down so that turret can move.
       */
      gamePieceMode = GamePieceMode.CARGO;
      camera.cargo();
      visionController.navigatorFeedback();
    }

    switch (gamePieceMode) {
      case DEFENSE:
        turret.moveTurretToHome();
        if (turret.isHome()) {
          if (cargoIntake.arm() == CargoIntakeArmState.DOWN) {
            cargoIntake.arm(CargoIntakeArm.UP);
          }
          if (hatchMech.arm() == HatchArm.OUT) {
            hatchMech.arm(HatchArm.IN);
          }
        }
        break;
      case CARGO:
        if (driverStation.getAcquireBall()) {
          /*
          * Acquire Cargo: - Must be in CARGO mode and roller arm must be DOWN. - Turn on
          * roller, move turret to home, lower down, and turn on claw. - Must check that
          * it is safe to move turret. - Cancels Target Lock
          */
          if (cargoIntake.arm() == CargoIntakeArmState.DOWN) { // If cargo intake arm is down
            cargoIntake.roller(CargoIntakeRoller.REVERSE); // Suck ball into cargo intake mech
            if (turret.isHome() == false && isSafeToMoveTurret()) {
              turret.moveTurretToHome();
            } else {
              // Move cargo mech arm down to pick up cargo
              cargoMech.wrist(CargoMechWrist.CARGO_BIN); 
              cargoMech.claw(CargoMechClaw.REVERSE); // Suck ball into cargo arm mech
            }
          }
        } else if (driverStation.setCargoPos()) {
          // Must be in cargo mode. Stop claw. Move cargo arm to cargo ship height.
          cargoMech.claw(CargoMechClaw.STOP);
          cargoMech.wrist(CargoMechWrist.CARGO_SHIP);

        } else if (driverStation.setCargoPos()) {
          // Must be in cargo mode. Stop claw. Move cargo arm to low rocket height.
          cargoMech.claw(CargoMechClaw.STOP);
          cargoMech.wrist(CargoMechWrist.LOW_ROCKET);

        } else if (driverStation.getFireCargo()) {
          // Must be in cargo mode. Reverses claw motor to throw cargo.
          cargoMech.claw(CargoMechClaw.FORWARD);

        } else {
          cargoMech.claw(CargoMechClaw.STOP);
        }
        /*
        * //TODO: Load Cargo onto arm: Must be in Cargo Mode and roller are must be
        * down. move turret to home, turn on claw, lower arm. Must check that it is
        * safe to move turret. Cancels Target Lock
        */
        break;

      case HATCH:
        if (driverStation.fireHatch()) {
          /*
          * //TODO: Fire Hatch Must be in hatch mode. Pushes cargo arm forward for some
          * count of cycles, then activates hatch solenoids. After trigger is released,
          * moves both hatch and arm soledoids to the retracted position. May need to be
          * combined with acquire hatch to insure that the arm can move forward during
          * acquisition. Could just be before acquire hatch as moves happen at end.
          */
          gamePieceMode = GamePieceMode.HATCH;
          if (gamePieceMode == GamePieceMode.HATCH) {
            if (hatchMech.arm() != HatchArm.IN) {
              hatchMech.arm(HatchArm.OUT);
            }
            if (hatchMech.arm() == HatchArm.OUT) {
              hatchMech.launcher(HatchLauncher.FIRE);
            }
          }

        } else if (driverStation.getAcquireHatch()) {
          /*
          * Acquire Hatch: When acquireing a hatch, the arm should move forward, but the
          * hatch launcher should not fire.
          */
          if (gamePieceMode == GamePieceMode.HATCH) {
            if (hatchMech.arm() == HatchArm.IN) {
              hatchMech.arm(HatchArm.OUT);
            }
            if (hatchMech.launcher() == HatchLauncher.FIRE) {
              hatchMech.launcher(HatchLauncher.RESET);
            }
          }
        } else {
          hatchMech.arm(HatchArm.IN);
          hatchMech.launcher(HatchLauncher.RESET);
        }
        break;

      default:
        LOGGER.error("Should always have a game piece mode.");
    }

    // Actions that apply in either cargo or hatch mode
    if ((gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH)) {
      if (driverStation.getRejectBall() && cargoIntake.arm() == CargoIntakeArmState.DOWN) {
        /*
         * Works in cargo or hatch mode. Cargo intake reverses motor to spit cargo.
         */
        cargoIntake.roller(CargoIntakeRoller.FORWARD);
      }

      if (true) {
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
            cargoMech.wrist(CargoMechWrist.LOW_ROCKET);
            turret.moveTurretToHome();
          }
        }
      }

      /*
       * //TODO: Move Turret Right Must be in hatch or cargo mode. Moves the turret to
       * +90. If arm is in low cargo acquire postion and move is required, move to
       * cargo arm to low rocket Must check that it is safe to move turret. Cancels
       * Target Lock
       */

      if (true) {
        if (gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH) {
          if (cargoMech.wrist() != CargoMechWristState.LOW_ROCKET) {
            cargoMech.wrist(CargoMechWrist.LOW_ROCKET);
            if (isSafeToMoveTurret()) {
              turret.target(90.0);
            }
          }
        }
      }

      /*
       * //TODO: Move Turret Left Must be in hatch or cargo mode. Moves the turret to
       * -90. If arm is in low cargo acquire postion and move is required, move to
       * cargo arm to low rocket Must check that it is safe to move turret. Cancels
       * Target Lock
       */

      if (true) {
        if (gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH) {
          if (cargoMech.wrist() != CargoMechWristState.LOW_ROCKET) {
            cargoMech.wrist(CargoMechWrist.LOW_ROCKET);
            if (isSafeToMoveTurret()) {
              turret.target(-90.0); // target lock handled here
            }
          }
        }
      }

      /*
       * //TODO: Target Lock Turret Must be in hatch or cargo mode. Set target to
       * track mode. Should allow override if other turret move.
       */
      if (gamePieceMode == GamePieceMode.CARGO || gamePieceMode == GamePieceMode.HATCH) {
        turret.lockOnTarget();
      }

      /*
       * //TODO: Fine Adjust Turret Manually move the turret based on stick. Should
       * check for unsafe turret situations. Cancels target lock
       */
      if (true) {
        if (isSafeToMoveTurret()) {
          turret.manual(driverStation.getFineAdjustTurret()); // cancel target lock handled here
        }
      }

      /*
       * //TODO: On Target Cargo Must be in cargo mode. If vision reports on target,
       * blink blue LEDs and rumble nav controller
       */
      if (gamePieceMode == GamePieceMode.CARGO) {
        visionController.navigatorFeedback();
      }

      /*
       * //TODO: On Target Hatch Must be in hatch mode. If vision reports on target,
       * blink gold LEDs and rumble nav controller
       */
      if (gamePieceMode == GamePieceMode.HATCH) {
        visionController.navigatorFeedback();
      }

    }
    // Update all systems
    if(cargoIntake != null)cargoIntake.periodic();
    if(cargoMech != null)cargoMech.periodic();
    if(hatchMech != null) hatchMech.periodic();
    if(turret != null)turret.periodic();
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
     //TODO : FIX THIS!!!!!
    // builder.addDoubleProperty(
    //     name + "Enabled", 
    //     this::getMode, // Lambda called when updating network table
    //     // Lambda calls set enabled if changed in Network table
    //     (gamePieceMode) -> setMode(gamePieceMode)); 
  }
}