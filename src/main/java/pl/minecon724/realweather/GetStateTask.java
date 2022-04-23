package pl.minecon724.realweather;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.WeatherState.State;

public class GetStateTask extends BukkitRunnable {

    Provider provider;
    String source;
    double pointLatitude;
    double pointLongitude;
    List<String> worlds;
    Logger logger;

    public GetStateTask(
        Provider provider, String source,
        double pointLatitude, double pointLongitude,
        List<String> worlds, Logger logger
    ) {
        this.provider = provider;
        this.source = source;
        this.pointLatitude = pointLatitude;
        this.pointLongitude = pointLongitude;
        this.worlds = worlds;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.fine("Refreshing weather by " + source);
        if (source.equals("point")) {
            State state = provider.request_state(pointLatitude, pointLongitude);
            logger.fine(String.format("Provider returned state %s %s", state.condition.name(), state.level.name()));
            for (String w : worlds) {
                World world = Bukkit.getWorld(w);
                if (world == null) continue;
                switch (state.simple) {
                    case CLEAR:
                        world.setThundering(false);
                        world.setStorm(false);
                    case RAIN:
                        world.setThundering(false);
                        world.setStorm(true);
                    case THUNDER:
                        world.setThundering(false);
                        world.setStorm(true);
                }
            }
        }
    }
    
}
