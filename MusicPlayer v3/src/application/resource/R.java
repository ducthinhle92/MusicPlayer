package application.resource;

import java.net.URL;

public class R {

	public static class strings {
		public static final String goPlayScene = "To play scene";
		public static final String goLibraryScene = "To library scene";		
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
