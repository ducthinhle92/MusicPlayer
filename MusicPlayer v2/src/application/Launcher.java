package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.resource.R;

public class Launcher extends Application {
	public static final int PLAY_SCREEN = 0;
	public static final int LIBRARY_SCREEN = 1;	
	public static final double MIN_WIDTH = 500;
	public static final double MIN_HEIGHT = 400;
	
	public int currentScene = LIBRARY_SCREEN;
	
	private static Launcher instance = null;

	@Override
	public void start(Stage primaryStage) {
		try {
			instance = this;
			FXMLLoader loader = new FXMLLoader(
					R.getLayoutFXML("LibraryScreen"));
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(R.getStyleSheet("application"));	

			primaryStage.setScene(scene);
			primaryStage.setMinWidth(MIN_WIDTH);
			primaryStage.setMinHeight(MIN_HEIGHT);
			
			FXMLController controller = loader.getController();
			controller.setStage(primaryStage);
			controller.manageLayout();
			
			// show it out!
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Launcher getInstance() {
		return instance;
	}
}
