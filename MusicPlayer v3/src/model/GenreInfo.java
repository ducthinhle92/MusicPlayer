package model;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import application.DatabaseController;

public class GenreInfo {
	
	private String name;
	private String count;
	private String length;
	
	public GenreInfo(String name) throws ClassNotFoundException, SQLException, URISyntaxException{
		this.name = name;
		List<MediaInfo> list = DatabaseController.getInstance().getGenreByName(name);
		int c = list.size();
		int l = 0;
		for(int i = 0; i < c; i ++){
			l += list.get(i).getDuration();
		}
		
		count = "" + c;
		length = l / 60 + ":"
				+ (l - 60 * (int) (l / 60));
	}
	
	public String getLength(){
		return length;

	}
	
	public String getCount(){
		return count;
	}
	
	public String getName(){
		return name;
	}


}
