package pl.minecon724.realweather;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RW extends JavaPlugin {
	FileConfiguration config;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
	}
}
