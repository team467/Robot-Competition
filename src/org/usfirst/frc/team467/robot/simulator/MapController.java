package org.usfirst.frc.team467.robot.simulator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the histogram creation.
 * 
 */
public class MapController {
	
	public static final double PIXELS_PER_MAP_INCH = 1;
	
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
	
	// Robot Shapes
	private Group robotShape = new Group();
	private Rectangle chassisShape = null;
	private Rectangle elevatorShape = null;

	// Network Tables
	RobotData data = RobotData.getInstance();
	
	Robot robot;	
	boolean runLocal = true;
	
	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize() {
		if (runLocal) {
			robot = new Robot();
			robot.robotInit();
		}
		this.robotActive = false;
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startRobot() {
		
		if (!this.robotActive) {
			
			createRobotShape();
			robotActive = true;
			if (runLocal) {
				robot.autonomousInit();
			} else {
				data.startClient();
			}
			updateRobot();
			
			// The robot runs it's cycle every 20 ms
			Runnable simulatedPeriodic = new Runnable() {

				@Override
				public void run() {
					if (runLocal) {
						robot.autonomousPeriodic();
					}
					updateRobot();
				}
			};

			this.timer = Executors.newSingleThreadScheduledExecutor();
			this.timer.scheduleAtFixedRate(simulatedPeriodic, 0, 1, TimeUnit.MILLISECONDS);

			// update the button content
			this.startButton.setText("Stop");
			
		} else {
			// the camera is not active at this point
			robotActive = false;
			drawField();
			
			robotArea.getChildren().remove(robotShape);	
			
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
	
	public void createRobotShape() {

		chassisShape = new Rectangle(Robot.LENGTH*12/2, Robot.WIDTH*12 , Color.DARKSLATEGREY);
		chassisShape.relocate(fieldOffsetY, fieldOffsetX);

		elevatorShape = new Rectangle(Robot.LENGTH*12/2, (Robot.WIDTH*12 - 4), Color.WHITESMOKE);
		elevatorShape.relocate(fieldOffsetY + (Robot.LENGTH/2) * 12, (fieldOffsetX + 2));
		
		robotShape.setBlendMode(BlendMode.SRC_OVER);
		robotShape.getChildren().add(chassisShape);
		robotShape.getChildren().add(elevatorShape);
		
		robotArea.getChildren().add(robotShape);
	}
	
	public void updateRobot() {
		if(!runLocal) {
			data.receive();
		}
		drawField();
		drawRobot(data.heading(), data.leftX(), data.leftY());
	}
	
	public double fieldOffsetY = 10.0 * 12.0;
	public double fieldOffsetX =  1.5 * 12.0;
	
	public void drawRobot(double heading, double leftX, double leftY) {
		Platform.runLater(() -> {
			robotShape.relocate((fieldOffsetY + (leftY - Robot.LENGTH/2) * 12),
					(fieldOffsetX + leftX * 12));
			robotShape.setRotate(Math.toDegrees(heading));
		});
	}
	
	/**
	 * Draws the field 
	 * Field sizes in width x height feet 
	 * 	Field: 29' x 74' (27' x 54" internal)
	 * 	Alliance Station: 22x10
	 * 	Auto Line: 27x2 (10 feet from alliance walls)
	 *  Exchange zone: 4x3 (2 inch tape)
	 *    Exchange hole is 1' 9" wide
	 *  Null Territory: 7ft 11.25in x 6 (2 inch tape)
	 *  Platform Zone: 11' 1.5" x 9' 11.75" [2' tape alliance color]
	 *  Portal: 4' x 12' 11"
	 *    10' on short width
	 *  Wall 1' 6" wide?
	 *  Power Cube Zone: 3' 9" x 3' 6"
	 *  Starting Line: 27' x 2" White tape, 2 ' 6" behind alliance wall
	 *  Player Station: 5' 9" x 1'
	 *  Scale 
	 *    15' from end to end
	 *    Plate is 3' x 4'
	 *    Platform top is 8' 8" x 3' 5.25"
	 *    Ramp is 1' 1"
	 *  Switch
	 *    14' from Alliance STation
	 *    Plates are 3 ' x 4'
	 *    Switch is 12' wide
	 *  Portal 
	 *    1' 2" square opening
	 *  Power cube is 1' 1" x 1' 1" x 11"
	 */
	public void drawField() {
				
		// Need to transfer actions to the JavaFX thread to be run
		Platform.runLater(() -> {
			GraphicsContext context = field.getGraphicsContext2D();
			context.setStroke(Color.YELLOW);
			context.setLineWidth(2.0 * PIXELS_PER_MAP_INCH);
			
			// Field
			context.setFill(Color.DARKGREY);	
			context.fillRect(
					 0.0, 
					 0.0, 
					74.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					30.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Red Alliance 
			context.setFill(Color.CRIMSON);
			
			// Red Alliance Station
			context.fillRect(
					 0.0,
					 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					10.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					22.0 * 12.0 * PIXELS_PER_MAP_INCH);

//			// Red Exchange Zone
//			context.fillRect(
//					 0.0,
//					 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
//					10.0 * 12.0 * PIXELS_PER_MAP_INCH, 
//					22.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Red Switch
			context.setFill(Color.LIGHTGRAY);
			context.fillRect(
			24.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			12.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.setFill(Color.DIMGRAY);
			context.fillRect(
			24.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.fillRect(
			24.0 * 12.0 * PIXELS_PER_MAP_INCH,
			18.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Blue Alliance
			context.setFill(Color.CORNFLOWERBLUE);

			// Blue Alliance Station
			context.fillRect(
					64.0 * 12.0 * PIXELS_PER_MAP_INCH,
					 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					10.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					22.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Blue Switch
			context.setFill(Color.LIGHTGRAY);
			context.fillRect(
			46.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			12.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.setFill(Color.DIMGRAY);
			context.fillRect(
			46.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.fillRect(
			46.0 * 12.0 * PIXELS_PER_MAP_INCH,
			18.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Center Line
			context.setFill(Color.DIMGREY);
			context.fillRect(
					(37.0 * 12.0 - 1.0) * PIXELS_PER_MAP_INCH,
					  1.5 * 12.0		* PIXELS_PER_MAP_INCH, 
					  2.0 				* PIXELS_PER_MAP_INCH, 
					 27.0 * 12.0        * PIXELS_PER_MAP_INCH);
			
			// Wall Line
			context.setFill(Color.LIGHTGREY);
			context.fillRect(
					(12.0 * 12.0 + 11) 							* PIXELS_PER_MAP_INCH,
					( 1.5 * 12.0 - 2.0)							* PIXELS_PER_MAP_INCH, 
					((74.0 * 12.0) - (12.0 * 12.0 + 11) * 2) 	* PIXELS_PER_MAP_INCH, 
					 2.0 			   							* PIXELS_PER_MAP_INCH);
			
			context.fillRect(
					(12.0 * 12.0 + 11) 							* PIXELS_PER_MAP_INCH,
					 28.5 * 12.0								* PIXELS_PER_MAP_INCH, 
					((74.0 * 12.0) - (12.0 * 12.0 + 11) * 2) 	* PIXELS_PER_MAP_INCH, 
					 2.0 			   							* PIXELS_PER_MAP_INCH);
			
			
			
		}); // End JavaFX thread transfer

	}


	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}
	
	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property
	 *            a {@link ObjectProperty}
	 * @param value
	 *            the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value)
	{
		Platform.runLater(() -> {
			property.set(value);
		});
	}


}