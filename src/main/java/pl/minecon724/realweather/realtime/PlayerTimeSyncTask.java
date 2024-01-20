package pl.minecon724.realweather.realtime;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.SubLogger;
import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.map.WorldMap;
import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class PlayerTimeSyncTask extends BukkitRunnable {
    private WorldMap worldMap = WorldMap.getInstance();
    private SubLogger subLogger = new SubLogger("playertime");

    private double scale;
    private List<World> worlds;

    public PlayerTimeSyncTask(double scale, List<World> worlds) {
        this.scale = scale;
        this.worlds = worlds;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!worlds.contains(player.getWorld())) {
                subLogger.info("resetting %s's time as they're in an excluded world", player.getName());
                player.resetPlayerTime();
                continue;
            }

            Coordinates coordinates;

            try {
                coordinates = worldMap.getCoordinates(player);
            } catch (GeoIPException e) {
                subLogger.info("Unable to determine GeoIP for %s (%s)",
                    player.getAddress().getHostString(), e.getMessage());

                continue;
            }

            /*
             * reasoning here:
             * earth is divided into timezones each covering 15 deg longitude
             * so first we find how many hours to offset
             * a day is 24h (no way)
             * in minecraft its 24000 ticks
             * so, 1h in ticks: 24000t / 24h = 1000t
             * seconds in day: 24h * 3600s = 86400s
             * seconds to ticks: 86400s * 20t = 1728000t
             * day irl is 1728000t, in minecraft its 24000t
             * for each minecraft tick, ticks irl: 1728000t / 24000t = 72t
             * we divide offset by that to sync time
             * then multiply by scale, thats obvious
             */

            double offset = coordinates.longitude / 15;
            offset *= 1000;
            offset *= scale;

            // why no modulo? because we also modify day

            long time = (long) offset;

            player.setPlayerTime(time, true);
            subLogger.info("%s's time is now off by %d ticks", player.getName(), time);
        }
    }
    
}
