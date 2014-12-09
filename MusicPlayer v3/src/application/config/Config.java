package application.config;

import java.io.File;

public class Config {
	public static final String SETTING_VOLUME = "volume";
	static File configFile;
	private static Config instance;
	
	Config() {
		instance = this;
		configFile = new File("setting.cfg");
	}
	
	public int getInt(String key) {
		return 0;
	}
	
	public static Config getInstance() {
		if(instance == null)
			return new Config();
		return instance;
	}

	public double getDouble(String key) {
		return 0;
	}
}
