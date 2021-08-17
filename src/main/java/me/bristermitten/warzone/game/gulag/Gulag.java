package me.bristermitten.warzone.game.gulag;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class Gulag {
    private final Game game;

    private @Nullable GulagPlayers gulagPlayers = null;

    private Deque<WarzonePlayer> playersInQueue = new ArrayDeque<>();

    public Gulag(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public GulagPlayers getGulagPlayers() {
        return gulagPlayers;
    }

    public void setGulagPlayers(GulagPlayers gulagPlayers) {
        this.gulagPlayers = gulagPlayers;
    }

    void join(WarzonePlayer player) {
        playersInQueue.add(player);
    }

    Deque<WarzonePlayer> getPlayersInQueue() {
        return playersInQueue;
    }


    /**
     * Simple tuple type storing the 2 players currently duelling in the gulag
     */
    record GulagPlayers(
            UUID a,
            UUID b
    ) {
    }
}
