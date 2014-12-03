package model;

import java.net.URI;
import java.net.URISyntaxException;

public class MediaInfo {
	
	private String title;
	private String artist;
	private String length;
	private String album;
	private String url;
	private String id;
	
	public MediaInfo(String id, String tit, String art, String len, String alb, String ur){
		this.id = id;
		title = tit;
		artist = art;
		length = len;
		album = alb;
		url = ur;
	}
	
	public String getId(){
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getLength() {
		return length;
	}

	public String getAlbum() {
		return album;
	}

	public String getUrl() {
		return url;
	}
	
	public MediaFile getMediaFile() {
		MediaFile f = null;
		try {
			f = new MediaFile(new URI(url));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return f;
	}
}