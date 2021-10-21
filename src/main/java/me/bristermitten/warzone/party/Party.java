package me.bristermitten.warzone.party;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
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

    Party(UUID owner, @NotNull Set<UUID> otherPlayers) {
        this.owner = owner;
        if (otherPlayers.size() + 1 >= MAX_SIZE) {
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

    /**
     * @return whether the party only has 1 player
     */
    public boolean isSingle() {
        return otherPlayers.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Party party)) return false;
        return getOwner().equals(party.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner());
    }

    @NotNull Set<UUID> getOtherPlayers() {
        return otherPlayers;
    }

    public boolean contains(UUID uuid) {
        return uuid.equals(owner) || otherPlayers.contains(uuid);
    }

    public UUID getOwner() {
        return owner;
    }

    void setOwner(UUID owner) {
        this.owner = owner;
    }

    public io.vavr.collection.Set<UUID> getAllMembers() {
        return io.vavr.collection.HashSet.ofAll(otherPlayers).add(owner);
    }

    public PartySize getSize() {
        return switch (getAllMembers().size()) {
            case 1 -> PartySize.SINGLES;
            case 2 -> PartySize.DOUBLES;
            default -> PartySize.SQUADS;
        };
    }

    @Override
    public String toString() {
        return "Party{" +
               "otherPlayers=" + otherPlayers +
               ", outgoingInvites=" + outgoingInvites +
               ", owner=" + owner +
               '}';
    }

    public boolean isFull() {
        return otherPlayers.size() >= MAX_SIZE;
    }
}
