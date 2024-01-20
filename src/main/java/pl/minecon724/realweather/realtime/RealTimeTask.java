package pl.minecon724.realweather.realtime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.SubLogger;

public class RealTimeTask extends BukkitRunnable {
    private SubLogger subLogger = new SubLogger("timer");
    double scale;
    ZoneId timezone;
    List<World> worlds;

    public RealTimeTask(double scale, ZoneId timezone, List<World> worlds) {
        this.scale = scale;
        this.timezone = timezone;
        this.worlds = worlds;
    }

    @Override
    public void run() {
        double now = ZonedDateTime.now(timezone).toInstant().getEpochSecond();
        now /= 3600; // to hour

        // explaination in PlayerTimeSyncTask line 47
        now *= 1000; // reallife s to mc ticks
        now -= 6000; // 0t is actually 6:00

        now *= scale; // scale
        now %= 24000;

        long time = (long) now;

        for (World w : worlds) {
            w.setFullTime(time);
            subLogger.info("Updated time for %s (to %d)", w.getName(), time);
        }
    }
}