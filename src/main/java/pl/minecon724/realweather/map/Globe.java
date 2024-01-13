package pl.minecon724.realweather.map;

import org.bukkit.Location;

public class Globe {
    double scaleLatitide, scaleLongitude;
    boolean wrap;

    public Globe(double scaleLatitude,
                double scaleLongitude,
                boolean wrap) {
        this.scaleLatitide = scaleLatitude;
        this.scaleLongitude = scaleLongitude;
        this.wrap = wrap;
    }
    
    double wrapDouble(double min, double max, double val) {
        return min + (val - min) % (max - min);
    }

    public double[] playerPosAsCoords(Location loc) {
        double[] out = new double[] {
            loc.getX() * scaleLongitude,
            -loc.getZ() * scaleLatitide
        };

        if (wrap) {
            out[0] = Math.max(-180.0, Math.min(180.0, out[0]));
            out[1] = Math.max(-90.0, Math.min(90.0, out[1]));
        } else {
            out[0] = wrapDouble(-180.0, 180.0, out[0]);
            out[1] = wrapDouble(-90.0, 90.0, out[1]);
        }

        return out;
    }
}
