package pl.minecon724.realweather.realtime;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import pl.minecon724.realweather.RealWeatherPlugin;
import pl.minecon724.realweather.weather.exceptions.ModuleDisabledException;

public class RealTimeCommander implements Listener {
    private RealWeatherPlugin plugin;

    private List<String> worldNames;
    private double scale;
    private ZoneId timezone;
    private boolean perPlayer;

    private volatile List<World> worlds = new ArrayList<>();
    private Map<World, Boolean> savedGamerule = new HashMap<>();

    private RealTimeTask task;
    private PlayerTimeSyncTask playerTimeSyncTask;
    
    public RealTimeCommander(RealWeatherPlugin plugin) {
        this.plugin = plugin;
    }

    public void init(ConfigurationSection config)
            throws ModuleDisabledException {

        if (!config.getBoolean("enabled"))
            throw new ModuleDisabledException();

		try {
			timezone = ZoneId.of(config.getString("timezone"));
		} catch (Exception e) {
			timezone = ZoneId.systemDefault();
		}

        worldNames = config.getStringList("worlds");
		scale = config.getDouble("scale");
        perPlayer = config.getBoolean("per_player");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void start() {
        // to save processing, run only when necessary
        long period = (long) Math.ceil(72 / scale);
        period = Math.max(period, 1);

        task = new RealTimeTask(scale, timezone, worlds);
        task.runTaskTimer(plugin, 0, period);

        if (perPlayer) {
            playerTimeSyncTask = new PlayerTimeSyncTask(scale, worlds);
            playerTimeSyncTask.runTaskTimerAsynchronously(plugin, 0, 40);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        if (worldNames.contains(world.getName())) {
            worlds.add(world);

            savedGamerule.put(world, world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();

        worlds.remove(world);
        if (savedGamerule.containsKey(world)) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, savedGamerule.remove(world));
        }
    }
}
