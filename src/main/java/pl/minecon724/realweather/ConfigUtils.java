package pl.minecon724.realweather;

import net.md_5.bungee.api.ChatColor;

public class ConfigUtils {
    public static String parsePlaceholders(String key, String value, String[] data) {
        if (key.equals("messages.actionbarInfo")) {
            value = value.replaceAll("%weather_full%", data[0] + " " + data[1]).replaceAll("%weather%", data[1]);
        }
        return value;
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}