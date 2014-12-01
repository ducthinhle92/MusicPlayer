package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.MediaFile;
import model.MediaInfo;
import model.PlayList;
import application.controller.LibraryScreen;
import application.controller.PlayScreen;
import application.resource.R;

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
	public Button play, prev, next, btn_saveList, btn_clearList, btnGoPlayScene,mute,stop;
	@FXML
	public TextField txtPlaylistName;
	@FXML
	public Slider volumeSlider, timeSlider;
	@FXML
	public Label playTime;
	@FXML
	public StackPane nowPlayingPane;

	final ObservableList<Integer> ratingSample = FXCollections
			.observableArrayList(1, 2, 3, 4, 5);

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
//		Image img_prev=new Image(getClass().getResourceAsStream("resource/image/img_prev.png"));
//		prev.setGraphic(new ImageView(img_prev));
//		prev.setBackground(null);prev.setPadding(new Insets(0,0,0,0));
//		Image img_next=new Image(getClass().getResourceAsStream("resource/image/img_next.png"));
//		next.setGraphic(new ImageView(img_next));
//		next.setBackground(null);next.setPadding(new Insets(0,0,0,0));
//		Image img_pause=new Image(getClass().getResourceAsStream("resource/image/img_pause.png"));
//		play.setGraphic(new ImageView(img_pause));
//		play.setBackground(null);play.setPadding(new Insets(0,0,0,0));
//		Image img_sound=new Image(getClass().getResourceAsStream("resource/image/img_sound.png"));
//		mute.setGraphic(new ImageView(img_sound));mute.setBackground(null);
//		Image img_stop=new Image(getClass().getResourceAsStream("resource/image/img_stop.png"));
//		stop.setGraphic(new ImageView(img_stop));stop.setBackground(null);
	}
	
	@FXML
	protected void openFile(ActionEvent event) throws ClassNotFoundException,
			SQLException {
		libraryScreen.processOpenFile();
	}

	@FXML
	protected void openFolder(ActionEvent event) {
		libraryScreen.processOpenFolder();
	}

	@FXML
	protected void exit(ActionEvent event) {
		System.exit(0);
	}
	
	@FXML
	protected void onChangeScene(ActionEvent event) {
		if(currentScreen == LIBRARY_SCREEN) {
			btnGoPlayScene.setText(R.strings.goPlayScene);			
			setScreen(PLAY_SCREEN);
		}
		else {
			btnGoPlayScene.setText(R.strings.goLibraryScene);			
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

	public void processOpenList(List<MediaInfo> playlist) {
		libraryScreen.processOpenPlayList(playlist);
	}

	public void setStage(Stage primaryStage) {
		stage = primaryStage;
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

	public MediaFile getCurrentAudio() {		
		return libraryScreen.getCurrentAudio();
	}
}
