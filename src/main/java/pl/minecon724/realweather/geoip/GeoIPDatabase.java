package pl.minecon724.realweather.geoip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import pl.minecon724.realweather.map.Coordinates;

public class GeoIPDatabase {
    private byte formatVersion;
    private long timestamp;
    public HashMap<Integer, Coordinates> entries = new HashMap<>();

    public void read(File file, boolean head) throws IOException, FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);

        formatVersion = (byte) gzipInputStream.read();
        byte[] bytes = gzipInputStream.readNBytes(2);
        timestamp = recoverTime(bytes);
        
        if (!head) {
        
            byte[] address;
            Coordinates coordinates;
            
            while (true) { // TODO true?
                System.out.println(gzipInputStream.available());
                address = gzipInputStream.readNBytes(4);
                if (address.length == 0)
                    break;
    
                coordinates = recoverCoordinates(gzipInputStream.readNBytes(6));
    
                entries.put(IPUtils.toInt(address), coordinates);
            }

        }

        gzipInputStream.close();
        fileInputStream.close();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte getFormatVersion() {
        return formatVersion;
    }

    /**
     * 2 bytes to 4 bytes wow magic
     * @param bytes 2 bytes
     * @return
     */
    @SuppressWarnings("null") // TODO better way of this?
    private long recoverTime(byte[] bytes) {
        long timestamp = 1704067200; // first second of 2024
        timestamp += Shorts.fromByteArray(bytes) * 60 * 10;
        return timestamp;
    }

    /**
     * Encoded to Coordinates
     * @param bytes 3 bytes
     * @return decoded Coordinates
     */
    private Coordinates recoverCoordinates(byte[] bytes) {
        int skewedLatitude = Ints.fromBytes(
            (byte)0, bytes[0], bytes[1], bytes[2]
        );

        int skewedLongitude = Ints.fromBytes(
            (byte)0, bytes[3], bytes[4], bytes[5]
        );

        double latitude = (skewedLatitude - 900000) / 10000.0;
        double longitude = (skewedLongitude - 1800000) / 10000.0;

        return new Coordinates(latitude, longitude);
    }
}
