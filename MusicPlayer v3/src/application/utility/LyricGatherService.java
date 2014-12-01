package application.utility;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LyricGatherService extends Service<Boolean> {
	private ObservableList<String> lyric;
	private String artist;
	private String songTitle;
	
	public LyricGatherService(String songTitle, String artist, 
			ObservableList<String> lyric) {
		this.songTitle = songTitle;
		this.artist = artist;
		this.lyric = lyric;
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
}
