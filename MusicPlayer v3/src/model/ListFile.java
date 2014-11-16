package model;

public class ListFile {
	
	private String title;
	private String artist;
	private String length;
	private String album;
	private String listName;
	private String url;
	private String id;
	
	public ListFile(String id, String tit, String art, String len, String alb, String listN, String ur){
		this.id = id;
		title = tit;
		artist = art;
		length = len;
		album = alb;
		listName = listN;
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

	public String getListName() {
		return listName;
	}

	public String getUrl() {
		return url;
	}
	
	

}
