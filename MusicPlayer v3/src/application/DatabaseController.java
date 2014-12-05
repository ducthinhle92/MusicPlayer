package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
		c = DriverManager.getConnection("jdbc:sqlite:audio2.db");
		Statement stat = c.createStatement();
		stat.executeUpdate("create table if not exists allmusic(id integer,"
				+ "title varchar(30),"
				+ "length varchar(15)," + "album varchar(30),"
				+ "artist varchar(30)," + "url text," + "primary key (id));");
		
		stat.executeUpdate("create table if not exists playlist(listname varchar(30),id integer);");
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
		return list;

	}
	
	public List<String> getAllMusicUrl() throws SQLException{
		List<String> listAllMusicUrl = new ArrayList<>();
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select url from allmusic;");
		while(res.next()){
			listAllMusicUrl.add(res.getString("url"));
		}
		return listAllMusicUrl;
	}

	public void deletePlaylist(String select) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement prep = c
				.prepareStatement("DELETE from playlist where listName=?;");
		prep.setString(1, select);
		prep.execute();

	}

	public List<MediaInfo> getPlaylist(String select) throws SQLException {
		// TODO Auto-generated method stub

		List<MediaInfo> list = new ArrayList<MediaInfo>();
		Statement stat = c.createStatement();
		PreparedStatement prep;
		prep = c.prepareStatement("select id from playlist where listname = ?;");
		prep.setString(1, select);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		return list;
	}
	
	public List<MediaInfo> getAllMusic() throws SQLException {
		// TODO Auto-generated method stub

		List<MediaInfo> list = new ArrayList<MediaInfo>();
		Statement stat = c.createStatement();
		
		
		ResultSet res = stat.executeQuery("select id from allmusic;");
		while(res.next()){
			list.add(getMusic(res.getString("id")));
			
		}
		return list;
	}
	
	public MediaInfo getMusic(String musicId) throws SQLException{
		String title;
		String artist;
		String length;
		String album;
		String url;
		MediaInfo info = null;
		
		PreparedStatement prep;
		prep = c.prepareStatement("select * from allmusic where id = ?;");
		prep.setString(1, musicId);
		ResultSet res = prep.executeQuery();
		while(res.next()){
			title = res.getString("title");
			length = res.getString("length");
			album = res.getString("album");
			url = res.getString("url");
			artist = res.getString("artist");
			info = new MediaInfo(musicId, title, artist, length, album, url);
		}
		
		return info;
		
	}

	public void insertData(String listName, String title, String artist,
			String length, String url, String album) throws SQLException {

		PreparedStatement prep;
		Statement stat = c.createStatement();
		ResultSet res = stat.executeQuery("select * from allmusic where url = '" + url +"';");
		if( res.next()){
			prep = c.prepareStatement("insert into playlist values(?,?);");
			prep.setString(1, listName);
			prep.setString(2, res.getString("id"));
			prep.execute();
		} else{
			prep = c.prepareStatement("insert into allmusic values(?,?,?,?,?,?);");
			prep.setString(2, title);
			prep.setString(3, length);
			prep.setString(4, album);
			prep.setString(5, artist);
			prep.setString(6, url);
			prep.execute();
			
			res = stat.executeQuery("select * from allmusic where url = '" + url +"';");
			
			prep = c.prepareStatement("insert into playlist values(?,?);");
			prep.setString(1, listName);
			res.next();
			prep.setString(2, res.getString("id"));
			prep.execute();
		}
	}
	
	public void insertIntoAllmusic(String title, String artist,
			String length, String url, String album) throws SQLException{
		PreparedStatement prep;
		prep = c.prepareStatement("insert into allmusic values(?,?,?,?,?,?);");
		prep.setString(2, title);
		prep.setString(3, length);
		prep.setString(4, album);
		prep.setString(5, artist);
		prep.setString(6, url);
		prep.execute();
		
	}

	public void deleteData(String id) throws SQLException {
		PreparedStatement prep = c
				.prepareStatement("DELETE from playlist where ID=?;");
		prep.setString(1, id);
		prep.execute();

	}

}