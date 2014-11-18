package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
	public static final int PLAY_SCENE = 0;
	public static final int LIBRARY_SCENE = 1;	
	public static final double MIN_WIDTH = 500;
	public static final double MIN_HEIGHT = 400;
	
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
			
			loader = new FXMLLoader(getClass().getResource(
					"PlayScreen.fxml"));
			root = (Parent) loader.load();
			playScene = new Scene(root);
			playScene.getStylesheets().add(Launcher.getStyleSheet(
					"application.css"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setScene(int sceneId) {
		if(sceneId == PLAY_SCENE) {
			stage.setScene(playScene);
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
