package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.state.StateChangeEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerStateChangeEvent extends StateChangeEvent<PlayerState, WarzonePlayer> {
    private static final HandlerList handlerList = new HandlerList();

    public PlayerStateChangeEvent(PlayerState oldState, PlayerState newState, WarzonePlayer subject) {
        super(oldState, newState, subject);
    }


    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
