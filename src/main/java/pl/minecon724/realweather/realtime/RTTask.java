package pl.minecon724.realweather.realtime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class RTTask extends BukkitRunnable {
    double timeScale;
    ZoneId timezone;
    List<World> worlds;

    public RTTask(double timeScale, ZoneId timezone, List<String> worlds) {
        this.timeScale = timeScale;
        this.timezone = timezone;
        this.worlds = new ArrayList<World>();
        for (String s : worlds) {
            World world = Bukkit.getWorld(s);
            if (world == null) continue;
            this.worlds.add(world);
        }
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
