package application.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.MediaFile;
import application.FXMLController;
import application.resource.R;
import application.utility.LyricGatherService;

@SuppressWarnings({ "unchecked" })
public class PlayScreen extends AbstractScreen {

	public static final ObservableList<String> lyric = FXCollections
			.observableArrayList();
	protected static final int MODE_EDIT = 0;
	protected static final int MODE_UPDATE = 1;
	private Button btnShowLyric;
	private StackPane lyricWrapper;
	private SplitPane playPaneSpliter;
	private SplitPane lyricComponent;
	private double dividerPos;
	private ListView<String> listViewLyric;
	private TextField txtTitle;
	private TextField txtArtist;
	private TextArea txtAreaLyric;
	private Button btnEditLyric;
	protected int lyricMode;
	private StackPane lyricBox;
	private Button btnUpdateLyric;
	private boolean updatingLyric = false;
	private LyricGatherService liveUpdater;
	private ObservableList<String> backupLyric;

	public PlayScreen(Stage primaryStage) {
		super(primaryStage);
	}

	@Override
	protected void initialize() {
		 AnchorPane viewPlay = (AnchorPane) findNodeById("viewPlay");
		 String background_play=R.getImage("background_play.jpg");
		 viewPlay.setStyle("-fx-background-image: url('" +background_play
		 +"')");

		playPaneSpliter = (SplitPane) findNodeById("splitPlayPane");
		lyricWrapper = (StackPane) findNodeById("lyricWrapper");
		lyricComponent = (SplitPane) findNodeById("lyricComp");
		lyricBox = (StackPane) findNodeById("lyricBox");

		listViewLyric = (ListView<String>) findNodeById("lvLyric");
		txtTitle = (TextField) findNodeById("txtTitle");
		txtArtist = (TextField) findNodeById("txtArtist");
		btnEditLyric = (Button) findNodeById("btnEditLyric");
		btnUpdateLyric = (Button) findNodeById("btnUpdateLyric");
		btnShowLyric = (Button) findNodeById("btnShowLyric");

		txtAreaLyric = new TextArea();
		lyricMode = MODE_UPDATE;

		listViewLyric.setItems(lyric);
		lyricWrapper.getChildren().clear();
		playPaneSpliter.setDividerPositions(1, 0);
		lyricComponent.setVisible(false);
		dividerPos = playPaneSpliter.getDividerPositions()[0];

		addEventHandler();
	}

	@Override
	public void show() {
		validateLyric();
	}

	private void validateLyric() {
		MediaFile audio = FXMLController.getInstance().getCurrentAudio();
		if (audio != null && lyricComponent.isVisible()) {
			txtTitle.setText(audio.getTitle());
			txtArtist.setText(audio.getArtist());

			String strLyric = audio.getLyric();
			if (strLyric != null && strLyric.length() > 0) {
				loadLyric(strLyric);
			} else {
				lyric.clear();
			}
		}
	}

	private void loadLyric(String strLyric) {
		listViewLyric.getItems().clear();
		String[] lines = strLyric.split("\n");
		for (String line : lines)
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
				btnShowLyric.setLayoutX((newValue.doubleValue() - btnShowLyric
						.getWidth()) / 2);
			}
		});

		btnEditLyric.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (lyricMode == MODE_EDIT) {
					setLyricMode(MODE_UPDATE);
				} else if (lyricMode == MODE_UPDATE) {
					setLyricMode(MODE_EDIT);
				}
			}
		});

		btnUpdateLyric.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(!updatingLyric)
					getLiveLyric();
				else {
					cancelGetLyric();
				}
			}
		});

		txtAreaLyric.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				btnEditLyric.setDisable(false);
			}
		});
	}

	protected void cancelGetLyric() {
		if(liveUpdater != null)
			liveUpdater.cancel();
		if(backupLyric != null) {
			lyric.clear();
			for(String s : backupLyric) lyric.add(s);				
		}			
		finishGetLiveLyric();
	}

	protected void getLiveLyric() {
		String title = txtTitle.getText().trim();
		String artist = txtArtist.getText().trim();
		if(title == null || title.length() == 0
				|| artist == null || artist.length() == 0) {
			return;
		}
		
		// reset lyric box and disable control components
		updatingLyric  = true;
		btnEditLyric.setDisable(true);
		btnUpdateLyric.setText(R.strings.cancel);
		// backup the lyric in case of failure
		if(lyric.size() > 0) {
			backupLyric = FXCollections.observableArrayList();
			for(String s : lyric) backupLyric.add(s);
		} else
			backupLyric = null;
		// now, clear the lyric box
		lyric.clear();
		
		liveUpdater = new LyricGatherService(title, artist, lyric);
		liveUpdater.setOnSucceeded(onSuccessHandler);
		liveUpdater.setOnFailed(onFailedHandler);
		liveUpdater.start();
	}
	
	private EventHandler<WorkerStateEvent> onSuccessHandler = new EventHandler<WorkerStateEvent>() {

		@Override
		public void handle(WorkerStateEvent arg0) {
			System.out.println("Retrieve lyric successfully");
			if(liveUpdater.getValue() == true)
				finishGetLiveLyric();
		}
	};
	private EventHandler<WorkerStateEvent> onFailedHandler = new EventHandler<WorkerStateEvent>() {
		@Override
		public void handle(WorkerStateEvent arg0) {
			System.out.println("Failed to retrieve lyric");
			// recover the last lyric
			if(backupLyric != null) {
				lyric.clear();
				for(String s : backupLyric) lyric.add(s);		
			}			
			finishGetLiveLyric();
		}
	};
	
	private void finishGetLiveLyric() {
		btnUpdateLyric.setText(R.strings.update_lyric);
		btnEditLyric.setDisable(false);
		updatingLyric = false;
	}

	protected void setLyricMode(int mode) {
		lyricMode = mode;
		if (lyricMode == MODE_EDIT) {
			lyricBox.getChildren().clear();
			lyricBox.getChildren().add(txtAreaLyric);
			btnEditLyric.setText(R.strings.save);
			btnEditLyric.setDisable(true);
		} else {
			lyricBox.getChildren().clear();
			lyricBox.getChildren().add(listViewLyric);
			btnEditLyric.setText(R.strings.edit_lyric);
		}
	}

	protected void onClickShowLyric() {
		if (lyricComponent.isVisible()) {
			// hide lyric box
			lyricComponent.setVisible(false);
			lyricWrapper.getChildren().clear();
			dividerPos = playPaneSpliter.getDividerPositions()[0];
			playPaneSpliter.setDividerPositions(1, 0);
		} else {
			// show lyric box
			lyricComponent.setVisible(true);
			playPaneSpliter.setDividerPosition(0, dividerPos);
			lyricWrapper.getChildren().add(lyricComponent);
			validateLyric();
		}
	}
}
