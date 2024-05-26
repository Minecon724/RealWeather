package pl.minecon724.realweather.weather;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameRule;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import pl.minecon724.realweather.SubLogger;
import pl.minecon724.realweather.weather.WeatherState.ConditionSimple;
import pl.minecon724.realweather.weather.WeatherState.State;
import pl.minecon724.realweather.weather.events.WeatherSyncEvent;

public class WeatherChanger implements Listener {
    private List<String> worldNames;
    private List<World> worlds = new ArrayList<>();
    private SubLogger subLogger = new SubLogger("weatherchanger");

    public WeatherChanger(List<String> worldNames) {
        this.worldNames = worldNames;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        subLogger.info("World %s is loading", world.getName());

        if (worldNames.contains(world.getName())) {
            worlds.add(world);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            subLogger.info("World %s has been registered", world.getName());

        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);

        worlds.remove(world);
        subLogger.info("World %s unloaded", world.getName());
    }

    @EventHandler
    public void onWeatherSync(WeatherSyncEvent event) {
        Player player = event.getPlayer();
        State state = event.getState();

        if (player != null) {
            subLogger.info("new weather for %s: %s %s", player.getName(), state.getCondition().name(), state.getLevel().name());

            player.setPlayerWeather(
                state.getSimple() == ConditionSimple.CLEAR
                ? WeatherType.CLEAR : WeatherType.DOWNFALL
            );

        } else {
            subLogger.info("new weather: %s %s", state.getCondition().name(), state.getLevel().name());

            worlds.forEach(w -> {
                w.setStorm(state.getSimple() != ConditionSimple.CLEAR);
                w.setThundering(state.getSimple() == ConditionSimple.THUNDER);
            });
            
        }

    }

}
