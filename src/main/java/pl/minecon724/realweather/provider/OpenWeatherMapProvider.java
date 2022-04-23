package pl.minecon724.realweather.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;

import pl.minecon724.realweather.Provider;
import pl.minecon724.realweather.WeatherState.*;

public class OpenWeatherMapProvider implements Provider {

	URL endpoint;
	
	String apiKey;
	
	public OpenWeatherMapProvider(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public void init() {
		try {
			endpoint = new URL("https://api.openweathermap.org");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public State request_state(double lat, double lon) {
		JSONObject json = new JSONObject();
		
		try {
			URL url = new URL(
				endpoint + String.format("/data/2.5/weather?lat=%s&lon=%s&appid=%s",
				Double.toString(lat), Double.toString(lon), apiKey
			));

			InputStream is = url.openStream();
			BufferedReader rd = new BufferedReader( new InputStreamReader(is, Charset.forName("UTF-8")) );
			StringBuilder sb = new StringBuilder();
			int c;
			while ((c = rd.read()) != -1) {
				sb.append((char) c);
			}
			is.close();
			json = new JSONObject(sb.toString());
		} catch (Exception e) { e.printStackTrace(); }

		int stateId = json.getJSONArray("weather")
			.getJSONObject(0).getInt("id");

		// Here comes the mess
		Condition condition = Condition.CLEAR;
		ConditionLevel level = ConditionLevel.LIGHT;
		if (stateId < 300) {
			condition = Condition.THUNDER;
			switch (stateId) {
				case 200:
				case 210:
				case 230:
					level = ConditionLevel.LIGHT;
				case 201:
				case 211:
				case 221:
				case 231:
					level = ConditionLevel.MODERATE;
				case 202:
				case 212:
				case 232:
					level = ConditionLevel.HEAVY;
			}
		} else if (stateId < 400) {
			condition = Condition.DRIZZLE;
			switch (stateId) {
				case 300:
				case 310:
					level = ConditionLevel.LIGHT;
				case 301:
				case 311:
				case 313:
				case 321:
					level = ConditionLevel.MODERATE;
				case 302:
				case 312:
				case 314:
					level = ConditionLevel.HEAVY;
			}
		} else if (stateId < 600) {
			condition = Condition.RAIN;
			switch (stateId) {
				case 500:
				case 520:
					level = ConditionLevel.LIGHT;
				case 501:
				case 511:
				case 521:
				case 531:
					level = ConditionLevel.MODERATE;
				case 502:
				case 522:
					level = ConditionLevel.HEAVY;
				case 503:
				case 504:
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
				case 601:
				case 611:
				case 613:
				case 616:
				case 621:
					level = ConditionLevel.MODERATE;
				case 602:
				case 622:
					level = ConditionLevel.HEAVY;
			}
		} else if (stateId > 800) {
			condition = Condition.CLOUDY;
			switch (stateId) {
				case 801:
					level = ConditionLevel.LIGHT;
				case 802:
					level = ConditionLevel.MODERATE;
				case 803:
					level = ConditionLevel.HEAVY;
				case 804:
					level = ConditionLevel.EXTREME;
			}
		}
		State state = new State(condition, level);
		return state;
	}
}
