package application.view;

import model.MediaFile;

public class PlayingItem {

	private String title;

	public PlayingItem(MediaFile mf) {
		this.title = mf.getTitle();
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString() {
		return title;
	}
}
