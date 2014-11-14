package application;

import java.io.File;


import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLController {
	FileChooser fileChooser = new FileChooser();
	List<File> list=null;
	Stage stage;
	List<MediaPlayer> players=new ArrayList<MediaPlayer>();
	MediaView mediaView =null;
	ObservableList<String> items =FXCollections.observableArrayList();
	private Duration duration;
	@FXML private ListView<String> listFile;
	@FXML private Label fileDetail;
	@FXML private Button play,prev,next;
	@FXML private Slider volumeSlider,timeSlider;
	@FXML private Label playTime;
	@FXML protected void openFile(ActionEvent event){
		processOpenFile();
	}
    @FXML protected void openFiles(ActionEvent event){
    	processOpenFiles();
	}
    @FXML protected void exit(ActionEvent event){
    	System.exit(0);
    }
    @FXML protected void btnPrev(ActionEvent event){
    	MediaPlayer curPlayer = mediaView.getMediaPlayer();
        int i=players.indexOf(curPlayer);
        if(i>0 && i<players.size()){
       	curPlayer.stop();
    	MediaPlayer prevPlayer = players.get((players.indexOf(curPlayer) -1) % players.size());
        mediaView.setMediaPlayer(prevPlayer);
        play(prevPlayer);
        }
		
	}
   	@FXML protected void btnPlay(ActionEvent event){
    	if ("Pause".equals(play.getText())) {
            mediaView.getMediaPlayer().pause();
            play.setText("Play");
          } else {
            play(mediaView.getMediaPlayer());
            play.setText("Pause");
          }
		
	}
    @FXML protected void btnNext(ActionEvent event){
    	MediaPlayer curPlayer = mediaView.getMediaPlayer();
    	curPlayer.stop();
    	MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
        mediaView.setMediaPlayer(nextPlayer);
        play(nextPlayer);
		}
    @FXML protected void btnStop(ActionEvent event){
    	MediaPlayer curPlayer = mediaView.getMediaPlayer();
    	curPlayer.stop();
    	//play.setText("Play");
    }
    @FXML protected void btnMute(ActionEvent event){
    	mediaView.getMediaPlayer().setVolume(0);
    	volumeSlider.setValue(0);
    }
    protected void play(MediaPlayer play) {
		// TODO Auto-generated method stub
    	play.play();
    	play.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });
    	 play.setOnReady(new Runnable() {
             public void run() {
                 duration = play.getMedia().getDuration();
                 updateValues();
             }
         });
    	play.setOnEndOfMedia(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		});
    	
	}
    protected void processOpenFile() {
		// TODO Auto-generated method stub
		configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(stage);
	}
    protected void processOpenFiles() {
		// TODO Auto-generated method stub
    	configureFileChooser(fileChooser);
        list =fileChooser.showOpenMultipleDialog(stage);
           if (list != null) {
        //get list items,players
        for(int i=0;i<list.size();i++){
     	  items.add(list.get(i).getName());
     	 Media media=new Media(list.get(i).toURI().toString());
     	 MediaPlayer mediaPlayer = new MediaPlayer(media);
     	 players.add(mediaPlayer);
       }
           }
       mediaView=new MediaView(players.get(0));
       //mediaView.getMediaPlayer().setVolume(0.5);
       play(mediaView.getMediaPlayer());
       listFile.setItems(items);
       }
    private static void configureFileChooser(final FileChooser fileChooser){                           
        fileChooser.setTitle("View Folder");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All", "*.*"),
                new FileChooser.ExtensionFilter("MP3", "*.mp3")
               );
 }
	public void setStage(Stage primaryStage) {
		    stage=primaryStage;
	}
	protected void updateValues() {
		// TODO Auto-generated method stub
		if (playTime != null &&timeSlider != null && volumeSlider != null) {
		     Platform.runLater(new Runnable() {
		        public void run() {
		          Duration currentTime = mediaView.getMediaPlayer().getCurrentTime();
		          playTime.setText(formatTime(currentTime, duration));
		          timeSlider.setDisable(duration.isUnknown());
		          if (!timeSlider.isDisabled() 
		            && duration.greaterThan(Duration.ZERO) 
		            && !timeSlider.isValueChanging()) {
		              timeSlider.setValue(currentTime.divide(duration).toMillis()
		                  * 100.0);
		          }
		          if (!volumeSlider.isValueChanging()) {
		            volumeSlider.setValue((int)Math.round(mediaView.getMediaPlayer().getVolume() 
		                  * 100));
		          }
		        }
		     });
		  }
	}
	private static String formatTime(Duration elapsed, Duration duration) {
		   int intElapsed = (int)Math.floor(elapsed.toSeconds());
		   int elapsedHours = intElapsed / (60 * 60);
		   if (elapsedHours > 0) {
		       intElapsed -= elapsedHours * 60 * 60;
		   }
		   int elapsedMinutes = intElapsed / 60;
		   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
		                           - elapsedMinutes * 60;
		 
		   if (duration.greaterThan(Duration.ZERO)) {
		      int intDuration = (int)Math.floor(duration.toSeconds());
		      int durationHours = intDuration / (60 * 60);
		      if (durationHours > 0) {
		         intDuration -= durationHours * 60 * 60;
		      }
		      int durationMinutes = intDuration / 60;
		      int durationSeconds = intDuration - durationHours * 60 * 60 - 
		          durationMinutes * 60;
		      if (durationHours > 0) {
		         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
		            elapsedHours, elapsedMinutes, elapsedSeconds,
		            durationHours, durationMinutes, durationSeconds);
		      } else {
		          return String.format("%02d:%02d/%02d:%02d",
		            elapsedMinutes, elapsedSeconds,durationMinutes, 
		                durationSeconds);
		      }
		      } else {
		          if (elapsedHours > 0) {
		             return String.format("%d:%02d:%02d", elapsedHours, 
		                    elapsedMinutes, elapsedSeconds);
		            } else {
		                return String.format("%02d:%02d",elapsedMinutes, 
		                    elapsedSeconds);
		            }
		        }
		    }
	}
