package pl.minecon724.realweather.map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class WorldMap {
    private final Type type;

    private double[] point;
    private GeoLocator geoLocator;
    private Globe globe;

    public WorldMap(Type type,
                    double[] point,
                    GeoLocator geoLocator,
                    Globe globe) {
        this.type = type;
        this.point = point;
        this.geoLocator = geoLocator;
        this.globe = globe;
    }

    public static WorldMap fromConfig(ConfigurationSection config)
            throws IllegalArgumentException {

        Type type;

        try {
            type = Type.valueOf(config.getString("type"));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Invalid type");
        }

        double[] point = null;
        GeoLocator geoLocator = null;
        Globe globe = null;

        if (type == Type.POINT) {
            point = new double[] {
                config.getDouble("point.latitude"),
                config.getDouble("point.longitude")
            };

        } else if (type == Type.PLAYER) {
            geoLocator = GeoLocator.fromConfig(
                config.getInt("player.geolite2_accountId"), 
                config.getString("player.geolite2_api_key")
            );

        } else if (type == Type.GLOBE) {
            globe = new Globe(
                config.getDouble("globe.scale_latitude"),
                config.getDouble("globe.scale_longitude"),
                config.getBoolean("globe.wrap")
            );
        }

        WorldMap worldMap = new WorldMap(type, point, geoLocator, globe);

        return worldMap;

    }

    /**
     * get player position as coords
     * @param player the player
     * @return latitude, longitude
     * @throws GeoIPException 
     */
    public double[] getPosition(Player player) throws GeoIPException {

        switch (this.type) {
            case POINT:
                return point;
            case PLAYER:
                if (player.getAddress().getAddress().isAnyLocalAddress())
                    throw new GeoIPException(player.getName() + "'s IP is local, check your proxy settings");
                    
                return geoLocator.getLocation(
                    player.getAddress().getAddress());
            case GLOBE:
                return globe.playerPosAsCoords(player.getLocation());
        }

        // this wont happen because we cover each type
        return null;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        POINT, PLAYER, GLOBE
    }
}
