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
		c = DriverManager.getConnection("jdbc:sqlite:audio.db");
		Statement stat = c.createStatement();
		stat.executeUpdate("create table if not exists playlist(id integer,"
				+ "listName varchar(30)," + "title varchar(30),"
				+ "length varchar(15)," + "album varchar(30),"
				+ "artist varchar(30)," + "url text," + "primary key (id));");
	}

	public List<String> getListNames() throws SQLException {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		List<String> list1 = new ArrayList<String>();
		List<MediaInfo> list2 = getData();

		for (int i = 0; i < list2.size(); i++) {
			list1.add(list2.get(i).getListName());
		}

		for (int i = 0; i < list1.size(); i++) {
			if (!list.contains(list1.get(i)))
				list.add(list1.get(i));
		}

		return list;

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
		List<MediaInfo> list2 = getData();

		for (int i = 0; i < list2.size(); i++) {
			if (list2.get(i).getListName().equals(select))
				list.add(list2.get(i));
		}
		return list;

	}

	public void insertData(String listName, String title, String artist,
			String length, String url, String album) throws SQLException {

		PreparedStatement prep;
		// stat.executeUpdate("drop table if exists user");

		// creating table

		// inserting data
		prep = c.prepareStatement("insert into playlist values(?,?,?,?,?,?,?);");
		prep.setString(2, listName);
		prep.setString(3, title);
		prep.setString(4, length);
		prep.setString(5, album);
		prep.setString(6, artist);
		prep.setString(7, url);
		prep.execute();
		// TODO Auto-generated method stub

	}

	public List<MediaInfo> getData() throws SQLException {
		// TODO Auto-generated method stub
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		Statement stat = c.createStatement();

		String title;
		String artist;
		String length;
		String album;
		String listName;
		String url;
		String id;

		ResultSet res = stat.executeQuery("select * from playlist");

		while (res.next()) {
			listName = res.getString("listName");
			album = res.getString("album");
			title = res.getString("title");
			length = res.getString("length");
			url = res.getString("url");
			artist = res.getString("artist");
			id = res.getString("id");
			list.add(new MediaInfo(id, title, artist, length, album, listName,
					url));

		}
		return list;
	}

	public void deleteData(String id) throws SQLException {
		PreparedStatement prep = c
				.prepareStatement("DELETE from playlist where ID=?;");
		prep.setString(1, id);
		prep.execute();

	}

}