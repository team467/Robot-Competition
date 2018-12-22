package frc.robot.simulator.gui;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import frc.robot.simulator.communications.RobotData;
import frc.robot.simulator.draw.FieldShape;
import frc.robot.simulator.draw.PowerCubeShape;
import frc.robot.simulator.draw.RobotShape;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the buttons and drawing
 * the field and robot.
 *
 */
public class MapController {

  /**
   * The overall window, including the buttons.
   */
  @FXML
  private BorderPane mapUserInterface;

  /**
   * The field pane, it needs to be separate from the canvas to allow overlays.
   */
  @FXML
  private BorderPane fieldMap;

  /**
   * The field window pane.
   */
  @FXML
  private Canvas field;

  /**
   * The overlay window pane for the robot.
   */
  @FXML
  private Pane robotArea;

  /**
   * The start button for beginning the match.
   */
  @FXML
  private Button startButton;

  /**
   * The select box for choosing the drive mode (i.e. autonomous vs. teleop).
   */
  @FXML
  private ChoiceBox<String> driveMode;

  /**
   * The select box for choosing the autonomous mode.
   */
  @FXML
  private ChoiceBox<String> teamColor;

  /**
   * The select box for choosing the autonomous mode.
   */
  @FXML
  private ChoiceBox<String> gameSpecificMessage;

  /**
   * The select box for choosing the autonomous mode.
   */
  @FXML
  private ChoiceBox<String> autonomousMode;

  /**
   * Text fields to be similar to Driver Station inputs.
   */
  @FXML
  private TextField dbString0;
  @FXML
  private TextField dbString1;
  @FXML
  private TextField dbString2;
  @FXML
  private TextField dbString3;
  @FXML
  private TextField dbString4;
  @FXML
  private TextField dbString5;
  @FXML
  private TextField dbString6;
  @FXML
  private TextField dbString7;
  @FXML
  private TextField dbString8;
  @FXML
  private TextField dbString9;

  /**
   * The timer for redrawing the robot after it moves.
   */
  private ScheduledExecutorService timer;

  /**
   * Flag to start getting the robot's moves.
   */
  private boolean robotActive;

  /**
   * The shapes for drawing for robot and field and other objects.
   */
  private RobotShape robotShape = new RobotShape();
  private Group robotGroup = null; // for adding and remove robot on map
  private FieldShape fieldShape = new FieldShape();
  private ArrayList<PowerCubeShape> cubes = new ArrayList<PowerCubeShape>();

  /**
   * Initialize method, automatically called by @{link FXMLLoader}.
   */
  public void initialize() {

    robotActive = false;
    fieldShape.context(field.getGraphicsContext2D());

    double redSwitchCubeOffsetX = 85.25; //next to red alliance station
    double redSwitchCubeOffsetY = 196;

    for (int i = 0; i < 6; i++) {
      cubes.add(new PowerCubeShape(redSwitchCubeOffsetX + i * 2.34 * 12.0, redSwitchCubeOffsetY)); 
      // 1.25' in between each cube ; y-coordinate is same for 6 cubes
    }

    double blueSwitchCubeOffsetX = 85.25; //next to blue alliance station
    double blueSwitchCubeOffsetY = 439.2;

    for (int i = 0; i < 6; i++) {
      cubes.add(
          new PowerCubeShape(blueSwitchCubeOffsetX + i * 2.34 * 12.0, blueSwitchCubeOffsetY)); 
      // 1.25' in between each cube ; y-coordinate is same for 6 cubes
    }

    for (PowerCubeShape cube : cubes) {
      robotArea.getChildren().add(cube.createPowerCube());
    }

    // Default the choice boxes to the first values
    driveMode.getSelectionModel().selectFirst();
    autonomousMode.getSelectionModel().selectFirst();
    gameSpecificMessage.getSelectionModel().selectFirst();
    teamColor.getSelectionModel().selectFirst();

    // Set Defaults for Smart Dashboard Buttons
    loadDataFromSmartDashboardIntoSimulator();
    enforceFloatingPoint(dbString0);
    enforceFloatingPoint(dbString1);
    enforceFloatingPoint(dbString2);
    enforceFloatingPoint(dbString3);
    enforceFloatingPoint(dbString4);
    enforceFloatingPoint(dbString5);
    enforceFloatingPoint(dbString6);
    enforceFloatingPoint(dbString7);
    enforceFloatingPoint(dbString8);
    enforceFloatingPoint(dbString9);

    // Temp, set values -- should use some sort of load from RobotMap or elsewhere saved
    dbString0.setText("5.0");
    dbString1.setText("0.0004");
    dbString2.setText("0.0");
    dbString3.setText("0.012");
    dbString4.setText("0.0");
    dbString5.setText("0.0");
    dbString6.setText("0.0004");
    dbString7.setText("0.0");
    dbString8.setText("0.012");
    dbString9.setText("0.0");
    loadSimulatedDataOntoSmartDashboard();

    // Draw static components
    Platform.runLater(() -> {
      drawFieldComponents();
    });
  }

  private void enforceFloatingPoint(TextField textField) {
    // Add control to make sure value is floating point
    textField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, 
          String oldValue, String newValue) {
          if (!newValue.matches("^[+-]?\\d{0,7}([\\.]\\d{0,4})?")) {
            textField.setText(oldValue);
          } else {
            SmartDashboard.putString(textField.getId(), textField.getText());
          }
      }
    });
  }  

  /**
   * Temporary, should probably move to RobotMap. This is used for the 
   * simulator PID controllers, including in tests.
   */
  public void loadSimulatedDataOntoSmartDashboard() {
    SmartDashboard.putString("DB/String 0", dbString0.getText()); // Tune Distance
    SmartDashboard.putString("DB/String 1", dbString1.getText()); // P Left
    SmartDashboard.putString("DB/String 2", dbString2.getText()); // Tune Distance
    SmartDashboard.putString("DB/String 3", dbString3.getText()); // D Left
    SmartDashboard.putString("DB/String 4", dbString4.getText()); // F Left
    SmartDashboard.putString("DB/String 5", dbString5.getText()); // F Left
    SmartDashboard.putString("DB/String 6", dbString6.getText()); // P Right
    SmartDashboard.putString("DB/String 7", dbString7.getText()); // F Left
    SmartDashboard.putString("DB/String 8", dbString8.getText()); // D Right
    SmartDashboard.putString("DB/String 9", dbString9.getText()); // F Right
  }

  private void loadDataFromSmartDashboardIntoSimulator() {
    dbString0.setText(SmartDashboard.getString("DB/String 0", ""));    
    dbString1.setText(SmartDashboard.getString("DB/String 1", ""));    
    dbString2.setText(SmartDashboard.getString("DB/String 2", ""));    
    dbString3.setText(SmartDashboard.getString("DB/String 3", ""));    
    dbString4.setText(SmartDashboard.getString("DB/String 4", ""));    
    dbString5.setText(SmartDashboard.getString("DB/String 5", ""));    
    dbString6.setText(SmartDashboard.getString("DB/String 6", ""));    
    dbString7.setText(SmartDashboard.getString("DB/String 7", ""));    
    dbString8.setText(SmartDashboard.getString("DB/String 8", ""));    
    dbString9.setText(SmartDashboard.getString("DB/String 9", ""));    
  }


  /**
   * The action triggered by pushing the button on the GUI. It creates a thread that monitors
   * robot movements
   */
  @FXML
  protected void startRobot() {

    if (driveMode.getValue() != null) {
      SimulatedData.driveMode = driveMode.getValue();
    } else {
      SimulatedData.driveMode = "Disabled";
    }

    if (autonomousMode.getValue() != null) {
      SimulatedData.autoMode = autonomousMode.getValue();
    } else {
      SimulatedData.autoMode = "None";
    }

    if (gameSpecificMessage.getValue() != null) {
      SimulatedData.gameSpecificMessage = gameSpecificMessage.getValue();
    } else {
      SimulatedData.gameSpecificMessage = "LLL";
    }

    if (teamColor.getValue() != null) {
      if (teamColor.getValue().equalsIgnoreCase("Red")) {
        SimulatedData.teamColor = Alliance.Red;
      } else {
        SimulatedData.teamColor = Alliance.Blue;
      }
    } else {
      SimulatedData.teamColor = Alliance.Red;
    }

    if (!robotActive) {

      if (robotGroup != null) {
        robotArea.getChildren().remove(robotGroup);
        robotShape = new RobotShape();
      }
      robotGroup = robotShape.createRobotShape();
      robotArea.getChildren().add(robotGroup);

      robotActive = true;
      robotShape.init();
      update();

      // The robot runs it's cycle every 20 ms
      Runnable simulatedPeriodic = new Runnable() {

        @Override
        public void run() {
          update();
        }
      };

      timer = Executors.newSingleThreadScheduledExecutor();
      timer.scheduleAtFixedRate(simulatedPeriodic, 0, 
          RobotMap.ITERATION_TIME_MS, TimeUnit.MILLISECONDS);

      // update the button content
      startButton.setText("Stop");

    } else {
      // the robot is not active at this point
      robotActive = false;
      RobotData.getInstance().zero();
      robotShape.zero();

      // update the button content
      startButton.setText("Start");

      // stop the timer
      stopRobot();
    }
  }

  /**
   * Stop the robot thread.
   */
  private void stopRobot() {
    if (timer != null && !timer.isShutdown()) {
      try {
        // stop the timer
        timer.shutdown();
        timer.awaitTermination(RobotMap.ITERATION_TIME_MS, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        // log any exception
        System.err.println("Exception in stopping the robot simulation..." + e);
      }
    }
  }

  /**
   * Periodically updates the robot and field shape.
   */
  public void update() {
    Platform.runLater(() -> {
      drawFieldComponents();
      robotShape.draw();
    });
  }

  private void drawFieldComponents() {
    fieldShape.draw();
    for (PowerCubeShape cube : cubes) {
      cube.draw();
    }
  }

  /**
   * On application close, stop the threads and release any resources.
   */
  protected void setClosed() {
    stopRobot();
  }

}