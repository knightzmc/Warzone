package me.bristermitten.warzone.game.gulag;

import io.vavr.collection.List;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class Gulag {
    private final Game game;

    private @Nullable GulagPlayers gulagPlayers = null;

    private final Deque<WarzonePlayer> playersInQueue = new ArrayDeque<>();

    public Gulag(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public @Nullable GulagPlayers getGulagPlayers() {
        return gulagPlayers;
    }

    public Option<GulagPlayers> getPlayers() {
        return Option.of(gulagPlayers);
    }

    public void setGulagPlayers(@Nullable GulagPlayers gulagPlayers) {
        this.gulagPlayers = gulagPlayers;
    }

    void join(WarzonePlayer player) {
        playersInQueue.add(player);
    }

    Deque<WarzonePlayer> getPlayersInQueue() {
        return playersInQueue;
    }

    @Override
    public String toString() {
        return "Gulag{" +
               "gulagPlayers=" + gulagPlayers +
               ", playersInQueue=" + playersInQueue +
               '}';
    }

    /**
     * Simple tuple type storing the 2 players currently duelling in the gulag
     */
    record GulagPlayers(
            UUID a,
            UUID b
    ) {
        public boolean contains(@NotNull UUID uuid) {
            return uuid.equals(a) || uuid.equals(b);
        }

        public boolean contains(WarzonePlayer player) {
            return contains(player.getPlayerId());
        }

        public List<UUID> getAsList() {
            return List.of(a, b);
        }
    }
}
