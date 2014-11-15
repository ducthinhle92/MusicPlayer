package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private static final double MIN_WIDTH = 500;
	private static final double MIN_HEIGHT = 400;

	@Override
	public void start(Stage primaryStage) {
		try {
			 FXMLLoader loader = new FXMLLoader(getClass().getResource("LibraryScreen.fxml"));
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			FXMLController controller = loader.getController();
			controller.setStage(primaryStage);

			primaryStage.setMinWidth(MIN_WIDTH);
			primaryStage.setMinHeight(MIN_HEIGHT);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
