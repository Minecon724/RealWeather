package pl.minecon724.realweather.map;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class GeoLocator {
    private static GeoLocator INSTANCE = null;

    private WebServiceClient client;
    private Map<InetAddress, Coordinates> cache;

    public static void init(int accountId, String apiKey) {
        INSTANCE = new GeoLocator(
            new WebServiceClient.Builder(accountId, apiKey)
            .host("geolite.info").build());
    }
    
    public GeoLocator(WebServiceClient client) {
        this.client = client;
        this.cache = new HashMap<>();
    }

    /**
     * get location by IP
     * @param address IP
     * @return geolocation in vector
     * @throws GeoIp2Exception 
     * @throws IOException 
     */
    public static Coordinates getCoordinates(InetAddress address)
            throws GeoIPException {

        GeoLocator instance = INSTANCE;

        Coordinates coordinates = null;

        coordinates = instance.lookup(address);
        if (coordinates != null)
            return coordinates;

        try {
            coordinates = Coordinates.fromGeoIpLocation(
                instance.client.city(address).getLocation()
            );
        } catch (IOException | GeoIp2Exception e) {
            throw new GeoIPException(e.getMessage());
        }

        instance.store(address, coordinates);

        return coordinates;
    }

    private Coordinates lookup(InetAddress address) {
        return this.cache.get(address);
    }

    private void store(InetAddress address, Coordinates coordinates) {
        this.cache.put(address, coordinates);
    }
}
