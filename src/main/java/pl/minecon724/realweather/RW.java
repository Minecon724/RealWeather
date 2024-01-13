package pl.minecon724.realweather;

import java.time.ZoneId;
import java.util.List;

import com.maxmind.geoip2.WebServiceClient;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.realtime.RTTask;
import pl.minecon724.realweather.weather.GetStateTask;
import pl.minecon724.realweather.weather.WeatherCommander;
import pl.minecon724.realweather.weather.exceptions.DisabledException;
import pl.minecon724.realweather.weather.exceptions.ProviderException;
import pl.minecon724.realweather.weather.provider.OpenWeatherMapProvider;
import pl.minecon724.realweather.weather.provider.Provider;

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
		} catch (DisabledException e) {
			getLogger().info("Weather module disabled");
		} // we leave ProviderException

		if (realtimeSec.getBoolean("enabled")) {
			ZoneId zone;
			try {
				zone = ZoneId.of(realtimeSec.getString("timezone"));
			} catch (Exception e) {
				zone = ZoneId.systemDefault();
			}
			new RTTask(
				realtimeSec.getDouble("timeScale"),
				zone,
				realtimeSec.getStringList("worlds")
			).runTaskTimer(this, 0, realtimeSec.getLong("interval"));
		}
		
		long end = System.currentTimeMillis();
		this.getLogger().info( String.format( this.getName() + " enabled! (%s ms)", Long.toString( end-start ) ) );
	}
}
