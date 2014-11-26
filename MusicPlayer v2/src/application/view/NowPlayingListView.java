package application.view;

import java.util.ArrayList;

import model.MediaFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class NowPlayingListView extends ListView<PlayingItem>{
	public NowPlayingListView() {
		super();
	}

	public void setItemArray(ArrayList<MediaFile> listFile) {
		ObservableList<PlayingItem> items = FXCollections.observableArrayList();
		for(MediaFile mf : listFile)
			items.add(new PlayingItem(mf));
		setItems(items);
	}

	public void clearItems() {
		setItems(null);
	}
}
