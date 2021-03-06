package me.bristermitten.warzone.papi;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.game.timer.GameTimer;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.game.*;
import me.bristermitten.warzone.timer.TimerRenderer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GameStatusPlaceholder implements WarzonePlaceholder {
    public static final String NOT_IN_GAME = "Not in game";
    private static final Map<Class<? extends InGameState>, String> PREFIXES = HashMap.of(
            AliveState.class, ChatColor.GREEN.toString(),
            InGameSpawningState.class, ChatColor.GREEN.toString(),
            InGulagState.class, ChatColor.YELLOW.toString(),
            SpectatingState.class, ChatColor.RED.toString()
    );
    private final PlayerManager playerManager;
    private final TimerRenderer timerRenderer;
    private final PartyManager partyManager;
    private final GameRepository gameRepository;

    @Inject
    public GameStatusPlaceholder(PlayerManager playerManager,
                                 TimerRenderer timerRenderer,
                                 PartyManager partyManager,
                                 GameRepository gameRepository) {
        this.playerManager = playerManager;
        this.timerRenderer = timerRenderer;
        this.partyManager = partyManager;
        this.gameRepository = gameRepository;
    }

    @Override

    public @Nullable String onPlaceholderRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String params) {
        if (!(offlinePlayer instanceof Player player) || !player.isOnline()) {
            return null;
        }

        return switch (params) {
            case "kill_count" -> gameRepository.getGameContaining(player.getUniqueId())
                    .flatMap(game -> game.getInfo(player.getUniqueId()))
                    .map(PlayerInformation::getKillCount)
                    .map(Objects::toString)
                    .getOrElse(NOT_IN_GAME);
            case "lobby_time_remaining" -> gameRepository.getGameContaining(player.getUniqueId())
                    .map(Game::getPreGameLobbyTimer)
                    .map(timer -> timerRenderer.render(timer, "Waiting for Players"))
                    .getOrElse(NOT_IN_GAME);
            case "time_remaining" -> gameRepository.getGameContaining(player.getUniqueId())
                    .map(Game::getTimer)
                    .filter(GameTimer::hasStarted)
                    .map(timerRenderer::render)
                    .getOrElse(NOT_IN_GAME);
            case "players_remaining" -> gameRepository.getGameContaining(player.getUniqueId())
                    .map(game -> game.getPartiesInGame()
                            .flatMap(Party::getAllMembers)
                            .map(playerManager::lookupPlayer)
                            .filter(Option::isDefined)
                            .map(Option::get)
                            .filter(warzonePlayer -> warzonePlayer.getCurrentState() instanceof AliveState || warzonePlayer.getCurrentState() instanceof InGulagState)
                            .length())
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

    @Override
    public Set<String> getPlaceholders() {
        return Set.of("kill_count", "lobby_time_remaining", "time_remaining", "players_remaining", "party_members_gameformat");
    }
}
