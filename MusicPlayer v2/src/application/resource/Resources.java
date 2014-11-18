package application.resource;

public class Resources {

	public static class Strings {
		public static final String goPlayScene = "To play scene";
		public static final String goLibraryScene = "To library scene";		
	}
	
	/**
	 * get StyleSheet file resources
	 * @param styleSheetFile: a file name, example: "application"
	 * @return
	 */
	public static String getStyleSheet(String styleSheetFile) {
		return Resources.class.getResource("css/" + styleSheetFile
				+ ".css").toExternalForm();
	}
	
	public static String getImage(String imageFile) {
		return Resources.class.getResource("image/" + imageFile)
				.toExternalForm();
	}
}
