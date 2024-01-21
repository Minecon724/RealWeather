package pl.minecon724.realweather.weather;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pl.minecon724.realweather.RealWeatherPlugin;
import pl.minecon724.realweather.SubLogger;
import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.weather.exceptions.ModuleDisabledException;
import pl.minecon724.realweather.weather.exceptions.WeatherProviderException;
import pl.minecon724.realweather.weather.provider.Provider;

public class WeatherCommander {
    private WorldMap worldMap = WorldMap.getInstance();
    private RealWeatherPlugin plugin;

    private boolean enabled;
    private List<String> worldNames;
    private String providerName;
    private Provider provider;
    private ConfigurationSection providerConfig;

    private GetStateTask getStateTask;

    private SubLogger subLogger = new SubLogger("weather");

    public WeatherCommander(RealWeatherPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize weather commander
     * @param config "weather" ConfigurationSection
     * @throws ModuleDisabledException if disabled in config
     * @throws ProviderException if invalid provider config
     */
    public void init(ConfigurationSection config)
            throws ModuleDisabledException, IllegalArgumentException {

        enabled = config.getBoolean("enabled");

        if (!enabled)
            throw new ModuleDisabledException();

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
        } catch (WeatherProviderException e) {
            subLogger.severe("Provider test failed, errors may occur");
            e.printStackTrace();
        }

        plugin.getServer().getPluginManager().registerEvents(
            new WeatherChanger(worldNames), plugin);

        subLogger.info("done");
    }

    public void start() {
        getStateTask = new GetStateTask(plugin, provider, worldMap);
        
        getStateTask.runTaskTimerAsynchronously(plugin, 0, 1200);
        subLogger.info("started", new Object[0]);
    }
}
