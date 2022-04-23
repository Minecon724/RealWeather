package pl.minecon724.realweather;

public class WeatherState {
	
	// Enums
	
	public enum Condition { THUNDER, DRIZZLE, RAIN, SNOW, CLEAR, CLOUDY };
	public enum ConditionLevel { LIGHT, MODERATE, HEAVY, EXTREME };
	public enum ConditionSimple { THUNDER, RAIN, CLEAR };
	
	// State class
	
	public class State {
		
		// Variables
		
		Condition condition;
		ConditionLevel level;
		ConditionSimple simple;
		
		// Constructor
		
		public State(Condition condition,
					ConditionLevel level,
					ConditionSimple simple) {
			this.condition = condition;
			this.level = level;
			this.simple = simple;
		}
	}
}
