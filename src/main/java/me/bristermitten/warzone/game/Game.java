package me.bristermitten.warzone.game;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.game.state.GameState;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.party.Party;

import java.util.Set;

public class Game {
    private final Arena arena;
    private final Set<Party> players;
    private GameState state = IdlingState.INSTANCE;

    public Game(Arena arena, Set<Party> players) {
        this.arena = arena;
        this.players = players;
    }

    public Arena getArena() {
        return arena;
    }
}
