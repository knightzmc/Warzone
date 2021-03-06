package me.bristermitten.warzone.game.gulag;

import io.vavr.control.Option;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.game.InGulagState;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class GulagManager {
    private static final long GULAG_MIN_TIME = TimeUnit.MINUTES.toMillis(7);
    private static final int GULAG_MIN_PLAYERS = 12;
    private static final int GULAG_MIN_PARTIES = 4;
    private final GameRepository gameRepository;
    private final PlayerManager playerManager;


    @Inject
    public GulagManager(GameRepository gameRepository, PlayerManager playerManager) {
        this.gameRepository = gameRepository;
        this.playerManager = playerManager;
    }

    public boolean gulagIsAvailable(Gulag gulag) {
        var game = gulag.getGame();
        if (!game.getTimer().hasStarted()) {
            return false;
        }
        if (game.getTimer().getTimeRemaining() < GULAG_MIN_TIME) {
            return false;
        }
        if (game.getAcceptedSize() == PartySize.SINGLES) {
            return gameRepository.getPlayers(game).size() >= GULAG_MIN_PLAYERS;
        } else {
            return game.getPartiesInGame().size() >= GULAG_MIN_PARTIES;
        }
    }

    public boolean gulagIsAvailableFor(@NotNull WarzonePlayer warzonePlayer) {
        if (warzonePlayer.getCurrentState() instanceof InGulagState) {
            return false;
        }
        var gameOpt = gameRepository.getGameContaining(warzonePlayer);
        if (gameOpt.isEmpty()) {
            return false;
        }
        var game = gameOpt.get();
        if (!gulagIsAvailable(game.getGulag())) {
            return false;
        }
        var infoOpt = game.getInfo(warzonePlayer.getPlayerId());
        if (infoOpt.isEmpty()) {
            return false;
        }
        var playerInfo = infoOpt.get();
        return playerInfo.getDeathCount() <= game.getArena().gameConfig().maxGulagEntries();
    }

    public void addToGulag(Gulag gulag, WarzonePlayer player) {
        gulag.join(player);
        playerManager.setState(player, PlayerStates::inGulagQueuingState);
        processQueue(gulag);
    }

    private Option<WarzonePlayer> getOnlinePlayer(Queue<WarzonePlayer> playerQueue) {
        while (!playerQueue.isEmpty()) {
            var next = playerQueue.peek();
            if (next.getPlayer().isDefined()) {
                return Option.of(playerQueue.poll());
            }
        }
        return Option.none();
    }

    private void processQueue(Gulag gulag) {
        if (gulag.getGulagPlayers() != null) {
            return; // nothing to do, a duel is in progress
        }

        var maybe1 = getOnlinePlayer(gulag.getPlayersInQueue());
        var maybe2 = getOnlinePlayer(gulag.getPlayersInQueue());

        if (maybe1.isEmpty() || maybe2.isEmpty()) {
            maybe1.peek(p1 -> gulag.getPlayersInQueue().offerFirst(p1));
            maybe2.peek(p2 -> gulag.getPlayersInQueue().offerFirst(p2));
            // Add them back to the queue
            return; // not enough valid players
        }

        var player1 = maybe1.get();
        var player2 = maybe2.get();


        var world = gulag.getGame().getArena().getWorldOrThrow();

        gulag.setGulagPlayers(new Gulag.GulagPlayers(
                player1.getPlayerId(),
                player2.getPlayerId()
        ));

        playerManager.setState(player1, PlayerStates::inGulagArenaState);
        playerManager.setState(player2, PlayerStates::inGulagArenaState);

        player1.getPlayer()
                .peek(p1 -> p1.teleport(gulag.getGame().getArena().gulagConfig().fightingArea1().toLocation(world)));

        player2.getPlayer()
                .peek(p2 -> p2.teleport(gulag.getGame().getArena().gulagConfig().fightingArea2().toLocation(world)));
    }
}
