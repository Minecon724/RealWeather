package pl.minecon724.realweather.weather;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.map.WorldMap.Type;
import pl.minecon724.realweather.map.exceptions.GeoIPException;
import pl.minecon724.realweather.weather.WeatherState.State;
import pl.minecon724.realweather.weather.events.WeatherSyncEvent;
import pl.minecon724.realweather.weather.provider.Provider;

public class GetStateTask extends BukkitRunnable {

    Logger logger = Logger.getLogger("weather scheduler");

    Provider provider;
    WorldMap worldMap;

    State storedState;
    Map<Player, State> playerStoredState = new HashMap<>();
    PluginManager pluginManager = Bukkit.getPluginManager();

    public GetStateTask(
        Provider provider,
        WorldMap worldMap
    ) {
        this.provider = provider;
        this.worldMap = worldMap;
    }

    // That's a lot of variables

    @Override
    public void run() {

        if (worldMap.getType() == Type.POINT) {
            Coordinates coordinates;
            try {
                coordinates = worldMap.getCoordinates(null);
            } catch (GeoIPException e) { return; }
            State state = provider.request_state(coordinates);
            
            if (!state.equals(storedState)) {
                pluginManager.callEvent(
                    new WeatherSyncEvent(null, storedState, state)
                );

                storedState = state;
            }

        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Coordinates coordinates;

                try {
                    coordinates = worldMap.getCoordinates(player);
                } catch (GeoIPException e) {
                    logger.info("GeoIP error for " + player.getName());
                    e.printStackTrace();
                    continue;
                }
                
                State state = provider.request_state(coordinates);

                if (!state.equals(playerStoredState.get(player))) {
                    pluginManager.callEvent(
                        new WeatherSyncEvent(
                            player,
                            playerStoredState.get(player),
                            state)
                    );

                    playerStoredState.put(player, state);
                }
            }
        }
        
    }
}
