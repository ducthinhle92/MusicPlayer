package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.MediaInfo;
import model.PlayList;
import application.controller.LibraryScreen;
import application.controller.PlayScreen;
import application.resource.R;

@SuppressWarnings({ "unchecked", "rawtypes" })
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
	public Button play, prev, next, btn_saveList, btn_clearList, btnGoPlayScene;
	@FXML
	public TextField txtPlaylistName;
	@FXML
	public Slider volumeSlider, timeSlider;
	@FXML
	public Label playTime;
	@FXML
	public Pane view;
	@FXML
	public TableView<MediaInfo> libraryTable;
	@FXML
	public TableView<PlayList> playlistTable;
	@FXML
	public TableColumn titleColumn, lengthColoumn, artistColumn, albumColumn,
			ratingCol, playlistCol;
	@FXML
	public StackPane nowPlayingPane;

	final ObservableList<Integer> ratingSample = FXCollections
			.observableArrayList(1, 2, 3, 4, 5);

	private DatabaseController dbController;

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
		dbController = DatabaseController.getInstance();
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

	public List<MediaInfo> getPlaylist() throws ClassNotFoundException,
			SQLException {
		return dbController.getData();
	}

	public List<PlayList> getListPlay() throws SQLException {
		List<PlayList> pl = new ArrayList<PlayList>();
		List<String> ls = dbController.getListNames();

		for (int i = 0; i < ls.size(); i++) {
			pl.add(new PlayList(ls.get(i)));
		}

		return pl;
	}

	public void processOpenList(List<MediaInfo> playlist) {
		libraryScreen.processOpenPlayList(playlist);
	}

	public void updateTable(List<MediaInfo> lt) {
		mediaFiles.clear();
		if (lt != null) {
			for (int i = 0; i < lt.size(); i++) {

				mediaFiles.add(lt.get(i));

			}

			titleColumn
					.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
							"title"));
			// lengthColoumn = new TableColumn("Length");
			lengthColoumn
					.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
							"length"));
			// albumColumn = new TableColumn("Album");
			albumColumn
					.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
							"album"));
			// artistColumn = new TableColumn("Artist");
			artistColumn
					.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
							"artist"));

			//
			//

			libraryTable
					.setRowFactory(new Callback<TableView<MediaInfo>, TableRow<MediaInfo>>() {

						@Override
						public TableRow<MediaInfo> call(TableView<MediaInfo> p) {
							final TableRow<MediaInfo> row = new TableRow<MediaInfo>();
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
												dbController.deleteData(id);
											} catch (SQLException e) {
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
											libraryScreen.onPlaySingleFile(row.getItem());											
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
}
