package org.usfirst.frc.team467.robot.simulator.gui;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.usfirst.frc.team467.robot.simulator.draw.FieldShape;
import org.usfirst.frc.team467.robot.simulator.draw.PowerCubeShape;
import org.usfirst.frc.team467.robot.simulator.draw.RobotShape;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
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
	 * The overall window, including the buttons
	 */
	@FXML
	private BorderPane mapUI;
	
	/**
	 * The field pane, it needs to be separate from the canvas to allow overlays
	 */
	@FXML
	private BorderPane fieldMap;
	
	/**
	 * The field window pane
	 */
	@FXML
	private Canvas field;
	
	/**
	 * The overlay window pane for the robot
	 */
	@FXML
	private Pane robotArea;
	
	/**
	 * The start button for beginning the match
	 */
	@FXML
	private Button startButton;

	/**
	 * The timer for redrawing the robot after it moves
	 */
	private ScheduledExecutorService timer;

	/**
	 * Flag to start getting the robot's moves
	 */
	private boolean robotActive;
	
	/**
	 * The shapes for drawing for robot and field and other objects
	 */
	private RobotShape robotShape = new RobotShape();
	private FieldShape fieldShape = new FieldShape();
	private ArrayList<PowerCubeShape> cubes = new ArrayList<PowerCubeShape>();
	
	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize() {
		this.robotActive = false;
		fieldShape.context(field.getGraphicsContext2D());
		
		double redSwitchCubeOffsetX = 85.25; //next to red alliance station
		double redSwitchCubeOffsetY = 196;
		
			for (int i = 0; i < 6; i++) {
				cubes.add(new PowerCubeShape(redSwitchCubeOffsetX + i * 2.34 * 12.0, redSwitchCubeOffsetY)); // 1.25' in between each cube ; y-coordinate is same for 6 cubes
			}
	
		double blueSwitchCubeOffsetX = 85.25; //next to blue alliance station
		double blueSwitchCubeOffsetY = 439.2;
			
			for (int i = 0; i < 6; i++) {
				cubes.add(new PowerCubeShape(blueSwitchCubeOffsetX + i * 2.34 * 12.0, blueSwitchCubeOffsetY)); // 1.25' in between each cube ; y-coordinate is same for 6 cubes
			}
			
	
	}
		
		

	/**
	 * The action triggered by pushing the button on the GUI. It creates a thread that monitors
	 * robot movements 
	 */
	@FXML
	protected void startRobot() {
		
		if (!this.robotActive) {
				
			robotArea.getChildren().add(robotShape.createRobotShape());
			for (PowerCubeShape cube : cubes) {
				robotArea.getChildren().add(cube.createPowerCube());
			}
			
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

			this.timer = Executors.newSingleThreadScheduledExecutor();
			this.timer.scheduleAtFixedRate(simulatedPeriodic, 0, 1, TimeUnit.MILLISECONDS);

			// update the button content
			this.startButton.setText("Stop");
			
		} else {
			// the camera is not active at this point
			robotActive = false;
			
			// update the button content
			this.startButton.setText("Start");
			
			// stop the timer
			stopAcquisition();
		}
	}

	/**
	 * Stop the robot thread
	 */
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(20, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the robot simulation..." + e);
			}
		}
	}
	
	/**
	 * Periodically updates the robot and field shape
	 */
	public void update() {
		Platform.runLater(() -> {
			fieldShape.draw();
		
			for (PowerCubeShape cube : cubes) {
				cube.draw();
			}
			
			robotShape.draw();
		});

	}
	
	/**
	 * On application close, stop the threads and release any resources.
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}
	
}