package pl.minecon724.realweather.weather.provider;

import pl.minecon724.realweather.weather.WeatherState;

public interface Provider {
	public void init();
	public WeatherState.State request_state(double[] coords);
	public String getName();
}
