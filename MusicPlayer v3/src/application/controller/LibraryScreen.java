package application.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
import model.MediaFile;
import model.MediaInfo;
import application.DatabaseController;
import application.FXMLController;
import application.resource.R;
import application.utility.Utils;
import application.view.MediaTreeView;
import application.view.NowPlayingListView;
import application.view.PlaylistTable;
import application.view.listener.ListViewListener;
import application.view.listener.TableListener;
import application.view.listener.TreeViewListener;

@SuppressWarnings({ "deprecation" })
public class LibraryScreen extends AbstractScreen {
	private enum Mode {
		Playing, Paused, Stoped
	};

	private DatabaseController dbController;
	private MediaTreeView menuTreeView;
	private TextField txtPlaylistName;
	private Slider timeSlider;
	private Slider volumeSlider;
	private Button play, mute;
	private Label playTime;
	
	Image img_mute=new Image(R.getImage("img_mute.png"));
	Image img_sound=new Image(R.getImage("img_sound.png"));
	Image img_pause=new Image(R.getImage("img_pause.png"));
	Image img_play=new Image(R.getImage("img_play.png"));


	private MediaView mediaView = null;
	private Duration duration;

	private ArrayList<MediaPlayer> players = new ArrayList<MediaPlayer>();
	private ArrayList<MediaFile> selectedFiles = new ArrayList<MediaFile>();

	private NowPlayingListView listFile;
	public List<File> list = new ArrayList<File>();

	private FileChooser fileChooser = new FileChooser();
	private DirectoryChooser folderChooser = new DirectoryChooser();

	private Mode mode = Mode.Stoped;
	private PlaylistTable playTable;
	private MediaFile currentAudio = null;

	public LibraryScreen(Stage primaryStage) {
		super(primaryStage);
	}

	@Override
	protected void initialize() {
		super.initialize();

		txtPlaylistName = FXMLController.getInstance().txtPlaylistName;
		timeSlider = FXMLController.getInstance().timeSlider;
		volumeSlider = FXMLController.getInstance().volumeSlider;
		play = FXMLController.getInstance().play;
		playTime = FXMLController.getInstance().playTime;
		mute = FXMLController.getInstance().mute;

		listFile = new NowPlayingListView();
		StackPane nowPlaying = FXMLController.getInstance().nowPlayingPane;
		nowPlaying.getChildren().add(listFile);

		try {
			dbController = DatabaseController.getInstance();

			// initialize library table
			playTable = new PlaylistTable();
			StackPane tablePane = (StackPane) findNodeById("tablePane");
			tablePane.getChildren().add(playTable.getTable());

			// initialize tree menu
			Pane treeViewPane = (Pane) findNodeById("treeViewPane");
			menuTreeView = new MediaTreeView();
			treeViewPane.getChildren().add(menuTreeView.getTreeView());
			int listSize = dbController.getListNames().size();
			String[] listNames = new String[listSize];
			for (int i = 0; i < listSize; i++) {
				listNames[i] = dbController.getListNames().get(i);
			}
			menuTreeView.loadTreeItems(listNames);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		addEventHandler();
	}

	private void addEventHandler() {
		timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				timeSlider.setValueChanging(true);
			}
		});

		timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				onProgressChanged();
			}
		});

		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				onVolumeChanged();
			}
		});

		listFile.setTreeViewListener(new ListViewListener() {
			@Override
			public void onItemClicked(MouseEvent event) {
				onClickNowPlayingList(event);
			}
		});

		menuTreeView.setTreeViewListener(new TreeViewListener() {
			@Override
			public void onItemSingleClicked(MouseEvent event,
					TreeItem<String> item) {
				String itemValue = item.getValue();
				if (item.getParent().getValue().equals("Playlist")) {
					playTable.setPlayList(itemValue);
				}
			}

			@Override
			public void onItemDoubleClicked(MouseEvent event,
					TreeItem<String> item) {
				String itemValue = item.getValue();
				if (item.getParent().getValue().equals("Playlist")) {
					try {
						// update playing list
						processOpenPlayList(dbController.getPlaylist(itemValue));
						// and show notify on playTable
						// playTable.setStatus("You are currently playing"
						// + " this list. You can edit it in the play panel");
					} catch (SQLException e) {
					}
				} else {
					System.out.println(itemValue + "is clicked!");
				}
			}

			@Override
			public void onPlayItem(String playList, int index) {
				try {
					// don't update playTable here, just playing list
					processOpenPlayList(dbController.getPlaylist(playList));
				} catch (SQLException e) {
				}
			}

			@Override
			public void onRemoveItem(String playList) {
				try {
					dbController.deletePlaylist(playList);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		playTable.setTableListener(new TableListener() {
			@Override
			public void onRemoveItem(String id) {
				try {
					dbController.deleteData(id);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPlayingItem(MediaInfo item) {
				onPlaySingleFile(item);
			}
		});
	}

	protected void onVolumeChanged() {
		if (volumeSlider.isValueChanging()) {
			mediaView.getMediaPlayer().setVolume(
					volumeSlider.getValue() / 100.0);
		}
	}

	protected void onProgressChanged() {
		mediaView.getMediaPlayer().pause();
		mediaView.getMediaPlayer().seek(
				duration.multiply(timeSlider.getValue() / 100.0));
		mediaView.getMediaPlayer().play();
		timeSlider.setValueChanging(false);
	}

	public void onSaveList() {
		String title;
		String artist;
		String length;
		String album;
		String listName;
		String url;
		MediaFile file;

		try {
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {

					listName = txtPlaylistName.getText();
					file = new MediaFile(list.get(i));
					title = file.getTitle();
					artist = file.getArtist();
					album = file.getAlbum();
					length = file.getLength();
					url = file.getPath();
					dbController.insertData(listName, title, artist, length,
							url, album);
				}
			}

			int listSize = dbController.getListNames().size();
			String[] listNames = new String[listSize];
			for (int i = 0; i < listSize; i++) {
				listNames[i] = dbController.getListNames().get(i);
			}
			menuTreeView.loadTreeItems(listNames);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void play(MediaPlayer player) {
		player.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});
		player.setOnReady(new Runnable() {
			public void run() {
				duration = player.getMedia().getDuration();
				updateValues();
			}
		});
		player.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				MediaPlayer curPlayer = mediaView.getMediaPlayer();
				curPlayer.stop();
				MediaPlayer nextPlayer = players.get((players
						.indexOf(curPlayer) + 1) % players.size());
				listFile.getSelectionModel().select(
						(players.indexOf(curPlayer) + 1) % players.size());
				mediaView.setMediaPlayer(nextPlayer);
				play(nextPlayer);

			}
		});
		// Set View khi play audio
		player.setAudioSpectrumListener(new AudioSpectrumListener() {

			@Override
			public void spectrumDataUpdate(double timestamp, double duration,
					float[] magnitudes, float[] phases) {
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
					// phaseNodes.getChildren().add(circle);
					i += 5;
				}
			}
		});
		
		player.play();
		currentAudio  = listFile.getSelectionModel().getSelectedItem();
		setMode(Mode.Playing);
	}

	private void setMode(Mode mode) {
		this.mode = mode;
		switch (mode) {
		case Playing:
			play.setGraphic(new ImageView(img_pause));play.setBackground(null);
			break;
		case Paused:
			play.setGraphic(new ImageView(img_play));play.setBackground(null);
			break;
		case Stoped:
			play.setGraphic(new ImageView(img_play));play.setBackground(null);
			break;
		}
	}

	public void onClickNowPlayingList(MouseEvent event) {
		int index = listFile.getSelectionModel().getSelectedIndex();
		MediaPlayer player = null;
		try {
			player = players.get(index);
		} catch (Exception e) {
			return;
		}

		if (event.getClickCount() == 2) {
			if (mediaView != null && mediaView.getMediaPlayer() != null) {
				mediaView.getMediaPlayer().stop();
			}

			mediaView.setMediaPlayer(player);
			play(mediaView.getMediaPlayer());
		}
	}

	protected void updateValues() {
		if (playTime != null && timeSlider != null && volumeSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = mediaView.getMediaPlayer()
							.getCurrentTime();
					playTime.setText(Utils.formatTime(currentTime, duration));
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
						if(volumeSlider.getValue()==0){
							
							 mute.setGraphic(new ImageView(img_mute));mute.setBackground(null);
							}
						if(volumeSlider.getValue()!=0){
							mute.setGraphic(new ImageView(img_sound));mute.setBackground(null);
					}
					}
				}
			});
		}
	}

	public void onClickPlay() {
		if (mediaView != null && mediaView.getMediaPlayer() != null) {
			if (mode == Mode.Playing) {
				mediaView.getMediaPlayer().pause();
				setMode(Mode.Paused);
			} else {
				play(mediaView.getMediaPlayer());
				setMode(Mode.Playing);
			}
		}
	}

	public void onClickNext() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();
		MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1)
				% players.size());
		listFile.getSelectionModel().select(
				(players.indexOf(curPlayer) + 1) % players.size());
		mediaView.setMediaPlayer(nextPlayer);
		play(nextPlayer);
	}

	public void onClickPrev() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		int i = players.indexOf(curPlayer);
		if (i > 0 && i < players.size()) {
			curPlayer.stop();
			MediaPlayer prevPlayer = players
					.get((players.indexOf(curPlayer) - 1) % players.size());
			listFile.getSelectionModel().select(
					(players.indexOf(curPlayer) - 1) % players.size());
			mediaView.setMediaPlayer(prevPlayer);
			play(prevPlayer);
		}
	}

	public void onClickStop() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();
		currentAudio = null;
		listFile.getSelectionModel().select(0);
		setMode(Mode.Stoped);
	}

	public void onClickMute() {
		mediaView.getMediaPlayer().setVolume(0);
		volumeSlider.setValue(0);
		mute.setGraphic(new ImageView(img_mute));mute.setBackground(null);
	}

	public void onClearList() {
		listFile.clearItems();
		mediaView.getMediaPlayer().stop();
	}

	// Xu li khi mo Open Folder
	public void processOpenFolder() {
		resetAll();
		File dir = folderChooser.showDialog(stage);
		for (final String url : dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".mp3"))
					return true;
				else {
					return false;
				}
			}

		})) {
			selectedFiles.add(new MediaFile(url));
			players.add(createPlayer("file:///"
					+ (dir + "\\" + url).replace("\\", "/").replaceAll(" ",
							"%20")));
			if (players.isEmpty()) {
				System.out.println("No audio found in " + dir);
			}
		}
		mediaView = new MediaView(players.get(0));
		listFile.setItemArray(selectedFiles);
		listFile.getSelectionModel().select(0);
		play(mediaView.getMediaPlayer());
	}

	// Xu li khi mo Open File
	public void processOpenFile() {
		resetAll();
		configureFileChooser(fileChooser);
		list = fileChooser.showOpenMultipleDialog(stage);

		if (list != null) {
			// get list items,players
			for (int i = 0; i < list.size(); i++) {
				selectedFiles.add(new MediaFile(list.get(i)));

				Media media = new Media(list.get(i).toURI().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				players.add(mediaPlayer);
			}
		}
		mediaView = new MediaView(players.get(0));
		listFile.setItemArray(selectedFiles);
		listFile.getSelectionModel().select(0);
		play(mediaView.getMediaPlayer());
	}

	public void processOpenPlayList(List<MediaInfo> playList) {
		resetAll();

		// get list items,players
		for (int i = 0; i < playList.size(); i++) {
			MediaFile file = playList.get(i).getMediaFile();
			selectedFiles.add(file);

			try {
				Media media = new Media(playList.get(i).getUrl());
				System.out.println(playList.get(i).getUrl());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				players.add(mediaPlayer);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mediaView = new MediaView(players.get(0));
		listFile.setItemArray(selectedFiles);
		listFile.getSelectionModel().select(0);
		play(mediaView.getMediaPlayer());
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

	public void resetAll() {
		if (mediaView != null && mediaView.getMediaPlayer() != null)
			mediaView.getMediaPlayer().stop();
		players.clear();
		selectedFiles.clear();
	}

	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("View Folder");
		fileChooser.setInitialDirectory(new File(System
				.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All", "*.*"),
				new FileChooser.ExtensionFilter("MP3", "*.mp3"));
	}

	public void onPlaySingleFile(MediaInfo selected) {
		if (mediaView != null) {
			if (mediaView.getMediaPlayer() != null) {
				mediaView.getMediaPlayer().stop();
			}
		}

		MediaPlayer mp1 = new MediaPlayer(new Media(selected.getUrl()));
		selectedFiles.clear();
		selectedFiles.add(selected.getMediaFile());
		listFile.setItemArray(selectedFiles);
		listFile.getSelectionModel().select(0);

		players.clear();
		players.add(mp1);
		mediaView = new MediaView(mp1);
		play(mediaView.getMediaPlayer());
	}

	/**
	 * get the current playing audio
	 * @return
	 */
	public MediaFile getCurrentAudio() {		
		return currentAudio;
	}
}