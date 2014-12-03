package application.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.MediaFile;
import application.view.listener.ListViewListener;

public class NowPlayingListView extends ListView<MediaFile> {
	private EventHandler<MouseEvent> eventHandler;
	private ListViewListener listener;
	private ObservableList<MediaFile> items;
	private int playingIndex = -1;

	public NowPlayingListView() {
		super();

		items = FXCollections.observableArrayList();
		setItems(items);

		eventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Node node = event.getPickResult().getIntersectedNode();
				if (node instanceof ListCell<?>
						|| node instanceof NowPlayingListView) {
					return;
				}

				processMouseEvent(event);
			}
		};
		setEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
	}

	protected void processMouseEvent(MouseEvent event) {
		if (listener != null) {
			listener.onItemClicked(event);
		}
	}

	public void setItemList(ObservableList<MediaFile> list) {
		items = list;
		setItems(items);
	}

	public void clearItems() {
		items.clear();
		playingIndex = -1;
	}

	public void setTreeViewListener(ListViewListener listener) {
		this.listener = listener;
	}

	public void setPlayingIndex(int index) {
		focusSelectedIndex(getSelectionModel().getSelectedIndex(), index);
		getSelectionModel().select(index);
		playingIndex = index;
	}

	private void focusSelectedIndex(int oldIndex, int newIndex) {
		// change the view to focus the index
		if(oldIndex != -1) {
			// unfocus old item
		}
		
		// focus on the new selected item
	}

	public ObservableList<MediaFile> getItem() {
		return items;
	}

	public MediaFile getPlayingItem() {
		return getSelectionModel().getSelectedItem();
	}

	public int getPlayingIndex() {		
		return playingIndex;
	}

	public MediaPlayer getMediaPlayer() {
		if(playingIndex == -1)
			return null;
		
		MediaFile file = items.get(playingIndex);
		MediaPlayer mp = new MediaPlayer(new Media(file.getPath()));
		
		return mp;
	}

	public int getNextIndex() {
		return (playingIndex + 1) % items.size();
	}
	
	public int getPrevIndex() {
		return (playingIndex - 1 + items.size()) % items.size();
	}
}
