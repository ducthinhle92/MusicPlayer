package application.view;


import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import application.DatabaseController;
import application.FXMLController;
import application.view.listener.TableListener;
import model.MediaInfo;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;


public class PlaylistTable extends TableView {

	private TableView playTable;
	private TableColumn titleColumn;
	private TableColumn lengthColoumn;
	private TableColumn artistColumn;
	private TableColumn albumColumn;
	private TableColumn genreColumn;
	private DatabaseController dbController;
	private FXMLController fxmlController;
	private TableListener listener;
	public static PlaylistTable instance;
	private String name;

	public PlaylistTable(String playlistSelected) throws ClassNotFoundException, SQLException, URISyntaxException {
		super();
		instance = this;
		name = playlistSelected;
		this.fxmlController = FXMLController.getInstance();
		dbController = DatabaseController.getInstance();
		titleColumn = new TableColumn("Title");
		lengthColoumn = new TableColumn("Length");
		artistColumn = new TableColumn("Artist");
		albumColumn = new TableColumn("Album");
		genreColumn = new TableColumn("Genre");
		this.getColumns().addAll(titleColumn, lengthColoumn, artistColumn,
				albumColumn, genreColumn);
		
		this.setItems(getTableData(dbController.getPlaylist(name)));
		setTableFactory();
	}
	
	public String getName(){
		return name;
	}

	public TableView getTable() {
		return playTable;
	}

	public void setTableData(ObservableList<MediaInfo> mediaFiles) {
		playTable.setItems(mediaFiles);
	}

	public ObservableList<MediaInfo> getTableData(List<MediaInfo> lt) {
		
		ObservableList<MediaInfo> mediaFiles = FXCollections
				.observableArrayList();
		if (lt != null) {
			for (int i = 0; i < lt.size(); i++) {

				mediaFiles.add(lt.get(i));

			}
		}
		return mediaFiles;
	}

	public void setTableFactory() {
		titleColumn
				.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
						"title"));
		lengthColoumn
				.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
						"length"));
		artistColumn
				.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
						"artist"));
		albumColumn
				.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
						"album"));
		
		genreColumn
				.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>(
						"genre"));
		
		this
				.setRowFactory(new Callback<TableView<MediaInfo>, TableRow<MediaInfo>>() {

					@Override
					public TableRow<MediaInfo> call(TableView<MediaInfo> p) {
						final TableRow<MediaInfo> row = new TableRow<MediaInfo>();
						row.setOnDragEntered(new EventHandler<DragEvent>() {
							@Override
							public void handle(DragEvent t) {

							}
						});
						
						row.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent event) {
								// TODO Auto-generated method stub
								if(event.getClickCount() > 1){
									MediaInfo item = row.getItem();
									listener.onPlayingItem(item);
								}
								
							}

							
						});

						final ContextMenu contextMenu = new ContextMenu();
						final MenuItem removeMenuItem = new MenuItem("Remove");
						removeMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										String id = row.getItem().getId();
										listener.onRemoveItem(id);
										instance.getItems().remove(
												row.getItem());
									}
								});
						contextMenu.getItems().add(removeMenuItem);
						final MenuItem playMenuItem = new MenuItem("Play");
						playMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										MediaInfo item = row.getItem();
										listener.onPlayingItem(item);
									}
								});
						contextMenu.getItems().add(playMenuItem);
						// Set context menu on row, but use a binding to
						// make it only show for non-empty rows:
						
						
						final MenuItem playNextMenuItem = new MenuItem("Play Next");
						playNextMenuItem
						.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								MediaInfo item = row.getItem();
								listener.onPlayNextItem(item);
							}
						});
				contextMenu.getItems().add(playNextMenuItem);
				
				row.contextMenuProperty().bind(
						Bindings.when(row.emptyProperty())
								.then((ContextMenu) null)
								.otherwise(contextMenu));

						return row;
					}
				});
	}

	public void setPlayList(String playList) {
		try {
			setTableData(getTableData(dbController.getPlaylist(playList)));
		} catch (Exception e) {
		}
	}

	public void setTableListener(TableListener tableListener) {
		// TODO Auto-generated method stub
		this.listener = tableListener;
		
	}

	public void setAllMusic() {
		// TODO Auto-generated method stub
		
		try{
			setTableData(getTableData(dbController.getAllMusic()));
		} catch(Exception e){
			
		}
		
	}
}
