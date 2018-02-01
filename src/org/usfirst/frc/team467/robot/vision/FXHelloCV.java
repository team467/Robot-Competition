package org.usfirst.frc.team467.robot.vision;
	
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class FXHelloCV extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// Load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FXHelloCV.fxml"));

			// Store the root element so that the controllers can use it
			BorderPane rootElement = (BorderPane)FXMLLoader.load(getClass().getResource("FXHelloCV.fxml"));

			// Create and style a scene
			Scene scene = new Scene(rootElement, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// Create the stage with the given title and the previously created scene.
			primaryStage.setTitle("JavaFX meets OpenCV");
			primaryStage.setScene(scene);
			
			// Show the GUI
			primaryStage.show();

			// Set the proper behavior on closing the application
			FXHelloCVController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					if (controller != null) {
						controller.setClosed();
					}
				}
			}));

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		// Load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
}
