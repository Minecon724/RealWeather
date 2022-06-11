package pl.minecon724.realweather.realtime;

import java.time.OffsetDateTime;
import java.time.ZoneId;
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
        double time = OffsetDateTime.now(timezone).toEpochSecond() / (72 * timeScale) - 18000;
        Bukkit.getLogger().info(Double.toString(time));
        for (World w : worlds) {
            w.setTime((long)time);
        }
    }
}
