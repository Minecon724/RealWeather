package pl.minecon724.realweather.geoip;

import com.google.common.primitives.Ints;

public class IPUtils {
    public static byte[] getSubnetStart(byte[] addressBytes, byte subnet) {
        int address = toInt(addressBytes);
        int mask = 0xFFFFFFFF << (32 - subnet);

        return fromInt(address & mask);
    }

    @SuppressWarnings("null")
    public static int toInt(byte[] address) {
        return Ints.fromByteArray(address);
    }

    public static byte[] fromInt(int value) {
        return Ints.toByteArray(value);
    }

    public static String toString(byte[] address) {
        String s = "";

        for (int i=0; i<4; i++) {
            s += Integer.toString(address[i] & 0xFF);
            if (i < 3) s += ".";
        }

        return s;
    }
}
