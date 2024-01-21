package pl.minecon724.realweather.map;

public class Coordinates {
    public double latitude, longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * converts Coordinates to double array
     * @return array in order of latitude, longitude
     */
    public double[] toDoubleArray() {
        return new double[] {
            this.latitude,
            this.longitude
        };
    }

    public Coordinates clamp() {
        this.latitude = Math.max(-90.0, Math.min(90.0, this.latitude));
        this.longitude = Math.max(-180.0, Math.min(180.0, this.longitude));

        return this;
    }

    public Coordinates wrap() {
        this.latitude = wrapDouble(-90.0, 90.0, this.latitude);
        this.longitude = wrapDouble(-180.0, 180.0, this.longitude);

        return this;
    }

    private static double wrapDouble(double min, double max, double val) {
        return min + (val - min) % (max - min);
    }
}
