package org.usfirst.frc.team467.robot.simulator.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.usfirst.frc.team467.robot.simulator.draw.FieldShape;
import org.usfirst.frc.team467.robot.simulator.draw.RobotShape;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the histogram creation.
 * 
 */
public class MapController {
	
	@FXML
	private BorderPane MapUI;
	
	@FXML
	private BorderPane fieldMap;
	
	@FXML
	private Canvas field;
	
	@FXML
	private Pane robotArea;
	
	// the FXML button
	@FXML
	private Button startButton;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;

	// a flag to start autonomous
	private boolean robotActive;
	
	// Draw Shapes
	RobotShape robotShape = new RobotShape();
	FieldShape fieldShape = new FieldShape();
	
	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize() {
		this.robotActive = false;
		fieldShape.context(field.getGraphicsContext2D());
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startRobot() {
		
		if (!this.robotActive) {
			
			robotArea.getChildren().add(robotShape.createRobotShape());
			
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
	 * Stop the acquisition from the camera and release all the resources
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
	
	public void update() {
		Platform.runLater(() -> {
			fieldShape.draw();
			robotShape.draw();
		});

	}
	
	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}
	
}