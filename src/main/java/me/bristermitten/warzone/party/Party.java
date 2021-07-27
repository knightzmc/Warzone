package me.bristermitten.warzone.party;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class Party {
    private static final int MAX_SIZE = 4;
    private final Set<UUID> otherPlayers;
    private UUID owner;

    Party(UUID owner, Set<UUID> otherPlayers) {
        this.owner = owner;
        if (otherPlayers.size() > MAX_SIZE) {
            throw new IllegalArgumentException("players is too big");
        }
        this.otherPlayers = otherPlayers;
    }

    void add(@NotNull UUID uuid) {
        if (otherPlayers.size() == MAX_SIZE) {
            throw new IllegalArgumentException("Party is full!");
        }
        otherPlayers.add(uuid);
    }

    void remove(@NotNull UUID uuid) {
        otherPlayers.remove(uuid);
    }

}
