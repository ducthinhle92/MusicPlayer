package application.view;

import java.sql.SQLException;

import application.FXMLController;
import application.controller.TreeViewListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MediaTreeView {
	private TreeView treeView;
	private TreeViewListener itemListener;

	public MediaTreeView()
			throws ClassNotFoundException, SQLException {

		treeView = new TreeView<>();
		treeView.setShowRoot(false);
		EventHandler<MouseEvent> mouseEventHandle = 
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				TreeItem<String> item = (TreeItem<String>) treeView
						.getSelectionModel().getSelectedItem();
				try {
					handleMouseClicked(event, item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				TreeCell<String> cell = new TreeCellImpl();
				return cell;
			}
		});
		
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
	}
	
	public void setOnItemClickListener(TreeViewListener listener) {
		this.itemListener = listener;
	}

	private void handleMouseClicked(MouseEvent event, TreeItem<String> item)
			throws SQLException {
		if(itemListener != null)
			itemListener.onItemClicked(event, item,FXMLController.getInstance().getLibraryScreen().getTable());
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
		public BoxTreeItem(String name) {
			this.setValue(name);
		}

		@Override
		public ContextMenu getMenu() {
			ContextMenu contextMenu = new ContextMenu();
			MenuItem playItem = new MenuItem("Play");

			playItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent ev) {
					
					if(itemListener != null)
						itemListener.onPlayItem(getValue().toString());
				}
			});

			MenuItem removeItem = new MenuItem("Remove");
			removeItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent ev) {
					if(itemListener != null)
						itemListener.onRemoveItem(getValue().toString());
					int index = treeView.getSelectionModel().getSelectedIndex();
					getChildren().remove(index);
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
