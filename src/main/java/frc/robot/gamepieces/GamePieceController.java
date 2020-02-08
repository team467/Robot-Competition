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
import frc.robot.gamepieces.States.ShooterState;
import frc.robot.gamepieces.States.IntakeState;
import frc.robot.gamepieces.States.StateMachine;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GamePieceController {

  private static GamePieceController instance = null;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceController.class.getName());

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

  //StateMachine
  private StateMachine shooterSM;
  private StateMachine indexerSM;
  private StateMachine climberSM;

  private DriverStation467 driverStation;
  private VisionController visionController;
  private LedI2C led;

  //DS controls 
  private boolean armPosition;
  private boolean rollerState;
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

  //TODO: get driverstation input and call intaker periodic().
  
  public enum GamePieceMode {
    AUTOMODE, DEFENSE, 
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

    mode = GamePieceMode.DEFENSE;

    shooterSM = new StateMachine(ShooterState.Idle);
    shooterSM = new StateMachine(IndexerState.Idle);

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
        driverStation.getIndexerAutoMode()
    );

  }

  void processGamePieceState(
      boolean driveCameraFront,
      boolean driveCameraRear,
      boolean getIndexerAutoMode) {

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
    shooterSM.step();
    indexerSM.step();
  }

  // TODO: put in logic
  public boolean indexerBallsForward() {
    return false;
  }

  // TODO: put in logic 
  public boolean indexerBallsReverse() {

    return false;
  }

  public boolean indexerForward() {
    return false;
  }

  public boolean indexerBackwards() {
    return false;
  }

  public boolean indexerAutoMode() {
    driverStation.getIndexerAutoMode();
    return true;
  }

  // public boolean shooterLoadingBall() {
  //   if (shooterSM.) {

  //   }
  //   return true;
  // }

  private void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addStringMetric(name + " Mode", mode::name);
  }

  public void runOnTeleopInit(){
  }
}