package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
	public static final int PLAY_SCENE = 0;
	public static final int LIBRARY_SCENE = 1;
	
	private static final double MIN_WIDTH = 500;
	private static final double MIN_HEIGHT = 400;
	private static Launcher instance = null;
	private Scene libraryScene;
	private Scene playScene;
	public Stage stage;

	@Override
	public void start(Stage primaryStage) {
		try {
			instance = this;
			this.stage = primaryStage;
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"LibraryScreen.fxml"));
			Parent root = (Parent) loader.load();
			libraryScene = new Scene(root);
			libraryScene.getStylesheets().add(getStyleSheet("appliccation.css"));			
			FXMLController controller = loader.getController();
			controller.setStage(primaryStage);

			primaryStage.setMinWidth(MIN_WIDTH);
			primaryStage.setMinHeight(MIN_HEIGHT);
			primaryStage.setScene(libraryScene);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setScene(int sceneId) {
		if(sceneId == PLAY_SCENE) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"PlayScreen.fxml"));
			Parent root;
			try {
				root = (Parent) loader.load();

				playScene = new Scene(root);
				playScene.getStylesheets().add(Launcher.getStyleSheet("application.css"));
				stage.setScene(playScene);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(sceneId == LIBRARY_SCENE) {
			stage.setScene(libraryScene);
		}
	}

	public static String getStyleSheet(String string) {
		return instance.getClass().getResource("application.css")
				.toExternalForm();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Launcher getInstance() {
		return instance;
	}
}
