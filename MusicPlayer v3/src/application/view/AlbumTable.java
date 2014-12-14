package application.view;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import application.DatabaseController;
import application.view.listener.TableListener;
import model.AlbumInfo;
import model.MediaInfo;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class AlbumTable extends TableView{
	
	
	private TableColumn album; 
	private TableColumn count;
	private TableColumn length; 
	private DatabaseController dbController;
	private TableListener listener;
	
	public AlbumTable() throws ClassNotFoundException, SQLException, URISyntaxException{
		super();
		dbController = DatabaseController.getInstance();
		album = new TableColumn("Album");
		count = new TableColumn("Count");
		length = new TableColumn("Length");
		this.getColumns().addAll(album, count, length);
		
		this.setItems(getAlbumData(dbController.getAlbum()));
		this.setTableFactory();
		
	}
	
	public ObservableList<AlbumInfo> getAlbumData(List<AlbumInfo> lt) {
		
		ObservableList<AlbumInfo> albums = FXCollections
				.observableArrayList();
		if (lt != null) {
			for (int i = 0; i < lt.size(); i++) {

				albums.add(lt.get(i));

			}
		}
		return albums;
	}
	
	public void setTableFactory() {
		album
				.setCellValueFactory(new PropertyValueFactory<AlbumInfo, String>(
						"name"));
		count
				.setCellValueFactory(new PropertyValueFactory<AlbumInfo, String>(
						"count"));
		length
				.setCellValueFactory(new PropertyValueFactory<AlbumInfo, String>(
						"length"));
		
		this
				.setRowFactory(new Callback<TableView<AlbumInfo>, TableRow<AlbumInfo>>() {

					@Override
					public TableRow<AlbumInfo> call(TableView<AlbumInfo> p) {
						final TableRow<AlbumInfo> row = new TableRow<AlbumInfo>();
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
									AlbumInfo item = row.getItem();
									listener.onPlayingAlbumItem(item);
								}
								
							}

							
						});

						final ContextMenu contextMenu = new ContextMenu();
						
						final MenuItem playMenuItem = new MenuItem("Play");
						playMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										AlbumInfo item = row.getItem();
										listener.onPlayingAlbumItem(item);
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
				});
	}
	
	public void setTableListener(TableListener tableListener) {
		// TODO Auto-generated method stub
		this.listener = tableListener;
		
	}

}
