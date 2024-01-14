package pl.minecon724.realweather.weather;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import pl.minecon724.realweather.weather.WeatherState.State;
import pl.minecon724.realweather.weather.events.WeatherSyncEvent;

public class WeatherChanger implements Listener {
    private List<String> worldNames;
    private List<World> worlds = new ArrayList<>();

    public WeatherChanger(List<String> worldNames) {
        this.worldNames = worldNames;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        if (worldNames.contains(world.getName()))
            worlds.add(world);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();

        worlds.remove(world);
    }

    @EventHandler
    public void onWeatherSync(WeatherSyncEvent event) {
        Player player = event.getPlayer();
        State state = event.getState();

        if (player != null) {
            player.sendMessage("local: " + state.getCondition().name() + " " 
            + state.getLevel().name() + " " + state.getSimple().name());
        } else {
            Bukkit.getServer().broadcastMessage("global: " + state.getCondition().name() + " " 
            + state.getLevel().name() + " " + state.getSimple().name());
        }

    }

}
