package pl.minecon724.realweather.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;

import pl.minecon724.realweather.*;
import pl.minecon724.realweather.WeatherState.State;

public class OpenWeatherMapProvider implements Source {

	URL endpoint;
	
	RW main;
	String apiKey;
	
	public OpenWeatherMapProvider(RW main, String apiKey) {
		this.main = main;
		this.apiKey = apiKey;
	}
	
	public void init() {
		try {
			endpoint = new URL("https://api.openweathermap.org");
		} catch (MalformedURLException e) {
			 
		}
	}

	public State request_state(double lat, double lon) {
		JSONObject json;
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			public void run() {
				try {
					HttpURLConnection con = (HttpURLConnection) endpoint.openConnection();
					con.setRequestMethod("GET");
					int status = con.getResponseCode();
					InputStream stream = status > 299 ? con.getErrorStream() : con.getInputStream();
					BufferedReader rd = new BufferedReader(
						new InputStreamReader(stream));
					String line;
					StringBuffer content = new StringBuffer();
					while ((line = rd.readLine()) != null) {
						content.append(line);
					}
					rd.close();
					con.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		content['a']
		Condition condition;
		switch () {
		
		}
		return state;
	}
}
