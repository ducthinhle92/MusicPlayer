package application.config;

import java.io.File;

public class ConfigManager {
	static File configFile;
	private ConfigManager instance;
	
	ConfigManager() {
		instance = this;
		configFile = new File("setting.cfg");
	}
	
	public int getInt(String key) {
		return 0;
	}
	
	public ConfigManager getInstance() {
		if(instance == null)
			return new ConfigManager();
		return instance;
	}
}
