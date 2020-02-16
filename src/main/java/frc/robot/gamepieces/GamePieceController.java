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

  private DriverStation467 driverStation;
  private VisionController visionController;
  private LedI2C led;
  public boolean RobotAligned = true;// TODO determine where this is set

  // DS controls
  public boolean IndexAuto = true;
  public boolean ShooterAuto = true;
  private boolean armPosition = false; // TODO get inputs from DS class
  private boolean rollerStateIN = false;
  private boolean rollerStateOUT = false;
  public boolean fireWhenReady = false;
  public boolean triggerManual = false;
  public boolean flywheelManual = false;
  public double shooterSpeed = 0.1;
  public static double shooterPreviousSpeed;

  public IndexerMode indexMode;
  public ShooterMode shootMode;

  private State currentState;

  // Shooter States
  private State shooterState;

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

    if (IndexAuto) {
      indexMode = IndexerMode.AUTO;
    } else {
      indexMode = IndexerMode.MANUAL;
    }

    if (ShooterAuto) {
      shootMode = ShooterMode.AUTO;
    } else {
      shootMode = ShooterMode.MANUAL;
    }

    updateStates();

    // Separate reading from driver station from processing state
    // so that tests can manually feed inputs.
    processGamePieceState(driverStation.getDriveCameraFront(), driverStation.getDriveCameraBack());

  }

  public void updateStates() {
    shooterState = shooterSM.getCurrentState();

  }

  public void processGamePieceState(boolean driveCameraFront, boolean driveCameraRear) {

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


    //roller controls
    if(RobotMap.HAS_INTAKE) {
      if(armPosition) {
        intake.setIntakeArm(IntakerArm.ARM_UP);
      } else {
        intake.setIntakeArm(IntakerArm.ARM_DOWN);
      }
    }
  }

  public boolean indexerBallsForward() {
    if (IndexerAL.calledForward) {
      IndexerAL.calledForward = false;
      return true;
    }
    return false;
  }

  public boolean indexerBallsReverse() {
    if (IndexerAL.calledReverse) {
      IndexerAL.calledReverse = false;
      return true;
    }
    return false;
  }

  public boolean indexerAutoMode() {
    driverStation.getIndexerAutoMode();
    return true;
  }

  public void determineShooterSpeed() {
    // math
    if (visionController.hasDistance()) {
      shooterSpeed = (0.16120202 * visionController.dist() + 65.5092) / 100;
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

  public State getShooterState(){
    return shooterState;
  }
}