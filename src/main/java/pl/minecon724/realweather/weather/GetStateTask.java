package pl.minecon724.realweather.weather;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.RealWeatherPlugin;
import pl.minecon724.realweather.SubLogger;
import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.map.WorldMap.Type;
import pl.minecon724.realweather.map.exceptions.GeoIPException;
import pl.minecon724.realweather.weather.WeatherState.State;
import pl.minecon724.realweather.weather.events.WeatherSyncEvent;
import pl.minecon724.realweather.weather.exceptions.WeatherProviderException;
import pl.minecon724.realweather.weather.provider.Provider;

public class GetStateTask extends BukkitRunnable {

    private SubLogger subLogger = new SubLogger("weather updater");

    private RealWeatherPlugin plugin;
    private Provider provider;
    private WorldMap worldMap;

    private State storedState;
    private Map<Player, State> playerStoredState = new HashMap<>();
    private PluginManager pluginManager = Bukkit.getPluginManager();

    public GetStateTask(
        RealWeatherPlugin plugin,
        Provider provider,
        WorldMap worldMap
    ) {
        this.plugin = plugin;
        this.provider = provider;
        this.worldMap = worldMap;
    }

    private void callEvent(Player player, State storedState, State state) {
        new BukkitRunnable() {

            @Override
            public void run() {
                pluginManager.callEvent(
                    new WeatherSyncEvent(player, storedState, state)
                );
            }
            
        }.runTask(plugin);
    }

    @Override
    public void run() {
        try {
            if (worldMap.getType() == Type.POINT) {
                Coordinates coordinates;
                try {
                    coordinates = worldMap.getCoordinates(null);
                } catch (GeoIPException e) { return; }
                State state = provider.request_state(coordinates);
                
                if (!state.equals(storedState)) {
                    callEvent(null, storedState, state);
                    storedState = state;
                }

            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Coordinates coordinates;

                    try {
                        coordinates = worldMap.getCoordinates(player);
                    } catch (GeoIPException e) {
                        subLogger.info("GeoIP error for %s", player.getName());
                        e.printStackTrace();
                        continue;
                    }
                    
                    State state = provider.request_state(coordinates);

                    if (!state.equals(playerStoredState.get(player))) {
                        callEvent(player,
                                playerStoredState.get(player),
                                state);

                        playerStoredState.put(player, state);
                    }
                }
            }
        } catch (WeatherProviderException e) {
            subLogger.info("Weather provider error");
            e.printStackTrace();
        }
        
    }
}
