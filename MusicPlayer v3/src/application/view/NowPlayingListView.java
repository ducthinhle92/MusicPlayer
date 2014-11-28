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

public class NowPlayingListView extends ListView<PlayingItem>{
	private EventHandler<MouseEvent> eventHandler;
	private ListViewListener listener;
	private ObservableList<PlayingItem> items;

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
			items.add(new PlayingItem(mf));
	}

	public void clearItems() {
		setItems(null);
	}

	public void setTreeViewListener(ListViewListener listener) {
		this.listener = listener;
	}
}
