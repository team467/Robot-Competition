/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 * 
 * This application handles a video stream and can convert its frames in gray
 * scale or color. Moreover, for each frame, it shows the corresponding
 * histogram and it is possible to add a logo in a corner of the video.
 * 
 */
public class Map extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// Load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Map.fxml"));
			
			// Store the root element so that the controllers can use it
			BorderPane root = (BorderPane) loader.load();
			
			// Create and style a scene
			Scene scene = new Scene(root, 950, 450);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// Create the stage with the given title and the previously created scene
			primaryStage.setTitle("Robot Map");
			primaryStage.setScene(scene);

			// Show the GUI
			primaryStage.show();

			// Set the proper behavior on closing the application
			MapController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void drawShapes(GraphicsContext context) {
//		context.setFill(Color.GREEN);
//		context.setStroke(Color.BLUE);
//		context.setLineWidth(5);
//		context.strokeLine(40,  10, 10, 40);
//		context.fillOval(10, 60, 30, 30);
//		context.strokeOval(60, 60, 30, 30);
//		context.fillRoundRect(110, 60, 30, 30, 10, 10);
//		context.strokeRoundRect(160, 60, 30, 30, 10, 10);
//		context.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
//		context.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
//		context.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
//		context.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
//		context.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
//		context.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
//		context.fillPolygon(new double[]{10, 40, 10, 40},
//                new double[]{210, 210, 240, 240}, 4);
//		context.strokePolygon(new double[]{60, 90, 60, 90},
//                  new double[]{210, 210, 240, 240}, 4);
//		context.strokePolyline(new double[]{110, 140, 110, 140},
//                   new double[]{210, 210, 240, 240}, 4);
//	}

}
