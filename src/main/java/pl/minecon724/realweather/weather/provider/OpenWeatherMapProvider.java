package pl.minecon724.realweather.weather.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import pl.minecon724.realweather.map.Coordinates;
import pl.minecon724.realweather.weather.WeatherState.*;
import pl.minecon724.realweather.weather.exceptions.WeatherProviderException;

public class OpenWeatherMapProvider implements Provider {

	URL endpoint;

	String apiKey;
	
	public OpenWeatherMapProvider(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public String getName() {
		return "OpenWeatherMap";
	}
	
	public void init() {
		try {
			endpoint = new URL("https://api.openweathermap.org");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public State request_state(Coordinates coordinates) throws WeatherProviderException {
		JsonObject jsonObject;
		
		try {
			URL url = new URL(
				String.format("%s/data/2.5/weather?lat=%f&lon=%f&appid=%s",
				endpoint, coordinates.latitude, coordinates.longitude, apiKey
			));

			InputStream is = url.openStream();
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(is, Charsets.UTF_8));

			JsonReader jsonReader = new JsonReader(rd);
			jsonObject = new Gson().fromJson(jsonReader, JsonObject.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WeatherProviderException("Couldn't contact openweathermap");
		}

		int stateId;
		
		try {
			stateId = jsonObject.getAsJsonArray("weather")
								.get(0).getAsJsonObject()
								.get("id").getAsInt();
			/*
			 * org.json comparison:
			 * stateId = json.getJSONArray("weather").getJSONObject(0).getInt("id");
			 * so is it truly worth it? yes see loading jsonobject from inputstream
			 */
		} catch (Exception e) {
			e.printStackTrace();
			throw new WeatherProviderException("Invalid data from openweathermap");
		}

		// Here comes the mess
		Condition condition = Condition.CLEAR;
		ConditionLevel level = ConditionLevel.LIGHT;
		if (stateId < 300) {
			condition = Condition.THUNDER;
			switch (stateId % 10) {
				case 0: // 200, 210, 230
					level = ConditionLevel.LIGHT;
					break;
				case 1: // 201, 211, 221, 231
					level = ConditionLevel.MODERATE;
					break;
				case 2: // 202, 212, 232
					level = ConditionLevel.HEAVY;
			}
		} else if (stateId < 400) {
			condition = Condition.DRIZZLE;
			switch (stateId % 10) {
				case 0: // 300, 310
					level = ConditionLevel.LIGHT;
					break;
				case 1: // 301, 311, 321
				case 3: // 313
					level = ConditionLevel.MODERATE;
					break;
				case 2: // 302, 312
				case 4: // 314
					level = ConditionLevel.HEAVY;
			}
		} else if (stateId < 600) {
			condition = Condition.RAIN;
			switch (stateId % 10) {
				case 0: // 500, 520
					level = ConditionLevel.LIGHT;
					break;
				case 1: // 501, 511, 521, 531
					level = ConditionLevel.MODERATE;
					break;
				case 2: // 502, 522
					level = ConditionLevel.HEAVY;
					break;
				case 3: // 503
				case 4: // 504
					level = ConditionLevel.EXTREME;
			}
		} else if (stateId < 700) {
			condition = Condition.SNOW;
			switch (stateId) {
				case 600:
				case 612:
				case 615:
				case 620:
					level = ConditionLevel.LIGHT;
					break;
				case 601:
				case 611:
				case 613:
				case 616:
				case 621:
					level = ConditionLevel.MODERATE;
					break;
				case 602:
				case 622:
					level = ConditionLevel.HEAVY;
			}
		} else if (stateId > 800) {
			condition = Condition.CLOUDY;
			switch (stateId) {
				case 801:
					level = ConditionLevel.LIGHT;
					break;
				case 802:
					level = ConditionLevel.MODERATE;
					break;
				case 803:
					level = ConditionLevel.HEAVY;
					break;
				case 804:
					level = ConditionLevel.EXTREME;
			}
		}
		
		State state = new State(condition, level);
		return state;
	}

	@Override
	public State[] request_state(Coordinates[] coordinates) throws WeatherProviderException {
		// OpenWeatherMap doesnt support bulk requests

		int length = coordinates.length;
		State[] states = new State[length];

		for (int i=0; i<length; i++) {
			states[i] = request_state(coordinates[i]);
		}

		return states;
	}
}
