package pl.minecon724.realweather.realtime;

import java.time.ZoneId;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import pl.minecon724.realweather.RW;
import pl.minecon724.realweather.weather.exceptions.DisabledException;

public class RealTimeCommander implements Listener {
    RW plugin;

    List<String> worldNames;
    double scale;
    ZoneId timezone;

    RealTimeTask task;
    
    public RealTimeCommander(RW plugin) {
        this.plugin = plugin;
    }

    public void init(ConfigurationSection config)
            throws DisabledException {

        if (!config.getBoolean("enabled"))
            throw new DisabledException();

		try {
			timezone = ZoneId.of(config.getString("timezone"));
		} catch (Exception e) {
			timezone = ZoneId.systemDefault();
		}

        worldNames = config.getStringList("worlds");

		scale = config.getDouble("scale");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void start() {
        task = new RealTimeTask(scale, timezone);

        task.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        if (worldNames.contains(world.getName()))
            task.worlds.add(world);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();

        task.worlds.remove(world);
    }
}
