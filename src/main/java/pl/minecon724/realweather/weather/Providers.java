package pl.minecon724.realweather.weather;

import org.bukkit.configuration.ConfigurationSection;

import pl.minecon724.realweather.weather.exceptions.ProviderException;
import pl.minecon724.realweather.weather.provider.OpenWeatherMapProvider;
import pl.minecon724.realweather.weather.provider.Provider;

public class Providers {
    /**
     * get Provider by name
     * @param name name of provider
     * @param config configuration of provider
     * @return subclass of Provider or null if invalid
     * @throws ProviderException 
     * @see Provider
     */
    public static Provider getByName(String name, ConfigurationSection config)
            throws ProviderException {

        switch (name) {
            case "openweathermap":
                return openWeatherMap(config);
        }

        return null;
    }

    public static OpenWeatherMapProvider openWeatherMap(ConfigurationSection config)
            throws ProviderException {

        return new OpenWeatherMapProvider(
            config.getString("apiKey"));
    }
}
