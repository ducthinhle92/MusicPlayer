package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.MediaFile;
import model.MediaInfo;
import model.PlayList;
import application.config.Config;
import application.controller.LibraryScreen;
import application.controller.PlayScreen;
import application.resource.R;
import application.view.listener.MediaListener;

public class FXMLController {

	public static final int PLAY_SCREEN = 0;
	public static final int LIBRARY_SCREEN = 1;	
	
	private static FXMLController instance = null;

	private StackPane bodyPane;
	private Parent libraryPane;
	private Parent playPane;
	private PlayScreen playScreen;
	private LibraryScreen libraryScreen;
	private Stage stage;

	private int currentScreen = LIBRARY_SCREEN;
	
	ObservableList<PlayList> items2 = FXCollections.observableArrayList();
	ObservableList<MediaInfo> mediaFiles = FXCollections.observableArrayList();
	
	// FXML components
	@FXML
	public ListView<String> listFile;
	@FXML
	public Label fileDetail;
	@FXML
	public Button play, prev, next, btn_saveList, btn_clearList, btnChangeScene,mute,stop;
	@FXML
	public TextField txtPlaylistName;
	@FXML
	public Slider volumeSlider, timeSlider;
	@FXML
	public Label playTime, lbInfo;
	@FXML
	public StackPane nowPlayingPane;
	@FXML
	public VBox mainBackground;
	@FXML
	public Pane controlPane;

	final ObservableList<Integer> ratingSample = FXCollections
			.observableArrayList(1, 2, 3, 4, 5);
	private ArrayList<MediaListener> mediaListeners;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void initialize() throws ClassNotFoundException, SQLException {
		instance = this;
		mediaListeners = new ArrayList<MediaListener>();
	}
	
	@FXML
	protected void openFile(ActionEvent event) throws ClassNotFoundException,
			SQLException {
		libraryScreen.processOpenFile();
	}
	
	@FXML
	protected void onChangeScene(ActionEvent event) {
		if(currentScreen == LIBRARY_SCREEN) {		
			setScreen(PLAY_SCREEN);
		}
		else {			
			setScreen(LIBRARY_SCREEN);
		}
	}

	@FXML
	protected void btnPlay(ActionEvent event) {
		libraryScreen.onClickPlay();
	}

	@FXML
	protected void btnNext(ActionEvent event) {
		libraryScreen.onClickNext();		
	}

	@FXML
	protected void btnPrev(ActionEvent event) {
		libraryScreen.onClickPrev();		
	}

	@FXML
	protected void btnStop(ActionEvent event) {
		libraryScreen.onClickStop();
	}

	@FXML
	protected void btnMute(ActionEvent event) {
		libraryScreen.onClickMute();
	}

	@FXML
	protected void onSaveList(ActionEvent event) throws ClassNotFoundException,
			SQLException {
		libraryScreen.onSaveList();
	}

	@FXML
	protected void onClearList(ActionEvent event) {
		libraryScreen.onClearList();
	}
	
	@FXML
	protected void onExit() {
		Config.getInstance().dispose();
	}

	public void processOpenList(List<MediaInfo> playlist) {
		libraryScreen.processOpenPlayList(playlist);
	}

	public void setStage(Stage primaryStage) {
		stage = primaryStage;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {			
			@Override
			public void handle(WindowEvent event) {
				onExit();
			}
		});
	}

	public void manageLayout() {
		try {
			bodyPane = (StackPane) stage.getScene().lookup("#bodyPane");
			libraryPane = (Parent) stage.getScene().lookup("#libraryPane");

			FXMLLoader loader = new FXMLLoader(R.getLayoutFXML("PlayPane"));
			playPane = (Parent) loader.load();
			playScreen = new PlayScreen(stage);
			libraryScreen = new LibraryScreen(stage);

			// default screen is library
			setScreen(LIBRARY_SCREEN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setScreen(int screenId) {
		if(screenId == PLAY_SCREEN) {			
			bodyPane.getChildren().clear();
			bodyPane.getChildren().add(playPane);
			currentScreen = PLAY_SCREEN;
			playScreen.start();
		}
		else if(screenId == LIBRARY_SCREEN) {
			bodyPane.getChildren().clear();
			bodyPane.getChildren().add(libraryPane);
			currentScreen = LIBRARY_SCREEN;
			libraryScreen.start();
		}
	}

	public int getCurrentScreen() {
		return currentScreen;
	}

	public static FXMLController getInstance() {
		return instance;
	}
	
	public LibraryScreen getLibraryScreen(){
		return libraryScreen;
	}

	public MediaFile getCurrentMedia() {		
		return libraryScreen.getCurrentMedia();
	}

	public void onMediaChanged() {
		for(MediaListener ml : mediaListeners) {
			ml.onMediaChanged();
		}
	}

	public void addMediaListener(MediaListener listener) {
		mediaListeners.add(listener);
	}
}
