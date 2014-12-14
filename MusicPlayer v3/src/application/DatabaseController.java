package application;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.AlbumInfo;
import model.ArtistInfo;
import model.GenreInfo;
import model.MediaFile;
import model.MediaInfo;

public class DatabaseController {
	private static DatabaseController instance;
	private Connection c;
	
	public static DatabaseController getInstance() 
			throws ClassNotFoundException, SQLException {
		if(instance == null) {
			instance = new DatabaseController();
		}
		
		return instance;
	}

	public DatabaseController() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:audio.db");
		Statement stat = c.createStatement();
		stat.executeUpdate("create table if not exists allmusic(id integer,"
				+ "title varchar(30),"
				+ "length varchar(15)," + "album varchar(30),"
				+ "artist varchar(30),"+ "genre varchar(30)," + "url text," + "primary key (id));");
		
		stat.executeUpdate("create table if not exists playlist(listname varchar(30),id integer);");
		
		stat.close();
		

	}

	public List<String> getListNames() throws SQLException {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select DISTINCT listname FROM playlist;");

		String listName = "";
		while (res.next()) {
			listName = res.getString("listname");
			list.add(listName);
		}
		
		res.close();
		stat.close();
		
		return list;

	}
	
	public List<String> getAllMusicUrl() throws SQLException{
		List<String> listAllMusicUrl = new ArrayList<>();
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select url from allmusic;");
		while(res.next()){
			listAllMusicUrl.add(res.getString("url"));
		}
		stat.close();
		res.close();
		
		return listAllMusicUrl;
	}

	public void deletePlaylist(String select) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement prep = c
				.prepareStatement("DELETE from playlist where listName=?;");
		prep.setString(1, select);
		prep.execute();

		prep.close();
		

	}
	
	public List<String> getAlbumName() throws SQLException{
		
		List<String> list = new ArrayList<String>();
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select DISTINCT album FROM allmusic;");

		String listName = "";
		while (res.next()) {
			listName = res.getString("album");
			list.add(listName);
		}
		
		stat.close();
		res.close();
		
		return list;
		
	}
	
	public List<String> getArtistName() throws SQLException{
		
		List<String> list = new ArrayList<String>();
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select DISTINCT artist FROM allmusic;");
		
		

		String listName = "";
		while (res.next()) {
			listName = res.getString("artist");
			list.add(listName);
		}
		
		stat.close();
		res.close();
		
		return list;
		
	}
	
public List<String> getGenreName() throws SQLException{
		
		List<String> list = new ArrayList<String>();
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select DISTINCT genre FROM allmusic;");

		String listName = "";
		while (res.next()) {
			listName = res.getString("genre");
			list.add(listName);
		}
		
		stat.close();
		res.close();
		
		return list;
		
	}
	
	public List<MediaInfo> getAlbumByName(String name) throws SQLException, URISyntaxException{
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		PreparedStatement prep;
		prep = c.prepareStatement("select id from allmusic where album = ?;");
		prep.setString(1, name);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		
		prep.close();
		res.close();
		
		return list;
	}
	
	public List<MediaInfo> getArtistByName(String name) throws SQLException, URISyntaxException{
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		PreparedStatement prep;
		prep = c.prepareStatement("select id from allmusic where artist = ?;");
		prep.setString(1, name);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		prep.close();
		res.close();
		
		return list;
	}
	
	public List<MediaInfo> getGenreByName(String name) throws SQLException, URISyntaxException{
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		PreparedStatement prep;
		prep = c.prepareStatement("select id from allmusic where genre = ?;");
		prep.setString(1, name);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		
		prep.close();
		res.close();
		
		return list;
	}
	
	
	
	public List<AlbumInfo> getAlbum() throws SQLException, ClassNotFoundException, URISyntaxException{
		List<AlbumInfo> list = new ArrayList<AlbumInfo>();
		List<String> listAlbum = getAlbumName();
		for(int i = 0; i < listAlbum.size(); i ++){
			list.add(new AlbumInfo(listAlbum.get(i)));
		}
		return list;
	}
	
	public List<ArtistInfo> getArtist() throws SQLException, ClassNotFoundException, URISyntaxException {
		// TODO Auto-generated method stub
		List<ArtistInfo> list = new ArrayList<ArtistInfo>();
		
		List<String> listArtist = getArtistName();
		for(int i = 0; i < listArtist.size(); i ++){
			list.add(new ArtistInfo(listArtist.get(i)));
		}
		return list;
	}
	
	public List<GenreInfo> getGenre() throws SQLException, ClassNotFoundException, URISyntaxException {
		// TODO Auto-generated method stub
		List<GenreInfo> list = new ArrayList<GenreInfo>();
		
		List<String> listGenre = getGenreName();
		for(int i = 0; i < listGenre.size(); i ++){
			list.add(new GenreInfo(listGenre.get(i)));
		}
		return list;
	}
	


	public List<MediaInfo> getPlaylist(String select) throws SQLException, URISyntaxException {
		// TODO Auto-generated method stub

		List<MediaInfo> list = new ArrayList<MediaInfo>();
		PreparedStatement prep;
		prep = c.prepareStatement("select id from playlist where listname = ?;");
		prep.setString(1, select);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		
		prep.close();
		res.close();
		
		return list;
	}
	
	public List<MediaInfo> getAllMusic() throws SQLException, URISyntaxException {
		// TODO Auto-generated method stub

		List<MediaInfo> list = new ArrayList<MediaInfo>();
		Statement stat = c.createStatement();
		
		
		ResultSet res = stat.executeQuery("select id from allmusic;");
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		
		stat.close();
		res.close();
		
		return list;
	}
	
	public MediaInfo getMusic(String musicId) throws SQLException, URISyntaxException{

		String url = "";
		MediaInfo info = null;
		
		PreparedStatement prep;
		prep = c.prepareStatement("select * from allmusic where id = ?;");
		prep.setString(1, musicId);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			url = res.getString("url");
			
		}
		
		
		URI uri = new URI(url);
		
		
		MediaFile mdFile = new MediaFile(new File(uri));
		info = new MediaInfo(musicId, mdFile.getTitle(), mdFile.getArtist(), mdFile.getLength(), mdFile.getAlbum(), url );
		prep.close();
		res.close();
		
		return info;
		
	}

	public void insertData(String listName, String title, String artist,
			String length, String url, String album, String genre) throws SQLException, URISyntaxException {

		PreparedStatement prep;
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select * from allmusic where url = '" + url +"';");
		if( res.next()){
			prep = c.prepareStatement("insert into playlist values(?,?);");
			prep.setString(1, listName);
			prep.setString(2, res.getString("id"));
			prep.execute();
			prep.close();
		} else{
			prep = c.prepareStatement("insert into allmusic values(?,?,?,?,?,?,?);");
			
			prep.setString(2, title);
			prep.setString(3, length);
			prep.setString(4, album);
			prep.setString(5, artist);
			prep.setString(6, genre);
			prep.setString(7, url);
			prep.execute();
			prep.close();
			
			
			
			res = stat.executeQuery("select * from allmusic where url = '" + url +"';");
			
			prep = c.prepareStatement("insert into playlist values(?,?);");
			prep.setString(1, listName);
			res.next();
			prep.setString(2, res.getString("id"));
			prep.execute();
			prep.close();
		}
		
		
		res.close();
		stat.close();
		
	}
	
	public void insertIntoAllmusic(String title, String artist,
			String length, String url, String album, String genre) throws SQLException, URISyntaxException{
		PreparedStatement prep;
		prep = c.prepareStatement("insert into allmusic values(?,?,?,?,?,?,?);");
		prep.setString(2, title);
		prep.setString(3, length);
		prep.setString(4, album);
		prep.setString(5, artist);
		prep.setString(6, genre);
		prep.setString(7, url);
		prep.execute();
		prep.close();
		
		
		
		
	}

	public void deleteData(String id) throws SQLException {
		PreparedStatement prep = c
				.prepareStatement("DELETE from playlist where ID=?;");
		prep.setString(1, id);
		prep.execute();
		prep.close();
		

	}
	
	public void deleteAllMusicData(String id) throws SQLException{
		PreparedStatement prep = c
				.prepareStatement("DELETE from allmusic where ID=?;");
		prep.setString(1, id);
		prep.execute();
		prep.close();
		
	}



}