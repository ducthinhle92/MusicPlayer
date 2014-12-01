package model;

import java.io.File;
import java.net.URI;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

public class MediaFile {

	private SimpleStringProperty title;
	private SimpleStringProperty artist;
	private SimpleStringProperty length;
	private SimpleStringProperty album;
	private SimpleIntegerProperty rating;

	private String url;
	private AudioFile audioFile;

	public MediaFile(File file) {
		try {
			audioFile = AudioFileIO.read(file);
			
			title = new SimpleStringProperty(audioFile.getTag().getFirst(
					FieldKey.TITLE));
			artist = new SimpleStringProperty(audioFile.getTag().getFirst(
					FieldKey.ARTIST));
			album = new SimpleStringProperty(audioFile.getTag().getFirst(
					FieldKey.ALBUM));
			rating = new SimpleIntegerProperty(4);

			url = file.toURI().toString();

			int duration = audioFile.getAudioHeader().getTrackLength();

			length = new SimpleStringProperty(duration / 60 + ":"
					+ (duration - 60 * (int) (duration / 60)));

		} catch (Exception e) {
			
		}
	}

	public MediaFile(URI uri) {
		this(new File(uri));
	}
	
	public MediaFile(String path) {
		this(new File(path));
	}

	public String getPath() {
		return url;
	}

	public Integer getRating() {
		return rating.get();
	}

	public String getTitle() {
		return title.get();
	}

	public String getArtist() {
		return artist.get();
	}

	public String getLength() {
		return length.get();
	}

	public String getAlbum() {
		return album.get();
	}

	public String getLyric() {
		return audioFile.getTag().getFirst(FieldKey.LYRICS);
	}
	
	@Override
	public String toString() {	
		return title.get();
	}
}
