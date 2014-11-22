package application;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
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
import javafx.util.Callback;
import javafx.util.Duration;
import model.ListFile;
import model.MediaFile;
import model.PlayList;
import application.resource.R;

@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
public class FXMLController {
	FileChooser fileChooser = new FileChooser();
	DirectoryChooser folderChooser = new DirectoryChooser();
	List<File> list = new ArrayList<File>();
	Stage stage;
	List<MediaPlayer> players = new ArrayList<MediaPlayer>();
	MediaView mediaView = null;
	ObservableList<String> items = FXCollections.observableArrayList();
	ObservableList<PlayList> items2 = FXCollections.observableArrayList();
	ObservableList<ListFile> mediaFiles = FXCollections.observableArrayList();
	private Duration duration;
	@FXML
	private ListView<String> listFile;
	@FXML
	private Label fileDetail;
	@FXML
	private Button play, prev, next, btn_saveList, btn_clearList, btnGoPlayScene;
	@FXML
	private TextField tf_listName;
	@FXML
	private Slider volumeSlider, timeSlider;
	@FXML
	private Label playTime;
	@FXML
	private Pane view;

	@FXML
	private TableView<ListFile> libraryTable;

	@FXML
	private TableView<PlayList> playlistTable;

	@FXML
	private TableColumn titleColumn, lengthColoumn, artistColumn, albumColumn,
			ratingCol, playlistCol;

	final ObservableList<Integer> ratingSample = FXCollections
			.observableArrayList(1, 2, 3, 4, 5);

	DatabaseController control;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void initialize() throws ClassNotFoundException, SQLException {
		
		timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				timeSlider.setValueChanging(true);				
			}
		});
		
		timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				mediaView.getMediaPlayer().pause();
				mediaView.getMediaPlayer().seek(
						duration.multiply(timeSlider.getValue() / 100.0));
				mediaView.getMediaPlayer().play();
				timeSlider.setValueChanging(false);
			}
		});
		
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (volumeSlider.isValueChanging()) {
					mediaView.getMediaPlayer().setVolume(
							volumeSlider.getValue() / 100.0);
				}
			}
		});

		control = new DatabaseController();

		// List<ListFile> lf = getPlaylist();
		// updateTable(lf);

		List<PlayList> lf = getListPlay();
		if (lf != null) {
			updateListPlay(lf);
		}
	}

	public List<PlayList> getListPlay() throws SQLException {
		List<PlayList> pl = new ArrayList<PlayList>();
		List<String> ls = control.getListNames();

		for (int i = 0; i < ls.size(); i++) {
			pl.add(new PlayList(ls.get(i)));
		}

		return pl;

	}

	public void updateListPlay(List<PlayList> pl) throws SQLException {
		// Create a MenuItem and place it in a ContextMenu
		items2.clear();
		if (pl != null) {
			for (int i = 0; i < pl.size(); i++) {
				items2.add(pl.get(i));
			}
		}

		playlistCol
				.setCellValueFactory(new PropertyValueFactory<PlayList, String>(
						"name"));

		playlistTable
				.setRowFactory(new Callback<TableView<PlayList>, TableRow<PlayList>>() {

					@Override
					public TableRow<PlayList> call(TableView<PlayList> p) {
						final TableRow<PlayList> row = new TableRow<PlayList>();
						row.setOnDragEntered(new EventHandler<DragEvent>() {
							@Override
							public void handle(DragEvent t) {

							}
						});

						final ContextMenu contextMenu = new ContextMenu();
						final MenuItem removeMenuItem = new MenuItem("Remove");
						removeMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										String select = row.getItem().getName();
										try {
											control.deletePlaylist(select);

										} catch (SQLException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

										playlistTable.getItems().remove(
												row.getItem());
									}
								});
						contextMenu.getItems().add(removeMenuItem);
						final MenuItem playMenuItem = new MenuItem("Play");
						playMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										String select = row.getItem().getName();
										try {
											updateTable(control
													.getPlaylist(select));
										} catch (SQLException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									}
								});
						contextMenu.getItems().add(playMenuItem);
						// Set context menu on row, but use a binding to
						// make it only show for non-empty rows:
						row.contextMenuProperty().bind(
								Bindings.when(row.emptyProperty())
										.then((ContextMenu) null)
										.otherwise(contextMenu));

						return row;
					}
				}

				);

		playlistTable.setItems(items2);
	}

	@FXML
	protected void openFile(ActionEvent event) throws ClassNotFoundException,
			SQLException {
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
			listFile.getSelectionModel().select(
					(players.indexOf(curPlayer) + 1) % players.size());
			mediaView.setMediaPlayer(prevPlayer);
			play(prevPlayer);
		}
	}
	
	@FXML
	protected void onChangeScene(ActionEvent event) {
		if(Launcher.getInstance().currentScene == Launcher.LIBRARY_SCENE) {
			btnGoPlayScene.setText(R.strings.goPlayScene);			
			Launcher.getInstance().setScene(Launcher.PLAY_SCENE);
		}
		else {
			btnGoPlayScene.setText(R.strings.goLibraryScene);			
			Launcher.getInstance().setScene(Launcher.LIBRARY_SCENE);
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
	protected void onSaveList(ActionEvent event) throws ClassNotFoundException,
			SQLException {
		String title;
		String artist;
		String length;
		String album;
		String listName;
		String url;
		MediaFile file;

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {

				listName = tf_listName.getText();
				file = new MediaFile(list.get(i));
				title = file.getTitle();
				artist = file.getArtist();
				album = file.getAlbum();
				length = file.getLength();
				url = file.getPath();
				control.insertData(listName, title, artist, length, url, album);

			}

		}
		List<PlayList> lf = getListPlay();
		updateListPlay(lf);

	}

	@FXML
	protected void onClearList(ActionEvent event) {
		listFile.setItems(null);
		mediaView.getMediaPlayer().stop();

	}

	public List<ListFile> getPlaylist() throws ClassNotFoundException,
			SQLException {

		return control.getData();

	}

	public void updateTable(List<ListFile> lt) {
		mediaFiles.clear();
		if (lt != null) {
			for (int i = 0; i < lt.size(); i++) {

				mediaFiles.add(lt.get(i));

			}

			titleColumn
					.setCellValueFactory(new PropertyValueFactory<ListFile, String>(
							"title"));
			// lengthColoumn = new TableColumn("Length");
			lengthColoumn
					.setCellValueFactory(new PropertyValueFactory<ListFile, String>(
							"length"));
			// albumColumn = new TableColumn("Album");
			albumColumn
					.setCellValueFactory(new PropertyValueFactory<ListFile, String>(
							"album"));
			// artistColumn = new TableColumn("Artist");
			artistColumn
					.setCellValueFactory(new PropertyValueFactory<ListFile, String>(
							"artist"));

			//
			//

			libraryTable
					.setRowFactory(new Callback<TableView<ListFile>, TableRow<ListFile>>() {

						@Override
						public TableRow<ListFile> call(TableView<ListFile> p) {
							final TableRow<ListFile> row = new TableRow<ListFile>();
							row.setOnDragEntered(new EventHandler<DragEvent>() {
								@Override
								public void handle(DragEvent t) {

								}
							});

							final ContextMenu contextMenu = new ContextMenu();
							final MenuItem removeMenuItem = new MenuItem(
									"Remove");
							removeMenuItem
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent event) {
											String id = row.getItem().getId();
											try {
												control.deleteData(id);
											} catch (SQLException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

											libraryTable.getItems().remove(
													row.getItem());
										}
									});
							contextMenu.getItems().add(removeMenuItem);
							final MenuItem playMenuItem = new MenuItem("Play");
							playMenuItem
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent event) {
											if (mediaView != null) {
												if (mediaView.getMediaPlayer() != null) {
													mediaView.getMediaPlayer()
															.stop();
												}
											}

											// mediaView.setMediaPlayer(players.get(listFile
											// .getSelectionModel().getSelectedIndex()));
											Media m1 = new Media(row.getItem()
													.getUrl());
											MediaPlayer mp1 = new MediaPlayer(
													m1);
											items.clear();
											items.add(row.getItem().getTitle());
											listFile.setItems(items);

											players.add(mp1);
											mediaView = new MediaView(mp1);
											// mediaView.setMediaPlayer(mp1);
											play(mediaView.getMediaPlayer());

										}
									});
							contextMenu.getItems().add(playMenuItem);
							// Set context menu on row, but use a binding to
							// make it only show for non-empty rows:
							row.contextMenuProperty().bind(
									Bindings.when(row.emptyProperty())
											.then((ContextMenu) null)
											.otherwise(contextMenu));

							return row;
						}
					}

					);
			libraryTable.setItems(mediaFiles);
			// mediaFiles.clear();

		}

	}

	@FXML
	protected void btnNext(ActionEvent event) {
		MediaPlayer curPlayer = mediaView.getMediaPlayer();
		curPlayer.stop();
		MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1)
				% players.size());
		listFile.getSelectionModel().select(
				(players.indexOf(curPlayer) + 1) % players.size());
		// listFile.getFocusModel().focus((players.indexOf(curPlayer)+1) %
		// players.size());
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

	@FXML
	protected void selectItem() {
		listFile.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if (mediaView.getMediaPlayer() != null) {
					mediaView.getMediaPlayer().stop();
				}
				mediaView.setMediaPlayer(players.get(listFile
						.getSelectionModel().getSelectedIndex()));
				play(mediaView.getMediaPlayer());
			}
		});
	}

	protected void play(MediaPlayer play) {
		// TODO Auto-generated method stub
		// view.setStyle("-fx-background-color: #000033;");
		play.play();
		// view.getChildren().clear();
		// Group phaseNodes = new Group();
		// view.getChildren().add(phaseNodes);
		play.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});
		play.setOnReady(new Runnable() {
			public void run() {
				// play.play();
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
				// phaseNodes.getChildren().clear();
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
				System.out.println(list.get(i).toURI().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				players.add(mediaPlayer);
			}
		}
		mediaView = new MediaView(players.get(0));
		// mediaView.getMediaPlayer().setVolume(0.5);
		play(mediaView.getMediaPlayer());
		listFile.setItems(items);
		// titleColumn = new TableColumn("Title");

		// libraryTable.getColumns().addAll(titleColumn, lengthColoumn,
		// artistColumn, albumColumn);
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
		if (mediaView != null)
			mediaView.getMediaPlayer().stop();
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
