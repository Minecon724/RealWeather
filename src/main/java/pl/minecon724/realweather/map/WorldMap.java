package pl.minecon724.realweather.map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class WorldMap {
    private static WorldMap INSTANCE;

    private final Type type;

    private Coordinates point;

    public static WorldMap getInstance() {
        if (INSTANCE == null)
            throw new NullPointerException("No WorldMap");
        return INSTANCE;
    }

    public WorldMap(Type type,
            Coordinates point) {
        this.type = type;
        this.point = point;
    }

    public static void init(ConfigurationSection config)
            throws IllegalArgumentException {

        Type type;

        try {
            type = Type.valueOf(config.getString("type"));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Invalid type");
        }

        Coordinates point = null;

        if (type == Type.POINT) {
            point = new Coordinates(
                config.getDouble("point.latitude"),
                config.getDouble("point.longitude")
            );

        } else if (type == Type.PLAYER) {
            GeoLocator.init(
                config.getInt("player.geolite2_accountId"), 
                config.getString("player.geolite2_api_key")
            );

        } else if (type == Type.GLOBE) {
            Globe.init(
                config.getDouble("globe.scale_latitude"),
                config.getDouble("globe.scale_longitude"),
                config.getBoolean("globe.wrap")
            );
        }

        INSTANCE = new WorldMap(type, point);
    }

    /**
     * get coordinates of player
     * @param player the player
     * @return Coordinates
     * @throws GeoIPException 
     */
    public Coordinates getCoordinates(Player player) throws GeoIPException {

        switch (this.type) {
            case POINT:
                return point;
            case PLAYER:
                if (player.getAddress().getAddress().isAnyLocalAddress())
                    throw new GeoIPException(player.getName() + "'s IP is local, check your proxy settings");
                    
                return GeoLocator.getCoordinates(
                    player.getAddress().getAddress()
                );
            case GLOBE:
                return Globe.toCoordiates(player.getLocation());
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
