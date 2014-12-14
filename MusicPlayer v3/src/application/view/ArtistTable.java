package application.view;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import model.ArtistInfo;
import application.DatabaseController;
import application.view.listener.TableListener;
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

public class ArtistTable extends TableView{
	
	private TableColumn artist; 
	private TableColumn count;
	private TableColumn length; 
	private DatabaseController dbController;
	private TableListener listener;
	
	public ArtistTable() throws ClassNotFoundException, SQLException, URISyntaxException{
		super();
		dbController = DatabaseController.getInstance();
		artist = new TableColumn("Artist");
		count = new TableColumn("Count");
		length = new TableColumn("Length");
		this.getColumns().addAll(artist, count, length);
		
		this.setItems(getArtistData(dbController.getArtist()));
		this.setTableFactory();
		
	}
	
	public ObservableList<ArtistInfo> getArtistData(List<ArtistInfo> lt) {
		
		ObservableList<ArtistInfo> artist = FXCollections
				.observableArrayList();
		if (lt != null) {
			for (int i = 0; i < lt.size(); i++) {

				artist.add(lt.get(i));

			}
		}
		return artist;
	}
	
	public void setTableFactory() {
		artist
				.setCellValueFactory(new PropertyValueFactory<ArtistInfo, String>(
						"name"));
		count
				.setCellValueFactory(new PropertyValueFactory<ArtistInfo, String>(
						"count"));
		length
				.setCellValueFactory(new PropertyValueFactory<ArtistInfo, String>(
						"length"));
		
		this
				.setRowFactory(new Callback<TableView<ArtistInfo>, TableRow<ArtistInfo>>() {

					@Override
					public TableRow<ArtistInfo> call(TableView<ArtistInfo> p) {
						final TableRow<ArtistInfo> row = new TableRow<ArtistInfo>();
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
									ArtistInfo item = row.getItem();
									listener.onPlayingArtistItem(item);
								}
								
							}

							
						});

						final ContextMenu contextMenu = new ContextMenu();
						
						final MenuItem playMenuItem = new MenuItem("Play");
						playMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										ArtistInfo item = row.getItem();
										listener.onPlayingArtistItem(item);
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
