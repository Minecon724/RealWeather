package pl.minecon724.realweather;

import com.maxmind.geoip2.WebServiceClient;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.realtime.RealTimeCommander;
import pl.minecon724.realweather.weather.WeatherCommander;
import pl.minecon724.realweather.weather.exceptions.DisabledException;

public class RW extends JavaPlugin {
	FileConfiguration config;

	WebServiceClient client = null;

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();

		saveDefaultConfig();
		config = getConfig();

		WorldMap worldMap = WorldMap.fromConfig(
			config.getConfigurationSection("map")
		);

		WeatherCommander weatherCommander = new WeatherCommander(worldMap, this);
		try {
			weatherCommander.init(
				config.getConfigurationSection("weather")
			);
			weatherCommander.start();
		} catch (DisabledException e) {
			getLogger().info("Weather module disabled");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}

		RealTimeCommander realTimeCommander = new RealTimeCommander(this);
		try {
			realTimeCommander.init(
				config.getConfigurationSection("time")
			);
			realTimeCommander.start();
		} catch (DisabledException e) {
			getLogger().info("Time module disabled");
		}

		long end = System.currentTimeMillis();
		this.getLogger().info( String.format( this.getName() + " enabled! (%s ms)", Long.toString( end-start ) ) );
	}
}
