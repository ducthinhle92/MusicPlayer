package application.view;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import model.GenreInfo;
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

public class GenreTable extends TableView{
	
	private TableColumn genre; 
	private TableColumn count;
	private TableColumn length; 
	private DatabaseController dbController;
	private TableListener listener;
	
	public GenreTable() throws ClassNotFoundException, SQLException, URISyntaxException{
		super();
		dbController = DatabaseController.getInstance();
		genre = new TableColumn("Genre");
		count = new TableColumn("Count");
		length = new TableColumn("Length");
		this.getColumns().addAll(genre, count, length);
		
		this.setItems(getGenreData(dbController.getGenre()));
		this.setTableFactory();
		
	}
	
	public ObservableList<GenreInfo> getGenreData(List<GenreInfo> lt) {
		
		ObservableList<GenreInfo> genre = FXCollections
				.observableArrayList();
		if (lt != null) {
			for (int i = 0; i < lt.size(); i++) {

				genre.add(lt.get(i));

			}
		}
		return genre;
	}
	
	public void setTableFactory() {
		genre
				.setCellValueFactory(new PropertyValueFactory<GenreInfo, String>(
						"name"));
		count
				.setCellValueFactory(new PropertyValueFactory<GenreInfo, String>(
						"count"));
		length
				.setCellValueFactory(new PropertyValueFactory<GenreInfo, String>(
						"length"));
		
		this
				.setRowFactory(new Callback<TableView<GenreInfo>, TableRow<GenreInfo>>() {

					@Override
					public TableRow<GenreInfo> call(TableView<GenreInfo> p) {
						final TableRow<GenreInfo> row = new TableRow<GenreInfo>();
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
									GenreInfo item = row.getItem();
									listener.onPlayingGenreItem(item);
								}
								
							}

							
						});

						final ContextMenu contextMenu = new ContextMenu();
						
						final MenuItem playMenuItem = new MenuItem("Play");
						playMenuItem
								.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										GenreInfo item = row.getItem();
										listener.onPlayingGenreItem(item);
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
