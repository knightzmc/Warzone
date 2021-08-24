package me.bristermitten.warzone.papi;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.GameTimer;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.state.game.InGameState;
import me.bristermitten.warzone.player.state.game.InGulagState;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import me.bristermitten.warzone.util.DurationFormatter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameStatusPlaceholder implements WarzonePlaceholder {
    public static final String NOT_IN_GAME = "Not in game";
    private static final Map<Class<? extends InGameState>, String> PREFIXES = HashMap.of(
            AliveState.class, ChatColor.GREEN.toString(),
            InGulagState.class, ChatColor.YELLOW.toString(),
            SpectatingState.class, ChatColor.RED.toString()
    );
    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final PartyManager partyManager;

    @Inject
    public GameStatusPlaceholder(PlayerManager playerManager, GameManager gameManager, PartyManager partyManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.partyManager = partyManager;
    }

    @Override

    public @Nullable String onPlaceholderRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String params) {
        if (!(offlinePlayer instanceof Player player) || !player.isOnline()) {
            return null;
        }

        return switch (params) {
            case "kill_count" -> gameManager.getGameContaining(player.getUniqueId())
                    .flatMap(game -> game.getInfo(player.getUniqueId()))
                    .map(PlayerInformation::getKillCount)
                    .map(Objects::toString)
                    .getOrElse(NOT_IN_GAME);
            case "time_remaining" -> gameManager.getGameContaining(player.getUniqueId())
                    .map(Game::getTimer)
                    .filter(GameTimer::isInitialised)
                    .map(GameTimer::getTimeRemaining)
                    .map(DurationFormatter::format)
                    .getOrElse(NOT_IN_GAME);
            case "players_remaining" -> gameManager.getGameContaining(player.getUniqueId())
                    .map(game -> game.getPartiesInGame().stream()
                            .flatMap(party -> party.getAllMembers().stream())
                            .map(playerManager::lookupPlayer)
                            .filter(Option::isDefined)
                            .map(Option::get)
                            .filter(warzonePlayer -> warzonePlayer.getCurrentState() instanceof AliveState || warzonePlayer.getCurrentState() instanceof InGulagState)
                            .count())
                    .map(Objects::toString)
                    .getOrElse(NOT_IN_GAME);
            case "party_members_gameformat" -> List.ofAll(partyManager.getParty(player)
                    .getAllMembers())
                    .map(playerManager::lookupPlayer)
                    .filter(Option::isDefined)
                    .map(Option::get)
                    .map(user -> {
                        var state = user.getCurrentState();
                        String prefix = null;
                        if (state instanceof InGameState gameState) {
                            prefix = PREFIXES.getOrElse(gameState.getClass(), null);
                        }

                        if (prefix == null) {
                            prefix = ChatColor.BLACK.toString();
                        }
                        return prefix + user.getOfflinePlayer().getName();
                    })
                    .collect(Collectors.joining("\n"))
                    .trim();

            default -> null;
        };
    }
}
