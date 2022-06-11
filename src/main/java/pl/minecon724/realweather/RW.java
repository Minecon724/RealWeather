package pl.minecon724.realweather;

import java.util.List;

import com.maxmind.geoip2.WebServiceClient;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.minecon724.realweather.provider.OpenWeatherMapProvider;
import pl.minecon724.realweather.thirdparty.Metrics;

public class RW extends JavaPlugin {
	FileConfiguration config;

	WebServiceClient client = null;

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();

		saveDefaultConfig();
		config = getConfig();

		ConfigurationSection weatherSec = config.getConfigurationSection("weather");
		ConfigurationSection providerSec = config.getConfigurationSection("provider");
		ConfigurationSection settingsSec = config.getConfigurationSection("settings");
		ConfigurationSection messagesSec = config.getConfigurationSection("messages");

		String source = weatherSec.getString("source");
		ConfigurationSection point = weatherSec.getConfigurationSection("point");
		ConfigurationSection player = weatherSec.getConfigurationSection("player");
		ConfigurationSection map = weatherSec.getConfigurationSection("map");

		double pointLatitude = point.getDouble("latitude");
		double pointLongitude = point.getDouble("longitude");
		List<String> worlds = weatherSec.getStringList("worlds");

		String choice = providerSec.getString("choice").toLowerCase();
		ConfigurationSection providerCfg = providerSec.getConfigurationSection(choice);

		if (providerCfg == null) {
			this.getLogger().severe("Unknown provider: " + choice);
			this.getLogger().info("The plugin will disable now");
			Bukkit.getPluginManager().disablePlugin(this);
		}

		Provider provider = null;
		if (choice.equals("openweathermap")) {
			this.getLogger().info("Using OpenWeatherMap as the weather provider");
			provider = new OpenWeatherMapProvider( providerCfg.getString("apiKey") );
		}
		provider.init();

		if (source.equals("player")) {
			this.getLogger().info("Initializing GeoLite2 by MaxMind because we need it for retrieving players real world locations.");
			int accId = player.getInt("geolite2_accountId");
			String license = player.getString("geolite2_apiKey");
			client = new WebServiceClient.Builder(accId, license).host("geolite.info").build();
		}

		double scale_lat = map.getDouble("scale_lat");
		double scale_lon = map.getDouble("scale_lon");
		int on_exceed = map.getInt("on_exceed");

		boolean debug = settingsSec.getBoolean("debug");
		boolean debugDox = settingsSec.getBoolean("debugDox");

		new GetStateTask(
			provider, source, pointLatitude, pointLongitude, worlds, this.getLogger(),
			client, debug, debugDox, scale_lat, scale_lon, on_exceed,
			settingsSec.getBoolean("actionbar"), ConfigUtils.color(messagesSec.getString("actionbarInfo"))
		).runTaskTimerAsynchronously(this, 
			settingsSec.getLong("timeBeforeInitialRun"),
			settingsSec.getLong("timeBetweenRecheck")
		);

		Metrics metrics = new Metrics(this, 15020);
		metrics.addCustomChart(new Metrics.SimplePie("source_type", () -> {
			return source;
		}));
		
		long end = System.currentTimeMillis();
		this.getLogger().info( String.format( this.getName() + " enabled! (%s ms)", Long.toString( end-start ) ) );
	}
}
