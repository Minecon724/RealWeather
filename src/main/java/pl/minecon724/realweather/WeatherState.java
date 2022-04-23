package pl.minecon724.realweather;

public class WeatherState {
	
	// Enums
	
	public enum Condition { THUNDER, DRIZZLE, RAIN, SNOW, CLEAR, CLOUDY };
	public enum ConditionLevel { LIGHT, MODERATE, HEAVY, EXTREME };
	public enum ConditionSimple { THUNDER, RAIN, CLEAR };
	
	// State class
	
	public static class State {
		
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

		public State(Condition condition,
		            ConditionLevel level) {
			this.condition = condition;
			this.level = level;
			this.simple = null;
			switch (condition) {
				case THUNDER:
				    this.simple = ConditionSimple.THUNDER;
				case DRIZZLE:
				case RAIN:
				case SNOW:
				    this.simple = ConditionSimple.RAIN;
				case CLEAR:
				case CLOUDY:
					this.simple = ConditionSimple.CLEAR;
			}
		}
	}
}
