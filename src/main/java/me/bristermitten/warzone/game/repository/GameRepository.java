package me.bristermitten.warzone.game.repository;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface GameRepository {
    @NotNull Set<Game> getGames();

    @NotNull
    default Option<Game> getGameContaining(UUID uuid) {
        return getGames()
                .filter(game -> game.contains(uuid))
                .headOption();
    }

    @NotNull
    default Option<Game> getGameContaining(WarzonePlayer player) {
        return getGameContaining(player.getPlayerId());
    }

    @NotNull
    default Option<Game> getGameContaining(Party party) {
        return getGames()
                .filter(game -> game.getPartiesInGame().contains(party))
                .headOption();
    }

    /**
     * Get all players in a given Game
     * This method relies on a {@link WarzonePlayer} instance existing in the {@link PlayerManager} stored in this class,
     * as it will only call {@link PlayerManager#lookupPlayer(UUID)} to get a result immediately.
     * <p>
     * However, if the player is in a game then they <b><i>should</i></b> always be in the player cache, otherwise something has gone
     * very wrong
     */
    @NotNull Set<WarzonePlayer> getPlayers(@NotNull final Game game);
}
