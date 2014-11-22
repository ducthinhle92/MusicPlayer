package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import application.controller.PlayScreen;
import application.resource.R;

public class Launcher extends Application {
	public static final int PLAY_SCENE = 0;
	public static final int LIBRARY_SCENE = 1;	
	public static final double MIN_WIDTH = 500;
	public static final double MIN_HEIGHT = 400;
	
	public int currentScene = LIBRARY_SCENE;
	public Stage stage;
	
	private static Launcher instance = null; 
	private StackPane bodyPane;
	private Parent libraryPane;
	private Parent playPane;
	private PlayScreen playScreen;

	@Override
	public void start(Stage primaryStage) {
		try {
			instance = this;
			this.stage = primaryStage;
			FXMLLoader loader = new FXMLLoader(
					R.getLayoutFXML("LibraryScreen"));
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(
					R.getStyleSheet("application"));	
			FXMLController controller = loader.getController();
			controller.setStage(primaryStage);

			primaryStage.setMinWidth(MIN_WIDTH);
			primaryStage.setMinHeight(MIN_HEIGHT);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			bodyPane = (StackPane) scene.lookup("#bodyPane");
			libraryPane = (Parent) scene.lookup("#libraryPane");
			
			loader = new FXMLLoader(R.getLayoutFXML("PlayPane"));
			playPane = (Parent) loader.load();

			playScreen = new PlayScreen(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setScene(int sceneId) {
		if(sceneId == PLAY_SCENE) {			
			bodyPane.getChildren().clear();
			bodyPane.getChildren().add(playPane);
			currentScene = PLAY_SCENE;
			playScreen.start();
		}
		else if(sceneId == LIBRARY_SCENE) {
			bodyPane.getChildren().clear();
			bodyPane.getChildren().add(libraryPane);
			currentScene = LIBRARY_SCENE;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Launcher getInstance() {
		return instance;
	}
}
