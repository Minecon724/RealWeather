package pl.minecon724.realweather.weather.provider;

import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.weather.WeatherState.State;
import pl.minecon724.realweather.weather.exceptions.WeatherProviderException;

public interface Provider {
	public void init();
	public String getName();

	public State request_state(Coordinates coordinates) throws WeatherProviderException;
	public State[] request_state(Coordinates[] coordinates) throws WeatherProviderException;
}
