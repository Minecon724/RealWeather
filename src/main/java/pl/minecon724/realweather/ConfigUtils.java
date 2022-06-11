package pl.minecon724.realweather;

public class ConfigUtils {
    public static String parsePlaceholders(String key, String value, Object[] data) {
        if (key.equals("messages.actionbarInfo")) {
            value = value.replaceAll('%weather_full%', data[0]).replaceAll('%weather%', data[1]);
        }
        return value;
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}