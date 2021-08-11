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
    private final @NotNull Set<UUID> otherPlayers;
    private final Set<PartyInvite> outgoingInvites = new HashSet<>();
    private UUID owner;
    private boolean locked = false;

    Party(UUID owner, @NotNull Set<UUID> otherPlayers) {
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

    @NotNull Set<PartyInvite> getOutgoingInvites() {
        return outgoingInvites;
    }

    public boolean isEmpty() {
        return otherPlayers.isEmpty();
    }

    @NotNull Set<UUID> getOtherPlayers() {
        return otherPlayers;
    }

    UUID getOwner() {
        return owner;
    }

    void setOwner(UUID owner) {
        this.owner = owner;
    }

    public @Unmodifiable @NotNull Collection<UUID> getAllMembers() {
        var set = new HashSet<>(otherPlayers);
        set.add(owner);
        return set;
    }

    boolean isLocked() {
        return locked;
    }

    void setLocked(boolean locked) {
        this.locked = locked;
    }

    public PartySize getSize() {
        return switch (getAllMembers().size()) {
            case 1 -> PartySize.SINGLES;
            case 2 -> PartySize.DOUBLES;
            default -> PartySize.SQUADS;
        };
    }

    public boolean isFull() {
        return otherPlayers.size() >= MAX_SIZE;
    }
}
