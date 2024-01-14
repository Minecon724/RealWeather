package pl.minecon724.realweather.map;

import org.bukkit.Location;

public class Globe {
    private static double[] SCALE;
    private static boolean WRAP;

    public static void init(double scaleLatitude, double scaleLongitude, boolean wrap) {
        SCALE = new double[] { scaleLatitude, scaleLongitude };
        WRAP = wrap;
    }

    public static Coordinates toCoordiates(Location loc) {
        Coordinates coordinates = new Coordinates(
            -loc.getZ() * SCALE[0],
            loc.getX() * SCALE[1]);

        if (WRAP)
            coordinates.wrap();
        else
            coordinates.clamp();

        return coordinates;
    }
}
