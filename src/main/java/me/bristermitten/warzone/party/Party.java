package me.bristermitten.warzone.party;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class Party {
    private static final int MAX_SIZE = 4;
    private final Set<UUID> otherPlayers;
    private final Set<PartyInvite> outgoingInvites = new HashSet<>();
    private UUID owner;

    Party(UUID owner, Set<UUID> otherPlayers) {
        this.owner = owner;
        if (otherPlayers.size() > MAX_SIZE) {
            throw new IllegalArgumentException("players is too big");
        }
        this.otherPlayers = otherPlayers;
    }

    void add(@NotNull UUID uuid) {
        if (isFull()) {
            throw new IllegalArgumentException("Party is full!");
        }
        otherPlayers.add(uuid);
    }

    Set<PartyInvite> getOutgoingInvites() {
        return outgoingInvites;
    }

    public boolean isEmpty() {
        return otherPlayers.isEmpty();
    }

    Set<UUID> getOtherPlayers() {
        return otherPlayers;
    }

    UUID getOwner() {
        return owner;
    }

    void setOwner(UUID owner) {
        this.owner = owner;
    }

    public @Unmodifiable Collection<UUID> getAllMembers() {
        var set = new HashSet<>(otherPlayers);
        set.add(owner);
        return set;
    }

    public boolean isFull() {
        return otherPlayers.size() >= MAX_SIZE;
    }
}
