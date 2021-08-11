package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.state.StateChangeEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStateChangeEvent extends StateChangeEvent<GameState, Game> {
    private static final HandlerList handlerList = new HandlerList();

    protected GameStateChangeEvent(GameState oldState, GameState newState, Game subject) {
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
