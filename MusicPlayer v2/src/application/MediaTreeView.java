package application;

import java.sql.SQLException;
import java.util.List;

import javafx.collections.ObservableList;
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
import model.MediaInfo;
import application.controller.LibraryScreen;

public class MediaTreeView {
	private TreeView treeView;
	private FXMLController fxmlController;
	private DatabaseController dbController;
	
	private PlaylistTable playTable;

	public MediaTreeView(FXMLController fxmlController, LibraryScreen libScreen)
			throws ClassNotFoundException, SQLException {
		this.fxmlController = fxmlController;
		dbController = DatabaseController.getInstance();
		playTable = libScreen.getTable();
		if(playTable == null)
			System.out.println("null 2");

		treeView = new TreeView<>();
		treeView.setShowRoot(false);
		EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
			TreeItem<String> item = (TreeItem<String>) treeView
					.getSelectionModel().getSelectedItem();
			String selected = item.getValue();
			try {
				handleMouseClicked(event, selected);
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
		};
		treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				TreeCell<String> cell = new TreeCellImpl();
//				cell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
				return cell;
			}
			
			
		});
		
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

	}

	private void handleMouseClicked(MouseEvent event, String selected)
			throws SQLException {
		
		 Node node = event.getPickResult().getIntersectedNode();
		    // Accept clicks only on node cells, and not on empty spaces of the TreeView
		    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
		        String name = (String) ((TreeItem)treeView.getSelectionModel().getSelectedItem()).getValue();
		        System.out.println("Node click: " + name);
				if (event.getClickCount() == 1) {
					try {
//						fxmlController.updateTable(dbController.getPlaylist(selected));
						playTable.setTableData(playTable.getTableData(dbController.getPlaylist(selected)));
					} catch (SQLException e) {
					}
				} else if (event.getClickCount() == 2) {
					
					try {
//						fxmlController.updateTable(dbController.getPlaylist(selected));
						fxmlController.processOpenList(dbController
								.getPlaylist(selected));
//						
//						System.out.println("loi");
						try {
							List<MediaInfo>lt = dbController.getPlaylist(selected);
							System.out.println("ok 1");
							if(playTable == null)
								System.out.println("er 2");
							ObservableList<MediaInfo> mediaFiles = playTable.getTableData(lt);
							System.out.println("ok 2");
							playTable.setTableData(mediaFiles);
							System.out.println("ok 3");
							System.out.println("ko loi");
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("this error");
						}
						
					} catch (SQLException e) {
					}
				}
		    }
		
	}

	public TreeView getTreeView() {
		return treeView;
	}

	public void loadTreeItems(String... rootItems) {
		TreeItem<String> itemRoot = new TreeItem<>("Root");
		// TreeItem<String> nodeItem1 = new TreeItem<>("Playlist");
		TreeItem<String> noteItem2 = new TreeItem<>("Music");

		itemRoot.getChildren().add(noteItem2);

		TreeItem<String> item2Sub1 = new TreeItem<>("Artist");
		TreeItem<String> item2Sub2 = new TreeItem<>("Album");
		TreeItem<String> item2Sub3 = new TreeItem<>("Genre");
		noteItem2.getChildren().addAll(item2Sub1, item2Sub2, item2Sub3);
		TreeItem<String> item = new TreeItem<String>("Playlist");
		for (String itemString : rootItems) {
			item.getChildren().add(new BoxTreeItem(itemString));
		}
		treeView.setRoot(itemRoot);
		treeView.getRoot().getChildren().add(item);

		// locationTreeView.setRoot(root);
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

		public String getVal() {
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
						playTable.setTableData(playTable.getTableData(dbController.getPlaylist(listName)));
						fxmlController.processOpenList(dbController
								.getPlaylist(listName));
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
					TreeItem c = (TreeItem) treeView.getSelectionModel()
							.getSelectedItem();
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
				if (getTreeItem().getClass().getSimpleName()
						.equals("BoxTreeItem")) {
					setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
				}
		
				

			}
		}
	}

	public abstract class AbstractTreeItem extends TreeItem {
		public abstract ContextMenu getMenu();
	}

}
