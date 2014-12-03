package application.view;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.MediaFile;
import application.view.listener.ListViewListener;

public class NowPlayingListView extends ListView<MediaFile>{
	private EventHandler<MouseEvent> eventHandler;
	private ListViewListener listener;
	private ObservableList<MediaFile> items;

	public NowPlayingListView() {
		super();

		items = FXCollections.observableArrayList();
		setItems(items);
		
		eventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Node node = event.getPickResult().getIntersectedNode();
				if(node instanceof ListCell<?> 
				|| node instanceof NowPlayingListView) {
					return;
				}
				
				processMouseEvent(event);
			}
		};
		setEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
	}

	protected void processMouseEvent(MouseEvent event) {
		if(listener != null) {
			listener.onItemClicked(event);
		}
	}

	public void setItemArray(ArrayList<MediaFile> listFile) {
		items.clear();
		for(MediaFile mf : listFile)
			items.add(mf);
	}

	public void clearItems() {
		items.clear();
	}

	public void setTreeViewListener(ListViewListener listener) {
		this.listener = listener;
	}
	
	public void setPlayingItem(int index) {
		getSelectionModel().select(index);
	}
	
	public ObservableList<MediaFile> getItem() {
		return items;
	}

	public MediaFile getPlayingItem() {
		return null;
	}
}
