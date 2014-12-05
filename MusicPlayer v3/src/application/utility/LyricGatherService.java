package application.utility;

import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LyricGatherService extends Service<Boolean> {
	private ArrayList<String> lyric;
	private String artist;
	private String songTitle;
	
	public LyricGatherService(String songTitle, String artist) {
		this.songTitle = songTitle;
		this.artist = artist;
		this.lyric = new ArrayList<String>();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {			

			@Override
			protected Boolean call() throws Exception {
				List<String> result = null;
				result = LyricsGatherer.getSongLyrics(songTitle, artist, lyric);
				return (result != null);
			}
		};
	}
	
	public ArrayList<String> getResult() {
		return lyric;
	}
}
