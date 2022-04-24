package pl.minecon724.realweather;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import com.maxmind.geoip2.WebServiceClient;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.minecon724.realweather.provider.OpenWeatherMapProvider;

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

		if (settingsSec.getBoolean("debug")) {
			this.getLogger().setLevel(Level.ALL);
		}

		String source = weatherSec.getString("source");
		ConfigurationSection point = weatherSec.getConfigurationSection("point");
		ConfigurationSection player = weatherSec.getConfigurationSection("player");

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
			this.getLogger().info("This product includes GeoLite2 data created by MaxMind, available from https://maxmind.com");
			int accId = player.getInt("geolite2_accountId");
			String license = player.getString("geolite2_apiKey");
			client = new WebServiceClient.Builder(accId, license).build();
		}

		new GetStateTask(
			provider, source, pointLatitude, pointLongitude, worlds, this.getLogger(), client
		).runTaskTimerAsynchronously(this, 
			settingsSec.getLong("timeBeforeInitialRun"),
			settingsSec.getLong("timeBetweenRecheck")
		);

		new Metrics(this, 15020);
		
		long end = System.currentTimeMillis();
		this.getLogger().info( String.format( this.getName() + " enabled! (%s ms)", Long.toString( end-start ) ) );
	}
}
