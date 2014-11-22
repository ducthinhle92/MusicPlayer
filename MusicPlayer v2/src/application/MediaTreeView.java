package application;



import java.sql.SQLException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MediaTreeView {
	private TreeView treeView;
	private FXMLController fxController;
	private DatabaseController dbController;
	
	public MediaTreeView() throws ClassNotFoundException, SQLException{
		dbController = new DatabaseController();
		
		
		treeView = new TreeView<>();
		treeView.setShowRoot(false);
		treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				return new TreeCellImpl();
			}
		});
		EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
			TreeItem<String> item  = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
			String selected = item.getValue();
		    try {
				handleMouseClicked(event, selected);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		};

		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle); 
		

	}
	
	private void handleMouseClicked(MouseEvent event, String selected) throws SQLException {
		// TODO Auto-generated method stub
		
		if(event.getClickCount() == 1){
			
			try {
				fxController.updateTable(dbController.getPlaylist(selected));
			} catch (SQLException e) {	
			}
		} else if (event.getClickCount() == 2){
			try {
				fxController.updateTable(dbController.getPlaylist(selected));
			} catch (SQLException e) {	
			}
			
			fxController.processOpenList(dbController.getPlaylist(selected));
		}
		 
		
	}

	public TreeView getTreeView(){
		return treeView;
	}
	
	public void setController(FXMLController controll){
		this.fxController = controll;
	}
	
	
	public void loadTreeItems(String... rootItems) {
		TreeItem<String> itemRoot = new TreeItem<>("Root");
//		TreeItem<String> nodeItem1 = new TreeItem<>("Playlist");
		TreeItem<String> noteItem2 = new TreeItem<>("Music");
		
		itemRoot.getChildren().add(noteItem2);
		
		TreeItem<String> item2Sub1 = new TreeItem<>("Artist");
		TreeItem<String> item2Sub2 = new TreeItem<>("Album");
		TreeItem<String> item2Sub3 = new TreeItem<>("Genre");
		noteItem2.getChildren().addAll(item2Sub1,item2Sub2, item2Sub3);
	    TreeItem<String> item = new TreeItem<String>("Playlist");
	    for (String itemString: rootItems) {
	      item.getChildren().add(new BoxTreeItem(itemString));
	    }
	    treeView.setRoot(itemRoot);
	    treeView.getRoot().getChildren().add(item);
	    
	    
	 
//	    locationTreeView.setRoot(root);
	  }
	
	
	public class ProviderTreeItem extends AbstractTreeItem {
		// make class vars here like psswd
		public ProviderTreeItem(String name) {
			this.setValue(name);
		}

		@Override
		public ContextMenu getMenu() {
			MenuItem addInbox = new MenuItem("add inbox");
			addInbox.setOnAction(new EventHandler() {
				public void handle(Event t) {
					BoxTreeItem newBox = new BoxTreeItem("inbox");
					getChildren().add(newBox);
				}
			});
			return new ContextMenu(addInbox);
		}
	}

	public class BoxTreeItem extends AbstractTreeItem {
		// private List<String> emails = new LinkedList<>();
		public BoxTreeItem(String name) {
			this.setValue(name);
		}
		
		public String getVal(){
			return this.getValue().toString();
		}
		
		

		@Override
		public ContextMenu getMenu() {
			ContextMenu contextMenu = new ContextMenu();
			MenuItem playItem = new MenuItem("Play");
			
			playItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent ev) {
					// TODO Auto-generated method stub
					String listName = getVal();
					
					try {
						fxController.updateTable(dbController.getPlaylist(listName));
						fxController.processOpenList(dbController.getPlaylist(listName));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						
					}
					
				}
			});
			
			MenuItem removeItem = new MenuItem("Remove");
			removeItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent ev) {
					// TODO Auto-generated method stub
					try {
						dbController.deletePlaylist(getVal());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					TreeItem c = (TreeItem) treeView.getSelectionModel().getSelectedItem();
		            boolean remove = c.getParent().getChildren().remove(c);
					
				}
			});
			
			contextMenu.getItems().addAll(playItem, removeItem);
			return contextMenu;
		}
	}
	
	private final class TreeCellImpl extends TreeCell<String> {

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setText(getItem() == null ? "" : getItem().toString());
				setGraphic(getTreeItem().getGraphic());
				if(getTreeItem().getClass().getSimpleName().equals("BoxTreeItem")){
					setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
				}
				
			}
		}
	}
	
	public abstract class AbstractTreeItem extends TreeItem {
		public abstract ContextMenu getMenu();
	}

}
