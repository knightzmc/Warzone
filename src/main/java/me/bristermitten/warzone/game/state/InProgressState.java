package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.init.ArenaChestFiller;
import me.bristermitten.warzone.game.spawning.PlayerSpawner;
import me.bristermitten.warzone.game.world.GameWorldUpdateTask;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;

import javax.inject.Inject;

public class InProgressState implements GameState {
    private final GameWorldUpdateTask gameWorldUpdateTask;
    private final ArenaChestFiller arenaChestFiller;
    private final PlayerManager playerManager;
    private final PlayerSpawner playerSpawner;

    @Inject
    public InProgressState(GameWorldUpdateTask gameWorldUpdateTask, ArenaChestFiller arenaChestFiller, PlayerManager playerManager, PlayerSpawner playerSpawner) {
        this.gameWorldUpdateTask = gameWorldUpdateTask;
        this.arenaChestFiller = arenaChestFiller;
        this.playerManager = playerManager;
        this.playerSpawner = playerSpawner;
    }

    @Override
    public void onEnter(Game game) {
        game.getPartiesInGame().forEach(party -> {
            party.getAllMembers().forEach(uuid ->
                    playerManager.loadPlayer(uuid, warzonePlayer -> playerManager.setState(warzonePlayer, PlayerStates::inGameSpawningState)));
            playerSpawner.spawn(game, party);
        });
        game.getTimer().start();
        game.getGameBorder().begin();
        gameWorldUpdateTask.submit(game);
    }

    @Override
    public void onLeave(Game game) {
        gameWorldUpdateTask.remove(game);
        arenaChestFiller.unload(game.getArena());
    }
}
