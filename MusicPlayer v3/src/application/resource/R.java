package application.resource;

import java.net.URL;

public class R {

	public static class strings {
		public static final String goPlayScene = "To play scene";
		public static final String goLibraryScene = "To library scene";
		public static final String update_lyric = "Live update";
		public static final String cancel = "Cancel";
		public static final String save = "Save";
		public static final String edit_lyric = "Edit lyric";		
	}
	
	public static class styles {
		public static final String background_lib = "-fx-background-color: #e3edf8";
		public static final String background_play = 
				"-fx-background-color: "
				+ "linear-gradient(#000000 0%, #184a6f 45%, #184a6f 55%, #000000 100%);";

		public static final String label_info_lib = "-fx-text-fill: #2c2828";
		public static final String label_info_play = "-fx-text-fill: #ffffff";
		public static final String label_time_lib = "-fx-text-fill: #2c2828";
		public static final String label_time_play = "-fx-text-fill: #ffffff";
		
		public static final String control_pane_lib = "-fx-border-color: #000000; "
				+ "-fx-border-radius: 20; "
				+ "-fx-border-width: 1; "
				+ "-fx-border-insets: 0; "
				+ "-fx-background-color: e3edf8; "
				+ "-fx-background-radius: 20; "
				+ "-fx-opacity: 0.5;";
		public static final String control_pane_play = "-fx-border-color: #000000; "
				+ "-fx-border-radius: 20; "
				+ "-fx-border-width: 1; "
				+ "-fx-border-insets: 0; "
				+ "-fx-background-color: e3edf8; "
				+ "-fx-background-radius: 20; "
				+ "-fx-opacity: 0.2;";
		
	}
	
	public static URL getLayoutFXML(String fileLayout ) {		
		return R.class.getResource("layout/" + fileLayout + ".fxml");
	}
	
	/**
	 * get StyleSheet file resources
	 * @param styleSheetFile: a file name, example: "application"
	 * @return
	 */
	public static String getStyleSheet(String styleSheetFile) {
		return R.class.getResource("css/" + styleSheetFile
				+ ".css").toExternalForm();
	}
	
	public static String getImage(String imageFile) {
		return R.class.getResource("image/" + imageFile)
				.toExternalForm();
	}
}
