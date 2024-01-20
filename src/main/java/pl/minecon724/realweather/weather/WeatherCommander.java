package pl.minecon724.realweather.weather;

import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pl.minecon724.realweather.RW;
import pl.minecon724.realweather.SubLogger;
import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.weather.exceptions.DisabledException;
import pl.minecon724.realweather.weather.provider.Provider;

public class WeatherCommander {
    private WorldMap worldMap = WorldMap.getInstance();
    private RW plugin;

    private boolean enabled;
    private List<String> worldNames;
    private String providerName;
    private Provider provider;
    private ConfigurationSection providerConfig;

    private GetStateTask getStateTask;

    private SubLogger subLogger = new SubLogger("weather");

    public WeatherCommander(RW plugin) {
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

        try {
            provider.request_state(new Coordinates(0, 0));
        } catch (IOException e) {
            subLogger.info("Provider test failed, errors may occur", new Object[0]);
            e.printStackTrace();
        }

        plugin.getServer().getPluginManager().registerEvents(
            new WeatherChanger(worldNames), plugin);

        subLogger.info("done", new Object[0]);
    }

    public void start() {
        getStateTask = new GetStateTask(plugin, provider, worldMap);
        
        getStateTask.runTaskTimerAsynchronously(plugin, 0, 1200);
        subLogger.info("started", new Object[0]);
    }
}
