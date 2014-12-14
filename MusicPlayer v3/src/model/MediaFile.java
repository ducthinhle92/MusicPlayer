package model;

import java.io.File;
import java.net.URI;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;

public class MediaFile {

	private SimpleStringProperty title;
	private SimpleStringProperty artist;
	private SimpleStringProperty length;
	private SimpleStringProperty album;
	private SimpleIntegerProperty rating;
	private String genre;
	private int duration;

	private String url;
	private AudioFile audioFile;

	public MediaFile(File file) {
		try {
			audioFile = AudioFileIO.read(file);
			
			if(audioFile.getTag().getFirst(FieldKey.TITLE).equals("")){
				title = new SimpleStringProperty(file.getName());
			}
			else{
				title = new SimpleStringProperty(audioFile.getTag().getFirst(
						FieldKey.TITLE));
			}
			
			if(audioFile.getTag().getFirst(FieldKey.ARTIST).equals("")){
				artist = new SimpleStringProperty("No artist");
			}
			else{
				artist = new SimpleStringProperty(audioFile.getTag().getFirst(
						FieldKey.ARTIST));
			}
			
			if(audioFile.getTag().getFirst(FieldKey.ALBUM).equals("")){
				album = new SimpleStringProperty("No album");
			}
			else{
				album = new SimpleStringProperty(audioFile.getTag().getFirst(
						FieldKey.ALBUM));
			}
			
			if(audioFile.getTag().getFirst(FieldKey.GENRE).equals("")){
				genre = "No Genre";
			}
			else{
				genre = audioFile.getTag().getFirst(
						FieldKey.GENRE);
			}
			
			rating = new SimpleIntegerProperty(4);

			url = file.toURI().toString();

			duration = audioFile.getAudioHeader().getTrackLength();

			length = new SimpleStringProperty(duration / 60 + ":"
					+ (duration - 60 * (int) (duration / 60)));

		} catch (Exception e) {
			
		}
	}
	
	public int getDuration(){
		return duration;
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

	public void setTitle(String text) {
		title.set(text);
	}
	
	public void setArtist(String text) {
		artist.set(text);
	}
	
	public String getGenre(){
		return genre;
	}
	
	public void setLyric(String lyric) {
		try {
			audioFile.getTag().setField(FieldKey.LYRICS, lyric);
		} catch (KeyNotFoundException | FieldDataInvalidException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFile() throws CannotWriteException {
		try {
			audioFile.getTag().setField(FieldKey.TITLE, title.get());
			audioFile.getTag().setField(FieldKey.ARTIST, artist.get());
			audioFile.commit();
		} catch (KeyNotFoundException | FieldDataInvalidException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {	
		if(title == null)
			return "no title";
		
		if (title.get().trim().equals(""))
			return "no title";
		
		return title.get();
	}
}
