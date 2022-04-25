package pl.minecon724.realweather;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
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
    boolean broadcast;
    boolean debug, debugDox;
    double scaleLat, scaleLon;
    int onExceed;

    MapUtils mapUtils = new MapUtils();

    public GetStateTask(
        Provider provider, String source,
        double pointLatitude, double pointLongitude,
        List<String> worlds, Logger logger,
        WebServiceClient client, boolean broadcast,
        boolean debug, boolean debugDox,
        double scaleLat, double scaleLon,
        int onExceed
    ) {
        this.provider = provider;
        this.source = source;
        this.pointLatitude = pointLatitude;
        this.pointLongitude = pointLongitude;
        this.worlds = worlds;
        this.logger = logger;
        this.client = client;
        this.broadcast = broadcast;
        this.debug = debug;
        this.debugDox = debugDox;
        this.scaleLat = scaleLat;
        this.scaleLon = scaleLon;
        this.onExceed = onExceed;
    }

    @Override
    public void run() {
        logger.info("Refreshing weather by " + source);
        if (source.equals("point")) {
            State state = provider.request_state(pointLatitude, pointLongitude);
            if (debug) logger.info(String.format("Provider returned state %s %s (%s)", state.condition.name(), state.level.name(), state.simple.name()));
            for (String w : worlds) {
                World world = Bukkit.getWorld(w);
                if (world == null) continue;
                world.setThundering(state.simple == ConditionSimple.THUNDER ? true : false);
                world.setStorm(state.simple == ConditionSimple.CLEAR ? false : true);
            }
        } else if (source.equals("player")) {
            InetAddress playerIp = null;
            Player curr = null;
            try {
                Location location;
                State state;
                double lat, lon;
                CityResponse city;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    curr = p;
                    playerIp = p.getAddress().getAddress();
                    city = client.city(playerIp);
                    location = city.getLocation();
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    if (debugDox) logger.info( String.format( "%s's real location is %f, %f", p.getName(), lat, lon ));
                    state = provider.request_state(lat, lon);
                    if (debug) logger.info( String.format(
                        "Provider returned state %s %s for %s", state.condition.name(), state.level.name(), p.getName()
                    ));
                    p.setPlayerWeather(state.simple == ConditionSimple.CLEAR ? WeatherType.CLEAR : WeatherType.DOWNFALL);
                    if (broadcast) p.sendMessage( String.format("%s %s in %s", state.level.name(), state.condition.name(), city.getCity().getName()) );
                }
            } catch (AddressNotFoundException e) {
                logger.warning(String.format("%s's IP address (%s) is not a public IP address, therefore we can't retrieve their location.", curr.getName(), playerIp.toString()));
                logger.warning("Check your proxy settings if you believe that this is an error.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (source.equals("map")) {
            double[] coords;
            double lat, lon;
            State state;
            for (Player p : Bukkit.getOnlinePlayers()) {
                coords = mapUtils.playerPosAsCoords(p.getLocation(), scaleLat, scaleLon, onExceed);
                lon = coords[0];
                lat = coords[1];
                logger.info( String.format( "%s's location is %f, %f", p.getName(), lat, lon ));
                state = provider.request_state(lat, lon);
                if (debug) logger.info( String.format(
                        "Provider returned state %s %s for %f, %f", state.condition.name(), state.level.name(), lat, lon
                    ));
                if (broadcast) p.sendMessage( String.format("%s %s in %f, %f", 
                    state.level.name(), state.condition.name(), lat, lon
                ) );
            }
        }
    }
}
