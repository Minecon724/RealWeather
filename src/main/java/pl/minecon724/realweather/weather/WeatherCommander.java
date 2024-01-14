package pl.minecon724.realweather.weather;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pl.minecon724.realweather.RW;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.weather.exceptions.DisabledException;
import pl.minecon724.realweather.weather.provider.Provider;

public class WeatherCommander {
    WorldMap worldMap;
    RW plugin;

    boolean enabled;
    List<String> worldNames;
    String providerName;
    Provider provider;
    ConfigurationSection providerConfig;

    GetStateTask getStateTask;

    public WeatherCommander(WorldMap worldMap, RW plugin) {
        this.worldMap = worldMap;
        this.plugin = plugin;
    }
    
    /**
     * Initialize weather commander
     * @param config "weather" ConfigurationSection
     * @throws DisabledException if disabled in config
     * @throws ProviderException if invalid provider config
     */
    public void init(ConfigurationSection config)
            throws DisabledException, IllegalArgumentException {
        enabled = config.getBoolean("enabled");

        if (!enabled)
            throw new DisabledException();

        worldNames = config.getStringList("worlds");

        providerName = config.getString("provider.choice");

        // this can be null
        providerConfig = config.getConfigurationSection("provider")
            .getConfigurationSection(providerName);

        provider = Providers.getByName(providerName, providerConfig);
        if (provider == null)
            throw new IllegalArgumentException("Invalid provider: " + providerName);
        provider.init();

        plugin.getServer().getPluginManager().registerEvents(
            new WeatherChanger(worldNames), plugin);
    }

    public void start() {
        getStateTask = new GetStateTask(provider, worldMap);
        
        getStateTask.runTaskTimerAsynchronously(plugin, 0, 1200);
    }
}
