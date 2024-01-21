package pl.minecon724.realweather;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.realtime.RealTimeCommander;
import pl.minecon724.realweather.weather.WeatherCommander;
import pl.minecon724.realweather.weather.exceptions.ModuleDisabledException;

public class RealWeatherPlugin extends JavaPlugin {

	private final Logger logger = getLogger();
	
	private FileConfiguration config;

	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		config = getConfig();

		SubLogger.init(
			logger,
			config.getBoolean("logging", false)
		);

		ConfigurationSection mapConfigurationSection = config.getConfigurationSection("map");

		try {
			WorldMap.init(
				mapConfigurationSection,
				getDataFolder()
			);
		} catch (IOException e) {
			logger.severe("Unable to initialize WorldMap:");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}

		WeatherCommander weatherCommander = new WeatherCommander(this);
		try {
			weatherCommander.init(
				config.getConfigurationSection("weather")
			);
			weatherCommander.start();
		} catch (ModuleDisabledException e) {
			logger.info("Weather is disabled by user");
		} catch (IllegalArgumentException e) {
			logger.severe("Couldn't initialize weather provider:");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}

		RealTimeCommander realTimeCommander = new RealTimeCommander(this);
		try {
			realTimeCommander.init(
				config.getConfigurationSection("time")
			);
			realTimeCommander.start();
		} catch (ModuleDisabledException e) {
			logger.info("Time is disabled by user");
		}

	}
}
