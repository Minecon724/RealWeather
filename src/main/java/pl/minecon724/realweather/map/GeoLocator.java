package pl.minecon724.realweather.map;

import java.io.IOException;
import java.net.InetAddress;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Location;

import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class GeoLocator {
    private WebServiceClient client;

    public GeoLocator(WebServiceClient client) {
        this.client = client;
    }

    public static GeoLocator fromConfig(int accountId, String apiKey) {
        return new GeoLocator(
            new WebServiceClient.Builder(accountId, apiKey)
                    .host("geolite.info").build()
        );
        
    }

    /**
     * get location by IP
     * @param address IP
     * @return geolocation in vector
     * @throws GeoIp2Exception 
     * @throws IOException 
     */
    public double[] getLocation(InetAddress address)
            throws GeoIPException {

        Location location;
        try {
            location = client.city(address).getLocation();
        } catch (IOException | GeoIp2Exception e) {
            throw new GeoIPException(e.getMessage());
        }

        return new double[] {
            location.getLatitude(),
            location.getLongitude()
        };
    }
}
