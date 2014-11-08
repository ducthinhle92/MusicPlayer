package application;

import java.io.File;

import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.MapChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLController {
	FileChooser fileChooser = new FileChooser();
	DirectoryChooser folderChooser = new DirectoryChooser();
	List<File> list = null;
	Stage stage;
	List<MediaPlayer> players = new ArrayList<MediaPlayer>();
	MediaView mediaView = null;
	ObservableList<String> items = FXCollections.observableArrayList();
	private Duration duration;
	@FXML
	private ListView<String> listFile;
	@FXML
	private Label fileDetail;
	@FXML
	private Button play, prev, next;
	@FXML
	private Slider volumeSlider, timeSlider;
	@FXML
	private Label playTime;
	@FXML
	private Pane view;

	@FXML
	protected void openFile(ActionEvent event) {
		processOpenFile();
	}

	@FXML
	protected void openFolder(ActionEvent event) {
		processOpenFolder();
	}

	@FXML
	protected void exit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	protected void btnPrev(ActionEvent event) {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		int i = players.indexOf(curPlayer);
		if (i > 0 && i < players.size()) {
			curPlayer.stop();
			MediaPlayer prevPlayer = players
					.get((players.indexOf(curPlayer) - 1) % players.size());
			mediaView.setMediaPlayer(prevPlayer);
			play(prevPlayer);
		}

	}

	@FXML
	protected void btnPlay(ActionEvent event) {
		if ("Pause".equals(play.getText())) {
			mediaView.getMediaPlayer().pause();
			play.setText("Play");
		} else {
			play(mediaView.getMediaPlayer());
			play.setText("Pause");
		}

	}
	
	@FXML
	protected void btnNext(ActionEvent event) {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();
		MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1)
				% players.size());
		mediaView.setMediaPlayer(nextPlayer);
		play(nextPlayer);
	}

	@FXML
	protected void btnStop(ActionEvent event) {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();
		// play.setText("Play");
	}

	@FXML
	protected void btnMute(ActionEvent event) {
		mediaView.getMediaPlayer().setVolume(0);
		volumeSlider.setValue(0);
	}

	protected void play(MediaPlayer play) {
		// TODO Auto-generated method stub
		play.play();
		view.getChildren().clear();
		Group phaseNodes = new Group();
		view.getChildren().add(phaseNodes);
		/*
		 * timeSlider.valueProperty().addListener(new InvalidationListener() {
		 * public void invalidated(Observable ov) { if
		 * (timeSlider.isValueChanging()) { // multiply duration by percentage
		 * calculated by slider position
		 * play.seek(duration.multiply(timeSlider.getValue() / 100.0)); } } });
		 * // volumeSlider.valueProperty().addListener(new
		 * InvalidationListener() { public void invalidated(Observable ov) { if
		 * (volumeSlider.isValueChanging()) {
		 * play.setVolume(volumeSlider.getValue() / 100.0); } } });
		 */
		play.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});
		play.setOnReady(new Runnable() {
			public void run() {
				//play.play();
				duration = play.getMedia().getDuration();
				updateValues();
			}
		});
		play.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		});
		// Set View khi play audio
		play.setAudioSpectrumListener(new AudioSpectrumListener() {

			@Override
			public void spectrumDataUpdate(double timestamp, double duration,
					float[] magnitudes, float[] phases) {
				// TODO Auto-generated method stub
				phaseNodes.getChildren().clear();
				int i = 0;
				int x = 10;
				int y = 150;
				final Random rand = new Random(System.currentTimeMillis());
				for (float phase : phases) {
					int red = rand.nextInt(255);
					int green = rand.nextInt(255);
					int blue = rand.nextInt(255);

					Circle circle = new Circle(10);
					circle.setCenterX(x + i);
					circle.setCenterY(y + (phase * 100));
					circle.setFill(Color.rgb(red, green, blue, .70));
					phaseNodes.getChildren().add(circle);
					i += 5;
				}
			}
		});

	}

	/*
	 * protected void processOpenFile() { // TODO Auto-generated method stub
	 * configureFileChooser(fileChooser); File file =
	 * fileChooser.showOpenDialog(stage); }
	 */
	// Xu li khi mo Open Folder
	protected void processOpenFolder() {
		resetAll();
		File dir = folderChooser.showDialog(stage);
		for (final String file : dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name.endsWith(".mp3"))
					return true;
				else {
					return false;
				}
			}

		})) {
            items.add(file);
			players.add(createPlayer("file:///"
					+ (dir + "\\" + file).replace("\\", "/").replaceAll(" ",
							"%20")));
			if (players.isEmpty()) {
				System.out.println("No audio found in " + dir);
			}
			}
		mediaView = new MediaView(players.get(0));
		play(mediaView.getMediaPlayer());
		listFile.setItems(items);
		}

	// Xu li khi mo Open File
	protected void processOpenFile() {
		// TODO Auto-generated method stub
		resetAll();
		configureFileChooser(fileChooser);
		list = fileChooser.showOpenMultipleDialog(stage);
		if (list != null) {
			// get list items,players
			for (int i = 0; i < list.size(); i++) {
				items.add(list.get(i).getName());
				Media media = new Media(list.get(i).toURI().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				players.add(mediaPlayer);
			}
		}
		mediaView = new MediaView(players.get(0));
		// mediaView.getMediaPlayer().setVolume(0.5);
		play(mediaView.getMediaPlayer());
		listFile.setItems(items);
	}

	public MediaPlayer createPlayer(String src) {
		final MediaPlayer player = new MediaPlayer(new Media(src));
		player.setOnError(new Runnable() {
			public void run() {
				System.out.println("Media error occurred: " + player.getError());
			}
		});
		return player;
	}
   public void resetAll(){
	   if(mediaView!=null) mediaView.getMediaPlayer().stop();
	   players.clear();
	   items.clear();
	  }
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("View Folder");
		fileChooser.setInitialDirectory(new File(System
				.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All", "*.*"),
				new FileChooser.ExtensionFilter("MP3", "*.mp3"));
	}

	public void setStage(Stage primaryStage) {
		stage = primaryStage;
	}

	protected void updateValues() {
		// TODO Auto-generated method stub
		if (playTime != null && timeSlider != null && volumeSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = mediaView.getMediaPlayer()
							.getCurrentTime();
					playTime.setText(formatTime(currentTime, duration));
					timeSlider.setDisable(duration.isUnknown());
					if (!timeSlider.isDisabled()
							&& duration.greaterThan(Duration.ZERO)
							&& !timeSlider.isValueChanging()) {
						timeSlider.setValue(currentTime.divide(duration)
								.toMillis() * 100.0);
					}
					if (!volumeSlider.isValueChanging()) {
						volumeSlider.setValue((int) Math.round(mediaView
								.getMediaPlayer().getVolume() * 100));
					}
				}
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
				- elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60
					- durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours,
						elapsedMinutes, elapsedSeconds, durationHours,
						durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes,
						elapsedSeconds, durationMinutes, durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours,
						elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes,
						elapsedSeconds);
			}
		}
	}
}
