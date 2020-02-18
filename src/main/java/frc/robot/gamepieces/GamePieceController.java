package frc.robot.gamepieces;

import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.sensors.LedI2C;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;
import frc.robot.vision.VisionController;
import org.apache.logging.log4j.Logger;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.AbstractLayers.IntakeAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.gamepieces.States.IndexerState;
import frc.robot.gamepieces.States.IntakeState;
import frc.robot.gamepieces.States.ShooterState;
import frc.robot.gamepieces.States.State;
import frc.robot.gamepieces.States.IntakeState;
import frc.robot.gamepieces.States.StateMachine;
import frc.robot.gamepieces.States.IntakeState.IntakerArm;
import frc.robot.gamepieces.States.IntakeState.IntakerRollers;
import frc.robot.RobotMap;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GamePieceController {

  private static GamePieceController instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(GamePieceController.class.getName());

  protected String name = "Game Piece Controller";
  protected String subsystem = "Gamepieces";

  // Game Pieces
  private CameraSwitcher camera;
  // private IntakeAL intake;
  // private IndexerAL IndexerAL;
  // private ShooterAL ShooterAL;

  // Game Pieces' States
  private ShooterAL shooter;
  private IndexerAL indexer;
  private IntakeAL intaker;

  // StateMachine
  private StateMachine shooterSM;
  private StateMachine indexerSM;
  private StateMachine climberSM;
  private IntakeState intake;
  private ShooterState shooterState;

  private DriverStation467 driverStation;
  private VisionController visionController;
  private LedI2C led;
  public boolean RobotAligned = true;// TODO determine where this is set

  // DS controls
  public boolean IndexAuto = false;
  public boolean ShooterAuto = true;
  private boolean armPosition = false; // TODO get inputs from DS class
  private boolean rollerStateIN = false;
  private boolean rollerStateOUT = false;
  public boolean fireWhenReady = false;
  public boolean triggerManual = false;
  public boolean flywheelManual = false;
  public boolean climberDownButtonPressed = false;
  public boolean climberUpButtonPressed = false;
  public boolean climberEnabled = false;
  public double shooterSpeed = 0.9;
  public static double shooterPreviousSpeed;
  public boolean upButtonPressed = false;
  public boolean downButtonPressed = false;
  

  public IndexerMode indexMode;
  public ShooterMode shootMode;

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

  // TODO: get driverstation input and call intaker periodic().

  public enum IndexerMode {
    AUTO, MANUAL
  }

  public enum ShooterMode {
    AUTO, MANUAL
  }

  private GamePieceController() {
    driverStation = DriverStation467.getInstance();
    LOGGER.debug("Initializing driverstation");
    LOGGER.debug("Initializing camera");
    visionController = VisionController.getInstance();
    LOGGER.debug("Initializing vision controller");
    led = LedI2C.getInstance();

    // Enabling game pieces
    LOGGER.debug("Enabling the game pieces.");

    LOGGER.debug("Starting in DEFENSE mode.");

    shooterSM = new StateMachine(ShooterState.Idle);
    indexerSM = new StateMachine(IndexerState.Idle);
    intake = IntakeState.getInstance();

    registerMetrics();
  }

  /**
   * Checks for states from driverStation.
   */
  public void periodic() {

      // Separate reading from driver station from processing state
      // so that tests can manually feed inputs.
      processGamePieceState(driverStation.getDriveCameraFront(), driverStation.getDriveCameraBack());
   
  }

  void processGamePieceState(boolean driveCameraFront, boolean driveCameraRear) {

    // Depending on driver input, camera view switches to front or back.
    // Does not change the mode away from Hatch or Cargo, but does take camera.
    if (driveCameraFront) {
      LOGGER.debug("Forward Camera");
      camera.forward();
    } else if (driveCameraRear) {
      LOGGER.debug("Backward Camera");
      camera.backward();
    }
    updateGamePieces();
  }

  public void updateGamePieces() {
    // Update all systems
    if (RobotMap.HAS_SHOOTER)
      shooterSM.step();

    if (RobotMap.HAS_INDEXER)
      indexerSM.step();

    // roller controls
    if (RobotMap.HAS_INTAKE) {
      if (armPosition) {
        intake.setIntakeArm(IntakerArm.ARM_UP);
      } else {
        intake.setIntakeArm(IntakerArm.ARM_DOWN);
      }

      if (rollerStateIN) {
        intake.setIntakeRoller(IntakerRollers.ROLLERS_IN);
      } else if (rollerStateOUT) {
        intake.setIntakeRoller(IntakerRollers.ROLLERS_OUT);
      } else {
        intake.setIntakeRoller(IntakerRollers.ROLLERS_OFF);
      }

    }
  }

  public enum DriverInput {
    FORCE_TRUE, FORCE_FALSE, FORCE_AUTO_TRUE, FORCE_AUTO_FALSE, USE_DRIVER_INPUT
  }

  DriverInput forceCellsForward = DriverInput.USE_DRIVER_INPUT;
  DriverInput forceCellsReverse = DriverInput.USE_DRIVER_INPUT;
  DriverInput forceToAuto = DriverInput.USE_DRIVER_INPUT;

  public void cellsForward(DriverInput mode) {
    forceCellsForward = mode;
  }

  public void cellsReverse(DriverInput mode) {
    forceCellsReverse = mode;
  }

  public void autoInput(DriverInput mode) {
    forceToAuto = mode;
  }

  public boolean IndexerAuto() {
    boolean auto = false;
    if (forceToAuto == DriverInput.FORCE_AUTO_TRUE) {
      LOGGER.info("Driver pressed forward");
      return auto = true;
    } else {
      if (forceToAuto == DriverInput.FORCE_AUTO_FALSE)
        return auto =false;
    }
    return auto;
  }
  // TODO: put in logic
  public boolean indexerBallsForward() {
    if (forceCellsForward == DriverInput.FORCE_TRUE) {
      LOGGER.info("Driver pressed forward");
      return true;
    } else {
      if (forceCellsForward == DriverInput.FORCE_FALSE)
        return false;
    }
    boolean feed = false;
    if (driverStation.indexerFeed()) {
      return true;
    }
    return feed;
  }

  // TODO: put in logic
  public boolean indexerBallsReverse() {
    if (forceCellsReverse == DriverInput.FORCE_TRUE) {
      LOGGER.info("Driver pressed forward");
      return true;
    } else {
      if (forceCellsReverse == DriverInput.FORCE_FALSE)
        return false;
    }

    boolean reverse = false;
    if (driverStation.indexerReverse()) {
      return true;
    }
    return reverse;
  }

  public void determineShooterSpeed() {
    // math
    if (visionController.hasDistance()) {
      shooterSpeed = ((0.16120202 * visionController.dist() + 65.5092) / 100) * 0.95;
      shooterPreviousSpeed = shooterSpeed;
    } else {
      shooterSpeed = shooterPreviousSpeed;
    }

    // TODO figure out what speeds it will set
  }
  // public boolean shooterLoadingBall() {
  // if (shooterSM.) {

  // }
  // return true;
  // }

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    // telemetry.addStringMetric(name + " Mode", mode::name);
  }

  public void runOnTeleopInit() {
  }

  public void setAutomousFireWhenReady(boolean fire) {
    fireWhenReady = fire;
  }

  public boolean getFireWhenReady() {
    return fireWhenReady;
  }

  public State getShooterState() {
    return shooterState;
  }
}