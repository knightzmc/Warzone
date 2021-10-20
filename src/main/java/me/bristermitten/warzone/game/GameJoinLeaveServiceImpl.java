package me.bristermitten.warzone.game;

import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.cleanup.GameWinnerHandler;
import me.bristermitten.warzone.game.state.*;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.util.Unit;

import javax.inject.Inject;
import java.util.UUID;

public class GameJoinLeaveServiceImpl implements GameJoinLeaveService {
    private final PlayerManager playerManager;
    private final GameWinnerHandler gameWinnerHandler;
    private final GameStateManager gameStateManager;

    @Inject
    public GameJoinLeaveServiceImpl(PlayerManager playerManager,
                                    GameWinnerHandler gameWinnerHandler,
                                    GameStateManager gameStateManager) {
        this.playerManager = playerManager;
        this.gameWinnerHandler = gameWinnerHandler;
        this.gameStateManager = gameStateManager;
    }

    @Override
    public Future<Unit> leave(Game game, Party party) {
        if (game.getState() instanceof IdlingState) {
            // This shouldn't happen, just fail silently
            return Future.successful(Unit.INSTANCE);
        }
        var leavers = List.ofAll(party.getAllMembers());

        return Future.sequence(leavers.map(playerManager::loadPlayer)) // load all the players who are going to leave
                .map(players -> {
                    players.forEach(warzonePlayer ->
                            playerManager.setState(warzonePlayer, PlayerStates::inLobbyState)); // change player states

                    game.getParties().remove(party); // remove the party from the game if necessary
                    return game;
                }).flatMap(gameWinnerHandler::checkForWinner);
    }

    @Override
    public Future<Unit> leave(Game game, UUID leaver) {
        if (!game.contains(leaver)) {
            throw new IllegalArgumentException("Player is not in this game");
        }
        if (game.getState() instanceof IdlingState) {
            // This shouldn't happen, just fail silently
            return Future.successful(Unit.INSTANCE);
        }

        return playerManager.loadPlayer(leaver)
                .map(player -> {
                    playerManager.setState(player, PlayerStates::inLobbyState); // change player state
                    return game;
                })
                .flatMap(gameWinnerHandler::checkForWinner);
    }

    @Override
    public void join(Game game, Party party) {
        if (game.getState() instanceof InProgressState) {
            return; // this should never really happen so i'm not too concerned about a user friendly error message here
        }

        if (game.getState() instanceof IdlingState) {
            // fire it up!
            gameStateManager.setState(game, GameStates::inLobbyState);
        }

        if (game.getState() instanceof InLobbyState) {
            game.getParties().add(party);
            party.getAllMembers().forEach(uuid -> {
                game.getMutablePlayerInformation().put(uuid, new PlayerInformation(uuid));
                playerManager.loadPlayer(uuid, player ->
                        playerManager.setState(player, PlayerStates::inPreGameLobbyState));
            });

            if (game.isFull()) {
                gameStateManager.setState(game, GameStates::inProgressState);
            }
        }
    }
}
