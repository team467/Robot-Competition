///'
/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class for the JavaFX application that displays the robot and field. 
 * It creates and handle the main window with its resources (style, graphics, etc.). 
 */
public class Map extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Starts the window and connects the event handler.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// Load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Map.fxml"));

			// Store the root element so that the controllers can use it
			BorderPane root = (BorderPane) loader.load();

			// Create and style a scene
			Scene scene = new Scene(root, 950, 450);

			// Create the stage with the given title and the previously created scene
			primaryStage.setTitle("Robot Map");
			primaryStage.setScene(scene);

			// Show the GUI
			primaryStage.show();

			// Set the proper behavior on closing the application
			MapController controller = loader.getController();
			primaryStage.setOnCloseRequest((WindowEvent we) -> controller.setClosed());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
