package pl.minecon724.realweather.realtime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class RealTimeTask extends BukkitRunnable {
    double timeScale;
    ZoneId timezone;
    List<World> worlds;

    public RealTimeTask(double timeScale, ZoneId timezone, List<World> worlds) {
        this.timeScale = timeScale;
        this.timezone = timezone;
        this.worlds = worlds;
    }

    @Override
    public void run() {
        long now = ZonedDateTime.now(timezone).toInstant().getEpochSecond();
        now *= timeScale;
        now %= 86400;

        // day irl is 86400 secs
        // in game, its 1200 secs
        // to align, 86400 / 1200 = 72
        // then we convert to ticks by multiplying 20 (1s = 20t)
        // we subtract 24000 - 18000 = 6000 because 18000 is midnight
        double time = (now / 72.0) * 20 - 6000;
        time %= 24000;

        for (World w : worlds) {
            w.setFullTime((long)time);
        }
    }
}
