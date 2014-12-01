package application.controller;

import model.MediaFile;
import application.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@SuppressWarnings({"unchecked"})
public class PlayScreen extends AbstractScreen {

	public static final ObservableList<String> lyric = 
	        FXCollections.observableArrayList();
	private Button btnShowLyric;
	private StackPane lyricWrapper;
	private SplitPane playPaneSpliter;
	private SplitPane lyricContent;
	private double dividerPos;
	private ListView<String> lyricBox;
	private TextField txtTitle;
	private TextField txtArtist;

	public PlayScreen(Stage primaryStage) {
		super(primaryStage);
	}
	
	@Override
	protected void initialize() {
//		AnchorPane viewPlay = (AnchorPane) findNodeById("viewPlay");
//		String background_play=R.getImage("background_play.jpg");
//		viewPlay.setStyle("-fx-background-image: url('" +background_play +"')");
		for(int i=0; i<50; i++)
			lyric.add("Thịnh");
		
		btnShowLyric = (Button) findNodeById("btnShowLyric");
		playPaneSpliter = (SplitPane) findNodeById("splitPlayPane");
		lyricWrapper = (StackPane) findNodeById("lyricPane");
		lyricContent = (SplitPane) findNodeById("lyricSplitPane");
		lyricBox = (ListView<String>) findNodeById("lyricBox");
		txtTitle = (TextField) findNodeById("txtTitle");
		txtArtist = (TextField) findNodeById("txtTitle");
		
		lyricBox.setItems(lyric);
		lyricWrapper.getChildren().clear();
		playPaneSpliter.setDividerPositions(1, 0);
		lyricContent.setVisible(false);
		dividerPos = playPaneSpliter.getDividerPositions()[0];
		
		addEventHandler();
	}
	
	@Override
	public void show() {
		validateLyric();
	}

	private void validateLyric() {
		MediaFile audio = FXMLController.getInstance().getCurrentAudio();
		if(audio != null && lyricContent.isVisible()) {
			System.out.println("validating lyric");
			txtTitle.setText(audio.getTitle());
			txtArtist.setText(audio.getArtist());
			
			String strLyric = audio.getLyric();
			if(strLyric != null && strLyric.length() > 0) {
				loadLyric(strLyric);
			}
			else {
				lyric.clear();
			}
		}
	}

	private void loadLyric(String strLyric) {
		lyricBox.getItems().clear();
		String[] lines = strLyric.split("\n");
		for(String line : lines)
			lyric.add(line);
	}

	private void addEventHandler() {
		btnShowLyric.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				onClickShowLyric();
			}
		});
		
		lyricWrapper.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				btnShowLyric.setLayoutX(
						(newValue.doubleValue() - btnShowLyric.getWidth())/2);
			}
		});
	}

	protected void onClickShowLyric() {
		if(lyricContent.isVisible()) {
			// hide lyric box
			lyricContent.setVisible(false);
			lyricWrapper.getChildren().clear();
			dividerPos = playPaneSpliter.getDividerPositions()[0];
			playPaneSpliter.setDividerPositions(1, 0);
		}
		else {
			// show lyric box
			lyricContent.setVisible(true);
			playPaneSpliter.setDividerPosition(0, dividerPos);
			lyricWrapper.getChildren().add(lyricContent);
			validateLyric();
		}
	}
}
