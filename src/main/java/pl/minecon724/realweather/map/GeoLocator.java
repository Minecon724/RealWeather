package pl.minecon724.realweather.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;

import pl.minecon724.realweather.SubLogger;
import pl.minecon724.realweather.geoip.DatabaseDownloader;
import pl.minecon724.realweather.geoip.GeoIPDatabase;
import pl.minecon724.realweather.geoip.IPUtils;
import pl.minecon724.realweather.map.exceptions.GeoIPException;

public class GeoLocator {
    private static GeoLocator INSTANCE = null;

    private SubLogger subLogger = new SubLogger("geolocator");
    private GeoIPDatabase database;
    private HashMap<InetAddress, Coordinates> cache = new HashMap<>();

    public static void init(File databaseFile, String downloadUrl) throws IOException {
        INSTANCE = new GeoLocator(
            new GeoIPDatabase());

        INSTANCE.load(databaseFile, downloadUrl);
    }
    
    public GeoLocator(GeoIPDatabase database) {
        this.database = database;
    }

    public void load(File databaseFile, String downloadUrl) throws IOException {
        subLogger.info("This product includes GeoLite2 data created by MaxMind, available from https://www.maxmind.com");

        try {
            database.read(databaseFile);
        } catch (FileNotFoundException e) {
            new DatabaseDownloader(new URL(downloadUrl))
                    .download(databaseFile, false);
            database.read(databaseFile);
        }

        subLogger.info("Database: %s", INSTANCE.database.getTimestamp());
    }

    /**
     * get location by IP
     * @param address IP
     * @return geolocation in vector
     * @throws GeoIp2Exception 
     * @throws IOException 
     */
    public static Coordinates getCoordinates(InetAddress inetAddress)
            throws GeoIPException {

        Coordinates coordinates = INSTANCE.cache.get(inetAddress);
        if (coordinates != null)
            return coordinates;

        byte[] address = inetAddress.getAddress();
        byte subnet = 32;

        while (coordinates == null) {
            if (subnet == 0) {
                INSTANCE.subLogger.info("Not found :(");
                coordinates = new Coordinates(0, 0);
                break;
            }

            int query = IPUtils.toInt(address);

            coordinates = INSTANCE.database.entries.get(
                query
            );
            
            INSTANCE.subLogger.info("trying %s/%d = %d", IPUtils.toString(address), subnet, query);

            address = IPUtils.getSubnetStart(address, --subnet);
        }

        INSTANCE.subLogger.info("Done, caching");
        INSTANCE.cache.put(inetAddress, coordinates);

        return coordinates;
    }
}
