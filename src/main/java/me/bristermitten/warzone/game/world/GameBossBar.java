package me.bristermitten.warzone.game.world;

import me.bristermitten.warzone.game.Game;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameBossBar {
    private final Game game;
    private final Set<UUID> viewers = new HashSet<>();

    public GameBossBar(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void addViewer(UUID uuid) {
        viewers.add(uuid);
    }

    public void removeViewer(UUID uuid) {
        viewers.remove(uuid);
    }

    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    public double getProgress() {
        return (double) game.getTimer().getTimeRemaining() / game.getTimer().getDuration();
    }
}
