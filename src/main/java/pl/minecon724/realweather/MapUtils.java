package pl.minecon724.realweather;

import org.bukkit.Location;

public class MapUtils {
    double wrapDouble(double min, double max, double val) {
        return min + (val - min) % (max - min);
    }

    public double[] playerPosAsCoords(Location loc, double scaleLat, double scaleLon, int onExceed) {
        double[] out = new double[2];
        out[0] = loc.getX() * scaleLon;
        out[1] = -loc.getZ() * scaleLat;
        if (onExceed == 1) {
            out[0] = Math.max(-180.0, Math.min(180.0, out[0]));
            out[1] = Math.max(-90.0, Math.min(90.0, out[1]));
        } else if (onExceed == 2) {
            out[0] = wrapDouble(-180.0, 180.0, out[0]);
            out[1] = wrapDouble(-90.0, 90.0, out[1]);
        }
        return out;
    }
}
