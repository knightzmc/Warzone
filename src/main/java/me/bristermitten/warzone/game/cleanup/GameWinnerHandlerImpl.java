package me.bristermitten.warzone.game.cleanup;

import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.Functions;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

class GameWinnerHandlerImpl implements GameWinnerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameWinnerHandlerImpl.class);
    private final GameRepository gameRepository;

    private final XPHandler xpHandler;
    private final PartyManager partyManager;
    private final LangService langService;
    private final PlayerManager playerManager;

    @Inject
    public GameWinnerHandlerImpl(GameRepository gameRepository,
                                 PartyManager partyManager,
                                 XPHandler xpHandler,
                                 LangService langService,
                                 PlayerManager playerManager) {
        this.gameRepository = gameRepository;
        this.partyManager = partyManager;
        this.xpHandler = xpHandler;
        this.langService = langService;
        this.playerManager = playerManager;
    }


    @Override
    public @NotNull Option<Party> getWinner(@NotNull Game game) {
        var players = gameRepository.getPlayers(game);

        var stillAlive = players.filter(player -> player.getCurrentState() instanceof AliveState);
        var remainingParties = stillAlive.groupBy(partyManager::getParty);
        if (remainingParties.size() != 1) {
            return Option.none();
        }
        return remainingParties.headOption().map(Tuple2::_1);
    }

    @Override
    public @NotNull Future<Unit> giveWinnerRewards(@NotNull Game game, @NotNull Party winners) {
        winners.getAllMembers().forEach(winnerId -> {
            var player = Bukkit.getPlayer(winnerId);
            Objects.requireNonNull(player); // if they're offline they should've been removed from the party by now
            langService.send(player, config -> config.gameLang().winner());
        });

        game.getPlayersInGame()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> langService.send(player,
                        config -> config.gameLang().winnerBroadcast(),
                        Map.of("{winner}", Objects.requireNonNull(Bukkit.getPlayer(winners.getOwner())).getName())));

        var allPlayers = Future.sequence(game.getPlayersInGame()
                .map(playerManager::loadPlayer));

        return allPlayers
                .onSuccess(players -> giveWinnerXP(game, players, winners))
                .map(Functions.constant(Unit.INSTANCE));
    }


    private void giveWinnerXP(Game game, Seq<WarzonePlayer> players, Party winningParty) {
        var winners = players.filter(p -> partyManager.getParty(p).equals(winningParty));

        winners.forEach(winner -> xpHandler.addXP(winner, XPConfig::win));
        LOGGER.debug("Giving winner xp to players {}", winners);

        var top3 = players.toList()
                .sorted(Comparator.comparingInt(player ->
                        game.getInfo(player.getPlayerId())
                                .map(PlayerInformation::getKillCount)
                                .getOrElse(0)))
                .take(3);
        top3.forEach(p -> xpHandler.addXP(p, XPConfig::top3));
        LOGGER.debug("Giving top 3 xp to players {}", top3);
    }

}
