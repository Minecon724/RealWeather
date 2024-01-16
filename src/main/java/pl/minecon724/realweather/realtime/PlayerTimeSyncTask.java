package pl.minecon724.realweather.realtime;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class PlayerTimeSyncTask extends BukkitRunnable {
    private WorldMap worldMap = WorldMap.getInstance();
    private Logger logger = Logger.getLogger("timezone sync");

    private List<World> worlds;

    public PlayerTimeSyncTask(List<World> worlds) {
        this.worlds = worlds;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!worlds.contains(player.getWorld())) {
                player.resetPlayerTime();
                continue;
            }

            Coordinates coordinates;

            try {
                coordinates = worldMap.getCoordinates(player);
            } catch (GeoIPException e) {
                logger.warning(
                    String.format("Unable to determine GeoIP for %s (%s)",
                    player.getAddress().getHostString()));

                continue;
            }

            // longitude
            // / 15 as 15 degrees is 1 hour
            // * 60 to minutes
            // * 60 again to seconds
            // * 20 to ticks
            long offset = (long) (coordinates.longitude / 15 * 72000);

            player.setPlayerTime(offset, true);
        }
    }
    
}
