package pl.minecon724.realweather;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.record.Location;

import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.minecon724.realweather.WeatherState.ConditionSimple;
import pl.minecon724.realweather.WeatherState.State;

public class GetStateTask extends BukkitRunnable {

    Provider provider;
    String source;
    double pointLatitude;
    double pointLongitude;
    List<String> worlds;
    Logger logger;
    WebServiceClient client;

    public GetStateTask(
        Provider provider, String source,
        double pointLatitude, double pointLongitude,
        List<String> worlds, Logger logger,
        WebServiceClient client
    ) {
        this.provider = provider;
        this.source = source;
        this.pointLatitude = pointLatitude;
        this.pointLongitude = pointLongitude;
        this.worlds = worlds;
        this.logger = logger;
        this.client = client;
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
                world.setThundering(state.simple == ConditionSimple.THUNDER ? true : false);
                world.setStorm(state.simple == ConditionSimple.CLEAR ? false : true);
            }
        } else if (source.equals("player")) {
            try {
                InetAddress playerIp;
                Location location;
                State state;
                double lat, lon;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerIp = p.getAddress().getAddress();
                    location = client.city(playerIp).getLocation();
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    logger.fine( String.format(
                        "%s's location is %f, %f", p.getName(), lat, lon
                        ));
                    state = provider.request_state(lat, lon);
                    logger.fine( String.format(
                        "Provider returned state %s %s for %s", state.condition.name(), state.level.name(), p.getName()
                    ));
                    p.setPlayerWeather(state.simple == ConditionSimple.CLEAR ? WeatherType.CLEAR : WeatherType.DOWNFALL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
