package me.bristermitten.warzone.game.world;

import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameBossBar {
    private final Set<UUID> viewers = new HashSet<>();

    public void addViewer(UUID uuid) {
        viewers.add(uuid);
    }

    public void removeViewer(UUID uuid) {
        viewers.remove(uuid);
    }

    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

}
