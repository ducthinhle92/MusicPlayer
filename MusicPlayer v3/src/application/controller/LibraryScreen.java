package application.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MediaFile;
import model.MediaInfo;
import application.DatabaseController;
import application.FXMLController;
import application.config.Config;
import application.resource.R;
import application.utility.Utils;
import application.view.ButtonEffector;
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
	private File dir;

	// FXML component
	private MediaTreeView menuTreeView;
	private TextField txtPlaylistName;
	private Slider timeSlider;
	private Slider volumeSlider;
	private Button play, mute, stop, next, prev;
	private Label playTime, lbInfo;
	private VBox mainBackground;
	private List<String> allMusicUrl;

	private ImageView img_sound, img_mute;

	private MediaView mediaView = null;
	private Duration duration;
	private ObservableList<MediaFile> playingFiles = FXCollections
			.observableArrayList();
	private NowPlayingListView nowPlayingView;
	private FileChooser fileChooser = new FileChooser();

	private Mode mode = Mode.Stoped;
	private PlaylistTable playTable;
	private Pane controlPane;

	private boolean muted = false;
	private MediaInfoUpdater infoUpdater;

	public LibraryScreen(Stage primaryStage) {
		super(primaryStage);
	}

	@Override
	protected void initialize() {
		super.initialize();

		controlPane = FXMLController.getInstance().controlPane;
		lbInfo = FXMLController.getInstance().lbInfo;
		playTime = FXMLController.getInstance().playTime;
		mainBackground = FXMLController.getInstance().mainBackground;
		txtPlaylistName = FXMLController.getInstance().txtPlaylistName;
		timeSlider = FXMLController.getInstance().timeSlider;
		volumeSlider = FXMLController.getInstance().volumeSlider;
		play = FXMLController.getInstance().play;
		prev = FXMLController.getInstance().prev;
		next = FXMLController.getInstance().next;
		stop = FXMLController.getInstance().stop;
		mute = FXMLController.getInstance().mute;

		Image img_prev = new Image(R.getImage("img_prev.png"));
		prev.setGraphic(new ImageView(img_prev));
		prev.setBackground(null);
		prev.setDisable(true);
		ButtonEffector.addEffect(prev);
		ButtonEffector.setGraphic(prev, R.getImage("img_prev.png"),
				R.getImage("img_prev_hover.png"));

		Image img_next = new Image(R.getImage("img_next.png"));
		next.setGraphic(new ImageView(img_next));
		next.setBackground(null);
		next.setDisable(true);
		ButtonEffector.addEffect(next);
		ButtonEffector.setGraphic(next, R.getImage("img_next.png"),
				R.getImage("img_next_hover.png"));

		play.setBackground(null);
		ButtonEffector.addEffect(play);
		ButtonEffector.setGraphic(play, R.getImage("img_pause.png"),
				R.getImage("img_pause_hover.png"));

		img_sound = new ImageView(R.getImage("img_sound.png"));
		img_mute = new ImageView(R.getImage("img_mute.png"));
		mute.setGraphic(img_sound);
		mute.setBackground(null);
		ButtonEffector.addEffect(mute);

		Image img_stop = new Image(R.getImage("img_stop.png"));
		stop.setGraphic(new ImageView(img_stop));
		stop.setBackground(null);
		ButtonEffector.addEffect(stop);

		// initialize value and set default button state
		mediaView = new MediaView();
		stop.setDisable(true);
		lbInfo.setText("");
		volumeSlider.setValue(Config.getInstance().getDouble(
				Config.SETTING_VOLUME));

		nowPlayingView = new NowPlayingListView();
		nowPlayingView.setItemList(playingFiles);
		StackPane nowPlaying = FXMLController.getInstance().nowPlayingPane;
		nowPlaying.getChildren().add(nowPlayingView);

		try {
			dbController = DatabaseController.getInstance();
			allMusicUrl = dbController.getAllMusicUrl();
			System.out.println(allMusicUrl.size());

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

	@Override
	public void show() {
		super.show();
		mainBackground.setStyle(R.styles.background_lib);
		controlPane.setStyle(R.styles.control_pane_lib);
		lbInfo.setStyle(R.styles.label_info_lib);
		playTime.setStyle(R.styles.label_time_lib);
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
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				onVolumeChanged();
			}
		});

		nowPlayingView.setTreeViewListener(new ListViewListener() {
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
				} else if (itemValue.equals("All Music")) {
					playTable.setAllMusic();
					System.out.println("Set ok");
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

			@Override
			public void onPlayNextItem(MediaInfo item) {
				onPlayNextFile(item);
			}
		});

		playingFiles.addListener(new ListChangeListener<MediaFile>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends MediaFile> change) {
				if (playingFiles.size() >= 2) {
					prev.setDisable(false);
					next.setDisable(false);
				} else {
					prev.setDisable(true);
					next.setDisable(true);
				}
			}
		});
	}

	protected void onPlayNextFile(MediaInfo item) {
		MediaFile file = item.getMediaFile();
		int index = nowPlayingView.getNextIndex();
		playingFiles.add(index, file);
	}

	protected void onVolumeChanged() {
		if (muted) {
			muted = false;
			mute.setGraphic(img_sound);
		}
		if (mediaView.getMediaPlayer() != null)
			mediaView.getMediaPlayer().setVolume(
					volumeSlider.getValue() / 100.0);
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
			if (nowPlayingView != null) {
				for (int i = 0; i < nowPlayingView.getItem().size(); i++) {

					listName = txtPlaylistName.getText();
					file = nowPlayingView.getItem().get(i);
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
				onMediaReady();
			}
		});
		player.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				onMediaEnd();
			}
		});

		// set the volume
		if (muted)
			player.setVolume(0);
		else
			player.setVolume(volumeSlider.getValue() / 100);

		// update playing info
		if (infoUpdater != null)
			infoUpdater.finish();
		infoUpdater = new MediaInfoUpdater();
		infoUpdater.start();

		player.play();
		FXMLController.getInstance().onMediaChanged();
		if (mode != Mode.Playing)
			setMode(Mode.Playing);

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
	}

	protected void onMediaEnd() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();
		curPlayer.dispose();

		int index = nowPlayingView.getNextIndex();
		nowPlayingView.setPlayingIndex(index);
		MediaPlayer nextPlayer = nowPlayingView.getMediaPlayer();
		mediaView.setMediaPlayer(nextPlayer);
		play(nextPlayer);
	}

	protected void onMediaReady() {
		duration = mediaView.getMediaPlayer().getMedia().getDuration();
		updateValues();
	}

	private void setMode(Mode mode) {
		this.mode = mode;
		switch (mode) {
		case Playing:
			ButtonEffector.setGraphic(play, R.getImage("img_pause.png"),
					R.getImage("img_pause_hover.png"));
			stop.setDisable(false);
			break;
		case Paused:
			ButtonEffector.setGraphic(play, R.getImage("img_play.png"),
					R.getImage("img_play_hover.png"));
			break;
		case Stoped:
			ButtonEffector.setGraphic(play, R.getImage("img_play.png"),
					R.getImage("img_play_hover.png"));
			stop.setDisable(true);
			break;
		}
	}

	public void onClickNowPlayingList(MouseEvent event) {
		if (event.getClickCount() == 2) {
			if (mediaView != null && mediaView.getMediaPlayer() != null) {
				mediaView.getMediaPlayer().stop();
			}

			int index = nowPlayingView.getSelectionModel().getSelectedIndex();
			nowPlayingView.setPlayingIndex(index);
			mediaView.setMediaPlayer(nowPlayingView.getMediaPlayer());
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
					// if (!volumeSlider.isValueChanging()) {
					// volumeSlider.setValue((int) Math.round(mediaView
					// .getMediaPlayer().getVolume() * 100));
					// if (volumeSlider.getValue() == 0) {
					// mute.setGraphic(new ImageView(img_mute));
					// mute.setBackground(null);
					// }
					// if (volumeSlider.getValue() != 0) {
					// mute.setGraphic(new ImageView(img_sound));
					// mute.setBackground(null);
					// }
					// }
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
				mediaView.getMediaPlayer().play();
				setMode(Mode.Playing);
			}
		}
	}

	public void onClickNext() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		if (curPlayer != null) {
			curPlayer.stop();
			curPlayer.dispose();
		}

		int curIndex = nowPlayingView.getPlayingIndex();
		if (curIndex >= 0) {
			int index = nowPlayingView.getNextIndex();
			nowPlayingView.setPlayingIndex(index);
			MediaPlayer nextPlayer = nowPlayingView.getMediaPlayer();
			mediaView.setMediaPlayer(nextPlayer);

			if (mode == Mode.Playing)
				play(nextPlayer);
			else
				FXMLController.getInstance().onMediaChanged();
		}
	}

	public void onClickPrev() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		if (curPlayer != null) {
			curPlayer.stop();
			curPlayer.dispose();
		}
		int curIndex = nowPlayingView.getPlayingIndex();
		if (curIndex >= 0) {
			curPlayer.stop();

			int index = nowPlayingView.getPrevIndex();
			nowPlayingView.setPlayingIndex(index);
			MediaPlayer prevPlayer = nowPlayingView.getMediaPlayer();
			mediaView.setMediaPlayer(prevPlayer);

			if (mode == Mode.Playing)
				play(prevPlayer);
			else
				FXMLController.getInstance().onMediaChanged();
		}
	}

	public void onClickStop() {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();

		nowPlayingView.onStop();
		setMode(Mode.Stoped);
	}

	public void onClickMute() {
		if (muted) {
			muted = false;
			mute.setGraphic(img_sound);
			if (mediaView.getMediaPlayer() != null)
				mediaView.getMediaPlayer().setVolume(
						volumeSlider.getValue() / 100);
		} else {
			muted = true;
			mute.setGraphic(img_mute);
			if (mediaView.getMediaPlayer() != null)
				mediaView.getMediaPlayer().setVolume(0);
		}
		System.out.println("muted = " + muted);
	}

	public void onClearList() {
		nowPlayingView.clearItems();
		mediaView.getMediaPlayer().stop();
		FXMLController.getInstance().onMediaChanged();
	}

	public void processOpenFile() throws SQLException {
		configureFileChooser(fileChooser);
		List<File> listFile = fileChooser.showOpenMultipleDialog(stage);
		dir = null;

		if (listFile != null) {
			resetAll();
			dir = listFile.get(0).getParentFile();
			System.out.println(dir.getPath());
			for (int i = 0; i < listFile.size(); i++) {
				playingFiles.add(new MediaFile(listFile.get(i)));
			}

			// Luong add nhac vao thu vien
			Thread thread = new Thread() {

				@Override
				public void run() {
					try {
						addMusicToLibrary(dir);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			};
			thread.start();

		}

		nowPlayingView.setPlayingIndex(0);
		mediaView.setMediaPlayer(nowPlayingView.getMediaPlayer());
		play(mediaView.getMediaPlayer());
	}

	// add music to library
	public void addMusicToLibrary(File dir) throws SQLException {
		ArrayList<File> listMp3 = getMp3FileOnSelectedFolder(dir);

		for (int i = 0; i < listMp3.size(); i++) {
			if (!checkExits(listMp3.get(i))) {
				insertMp3FileToDatabase(listMp3.get(i));
			}
		}
	}

	public void insertMp3FileToDatabase(File file) throws SQLException {
		MediaFile mdFile = new MediaFile(file);
		String title = mdFile.getTitle();
		String length = mdFile.getLength();
		String artist = mdFile.getArtist();
		String album = mdFile.getAlbum();
		String url = mdFile.getPath();
		dbController.insertIntoAllmusic(title, artist, length, url, album);
	}

	public ArrayList<File> getMp3FileOnSelectedFolder(File dir) {
		ArrayList<File> list = new ArrayList<>();
		File[] files = dir.listFiles();

		for (File file : files) {
			String path = file.getPath();
			if (path.substring(path.length() - 4, path.length()).equals(".mp3")) {
				list.add(file);
			}
		}
		return list;
	}

	public boolean checkExits(File file) {
		MediaFile mdFile = new MediaFile(file);
		String mdPath = mdFile.getPath();

		if (mdPath != null) {
			for (int i = 0; i < allMusicUrl.size(); i++) {
				if (mdPath.equals(allMusicUrl.get(i))) {
					return true;
				}

			}
		}

		return false;
	}

	public void processOpenPlayList(List<MediaInfo> playList) {
		resetAll();

		// get list items,players
		for (int i = 0; i < playList.size(); i++) {
			MediaFile file = playList.get(i).getMediaFile();
			playingFiles.add(file);
		}

		nowPlayingView.setPlayingIndex(0);
		mediaView = new MediaView(nowPlayingView.getMediaPlayer());
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
		nowPlayingView.clearItems();
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

		playingFiles.clear();
		playingFiles.add(selected.getMediaFile());
		nowPlayingView.setPlayingIndex(0);
		mediaView.setMediaPlayer(nowPlayingView.getMediaPlayer());

		play(mediaView.getMediaPlayer());
	}

	/**
	 * get the current playing audio
	 * 
	 * @return
	 */
	public MediaFile getCurrentMedia() {
		return nowPlayingView.getPlayingItem();
	}

	class MediaInfoUpdater extends Thread {
		private static final long DELAY_TIME = 3000;
		int infoType = 0;
		boolean finished = false;

		@Override
		public void run() {
			while (!finished) {
				if (nowPlayingView == null)
					return;

				infoType++;
				if (infoType > 2)
					infoType = 0;

				MediaFile nowPlaying = nowPlayingView.getPlayingItem();
				if (nowPlaying == null)
					return;

				String strInfo = "";
				switch (infoType) {
				case 0:
					strInfo = nowPlaying.getTitle();
					break;
				case 1:
					strInfo = nowPlaying.getArtist();
					break;
				case 2:
					strInfo = nowPlaying.getAlbum();
					break;
				}

				final String infoText = strInfo;
				Platform.runLater(new Runnable() {
					public void run() {
						lbInfo.setText(infoText);
					}
				});

				try {
					Thread.sleep(DELAY_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void finish() {
			finished = true;
		}
	}
}
