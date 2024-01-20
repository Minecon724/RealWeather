package pl.minecon724.realweather.weather.provider;

import java.io.IOException;

import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.weather.WeatherState;

public interface Provider {
	public void init();
	public WeatherState.State request_state(Coordinates coordinates) throws IOException;
	public String getName();
}
