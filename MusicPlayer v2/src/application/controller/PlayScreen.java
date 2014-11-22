package application.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

@SuppressWarnings("unchecked")
public class PlayScreen extends AbstractScreen {

	public static final ObservableList<String> lyric = 
	        FXCollections.observableArrayList();

	public PlayScreen(Stage primaryStage) {
		super(primaryStage);
	}
	
	@Override
	protected void initialize() {
		for(int i=0; i<50; i++)
			lyric.add("Lyric");
		try {
			ListView<String> lyricBox = (ListView<String>) 
					findNodeById("lyric");
			lyricBox.setItems(lyric);
		} 
		catch(ClassCastException e) {
			
		}
	}
}
