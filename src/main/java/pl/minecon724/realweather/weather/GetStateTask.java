package pl.minecon724.realweather.weather;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.map.WorldMap.Type;
import pl.minecon724.realweather.map.exceptions.GeoIPException;
import pl.minecon724.realweather.weather.WeatherState.ConditionSimple;
import pl.minecon724.realweather.weather.WeatherState.State;
import pl.minecon724.realweather.weather.provider.Provider;

public class GetStateTask extends BukkitRunnable {

    Logger logger = Logger.getLogger("weather scheduler");

    Provider provider;
    WorldMap worldMap;
    List<World> worlds;

    public GetStateTask(
        Provider provider,
        WorldMap worldMap,
        List<World> worlds
    ) {
        this.provider = provider;
        this.worldMap = worldMap;
        this.worlds = worlds;
    }

    // That's a lot of variables

    @Override
    public void run() {

        if (worldMap.getType() == Type.POINT) {
            double[] position;
            try {
                position = worldMap.getPosition(null);
            } catch (GeoIPException e) { return; }
            State state = provider.request_state(position);
        
            for (World world : worlds) {
                if (world == null) return;
                world.setThundering(state.getSimple() == ConditionSimple.THUNDER ? true : false);
                world.setStorm(state.getSimple() == ConditionSimple.CLEAR ? false : true);
            }

        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                double[] position;

                try {
                    position = worldMap.getPosition(player);
                } catch (GeoIPException e) {
                    logger.info("GeoIP error for " + player.getName());
                    e.printStackTrace();
                    continue;
                }
                
                State state = provider.request_state(position);
                player.setPlayerWeather(state.getSimple() == ConditionSimple.CLEAR ? WeatherType.CLEAR : WeatherType.DOWNFALL);
            }
        }
        
    }
}
