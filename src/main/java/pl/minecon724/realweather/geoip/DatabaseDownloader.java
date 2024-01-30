package pl.minecon724.realweather.geoip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import pl.minecon724.realweather.SubLogger;

public class DatabaseDownloader {
    private SubLogger subLogger = new SubLogger("download");
    private URL downloadUrl;

    public DatabaseDownloader(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getDate(boolean ipv6) throws IOException {
        URL url = new URL(downloadUrl, ipv6 ? "ipv6.geo.gz" : "ipv4.geo.gz");
        URLConnection connection = url.openConnection();
        connection.connect();

        long lastModified = connection.getHeaderFieldDate("last-modified", 0);
        return lastModified;
    }

    // TODO verify
    public void download(File file, boolean ipv6) throws IOException {
        URL url = new URL(downloadUrl, ipv6 ? "ipv6.geo.gz" : "ipv4.geo.gz");

        URLConnection connection = url.openConnection();
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
        
        file.getParentFile().mkdirs();
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel fileChannel = fileOutputStream.getChannel();

        long size = connection.getHeaderFieldLong("Content-Length", 0);
        long position = 0;

        while (position < size) {
            position += fileChannel.transferFrom(readableByteChannel, position, 1048576);
            subLogger.info("%d%%", (int)(1.0 * position / size * 100));
        }


        fileChannel.close();
        fileOutputStream.close();
        readableByteChannel.close();
        inputStream.close(); // ok
    }
}
