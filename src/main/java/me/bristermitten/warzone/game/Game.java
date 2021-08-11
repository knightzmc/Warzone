package me.bristermitten.warzone.game;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.game.state.GameState;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.state.Stateful;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class Game implements Stateful<Game, GameState> {
    private final Arena arena;
    private final Set<Party> players;
    private final PartySize acceptedSize;
    private GameState state = IdlingState.INSTANCE;

    public Game(Arena arena, Set<Party> players, PartySize acceptedSize) {
        this.arena = arena;
        this.players = new HashSet<>(players);
        this.acceptedSize = acceptedSize;
    }

    public Arena getArena() {
        return arena;
    }

    Set<Party> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    @Override
    public void setCurrentState(@NotNull GameState currentState) {
        this.state.onLeave(this);
        this.state = currentState;
        this.state.onEnter(this);
    }

    public PartySize getAcceptedSize() {
        return acceptedSize;
    }

    public boolean isFull() {
        var currentSize = acceptedSize.getSize() * players.size();
        return currentSize >= arena.gameConfiguration().playerLimit();
    }
}
