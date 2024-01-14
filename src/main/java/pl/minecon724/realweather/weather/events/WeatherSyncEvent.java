package pl.minecon724.realweather.weather.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import pl.minecon724.realweather.weather.WeatherState.State;

public class WeatherSyncEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private State oldState;
    private State state;

    public WeatherSyncEvent(Player player, State oldState, State state) {
        super(player);

        this.oldState = oldState;
        this.state = state;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public State getOldState() {
        return oldState;
    }

    public State getState() {
        return state;
    }
    
}
