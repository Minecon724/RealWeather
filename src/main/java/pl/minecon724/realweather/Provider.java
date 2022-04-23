package pl.minecon724.realweather;

public interface Provider {
	public void init();
	public WeatherState.State request_state(double lat, double lon);
}
