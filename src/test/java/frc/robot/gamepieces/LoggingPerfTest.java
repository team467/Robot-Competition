package frc.robot.gamepieces;

import static org.apache.logging.log4j.util.Unbox.box;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import frc.robot.Robot;
import frc.robot.Robot.RobotMode;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.CargoMech.CargoMechClaw;
import frc.robot.gamepieces.CargoMech.CargoMechWrist;
import frc.robot.gamepieces.CargoMech.CargoMechWristState;
import frc.robot.gamepieces.HatchMechanism.HatchArm;
import frc.robot.gamepieces.HatchMechanism.HatchLauncher;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.utilities.PerfTimer;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoggingPerfTest {

  private static Telemetry telemetry;
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(LoggingPerfTest.class.getName());

  private PerfTimer gamepieceTimer;
 
  private static Robot robot;
  private static GamePieceController controller;
  private static HatchMechanism hatch;
  private static CargoIntake intake;
  private static CargoMech cargo;
  private static Turret turret;

  // Inputs normally from driver station

  // Driver camera
  private boolean driveCameraFront; 
  private boolean driveCameraRear;

  // Disable Safety
  private boolean disableSafety;

  // Mode switches
  private boolean defenseMode;
  private boolean cargoMode;
  private boolean hatchMode;

  // Cargo Intake Arm
  private boolean intakeUp;
  private boolean intakeDown;
  
  // Hatch mode inputs
  private boolean acquireHatch;
  private boolean fireHatch;
  
  // Cargo mode inputs
  private boolean acquireCargo;
  private boolean fireCargo;
  private boolean moveCargoWristToCargoShipPosition;
  private boolean moveCargoWristToLowRocketPosition;
  private double  manualWristMove;

  // Cargo intake inputs (either hatch or cargo mode)
  private boolean rejectCargo;
  private boolean intakeCargo;

  // Turret inputs (either hatch or cargo mode)
  private boolean moveTurretRight;
  private boolean moveTurretLeft;
  private boolean moveTurretToHome;
  private boolean enableTargetLock;
  private double  manualTurretMove;

  /**
   * Sets up robot in simulation mode and gets the test object.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.ROBOT_2019);
    Robot.enableSimulator();
    robot = new Robot();
    robot.robotInit();

    controller = GamePieceController.getInstance();
    hatch = HatchMechanism.getInstance();
    intake = CargoIntake.getInstance();
    cargo = CargoMech.getInstance();
    turret = Turret.getInstance();

    hatch.enabled(true);
    intake.enabled(true);
    cargo.enabled(true);
    turret.enabled(true);

    telemetry = Telemetry.getInstance();
    telemetry.robotMode(RobotMode.EXTERNAL_TEST);
  }

  /**
   * Resets the game piece controller before each test.
   */
  @Before
  public void init() {
    
    gamepieceTimer = new PerfTimer();

    // Set input defaults
    // override in individual tests
    driveCameraFront = false; 
    driveCameraRear = false;
    disableSafety = false;
    defenseMode = false;
    cargoMode = false;
    hatchMode = false;
    intakeUp = false;
    intakeDown = false;
    acquireCargo = false;
    fireCargo = false;
    moveCargoWristToCargoShipPosition = false;
    moveCargoWristToLowRocketPosition = false;
    acquireHatch = false;
    fireHatch = false;
    rejectCargo = false;
    intakeCargo = false;
    moveTurretRight = false; 
    moveTurretLeft = false;
    moveTurretToHome = false;
    enableTargetLock = false;
    manualWristMove = 0.0;
    manualTurretMove = 0.0;

    // reset the robot
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION));
    turret.simulatedSensorData(RobotMap.TURRET_HOME_TICKS);
    //intake.arm(CargoIntakeArm.UP); 
    intake.roller(CargoIntakeRoller.STOP);
    cargo.wrist(CargoMechWrist.CARGO_BIN);
    cargo.claw(CargoMechClaw.STOP);
    hatch.arm(HatchArm.IN);
    hatch.launcher(HatchLauncher.RESET);
    defenseMode = true;
    controller.switchMode(defenseMode, hatchMode, cargoMode);
    controller.updateGamePieces(); // Need the game pieces to update their interal state.
    defenseMode = false;
    LOGGER.debug("Reset the game pieces to the initial game state");

    // Verify initial state
    assertTrue(intake.arm() == CargoIntakeArm.UP); 
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);
    
    telemetry.start();
  }
  
  @AfterClass
  public static void closeAll() {
    robot.close();
  }

  @Test
  public void timeNetworkTableVersionTest() {
    int count = 100;
    for (int i = 0; i < count; i++) {
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace(e);
      }
      switch (count % 6) {
        case 0:
          defenseMode = true;
          intakeDown = false;
          break;
        case 3:
          cargoMode = true;
          intakeDown = true;
          break;
        default:
          defenseMode = false;
          cargoMode = false;
      }
  
      gamepieceTimer.startIteration();
      callProcessState();
      gamepieceTimer.endIteration();
    }

    LOGGER.info("Execution times Process: sum={} mean={} jitter={} telemetry mean={}", 
        box(gamepieceTimer.sum()), box(gamepieceTimer.mean()), box(gamepieceTimer.standardDeviation()),
        telemetry.checkTelemetryExecutionPerformance());

    assert(true);
  }

  private void callProcessState() {
    controller.processGamePieceState(
        driveCameraFront, 
        driveCameraRear,
        disableSafety,
        defenseMode, 
        hatchMode, 
        cargoMode,
        intakeUp,
        intakeDown,
        acquireCargo, 
        fireCargo, 
        moveCargoWristToCargoShipPosition, 
        moveCargoWristToLowRocketPosition,
        acquireHatch, 
        fireHatch, 
        rejectCargo, 
        intakeCargo, 
        moveTurretRight, 
        moveTurretLeft, 
        moveTurretToHome, 
        enableTargetLock, 
        manualWristMove, 
        manualTurretMove);
  }



}

