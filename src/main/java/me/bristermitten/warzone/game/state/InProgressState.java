package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.init.ArenaChestFiller;
import me.bristermitten.warzone.game.world.GameWorldUpdateTask;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class InProgressState implements GameState {
    private static final int PLAYER_SPAWN_RADIUS = 10; // players in a party spawn within 20 blocks of each other
    private final PlayerManager playerManager;
    private final GameWorldUpdateTask gameWorldUpdateTask;

    @Inject
    public InProgressState(PlayerManager playerManager, GameWorldUpdateTask gameWorldUpdateTask, ArenaChestFiller arenaChestFiller) {
        this.playerManager = playerManager;
        this.gameWorldUpdateTask = gameWorldUpdateTask;
        this.arenaChestFiller = arenaChestFiller;
    }


    private void spawn(Game game, Party party) {
        var playableArea = game.getArena().playableArea();
        var randomPoint = playableArea.random();
        var randomX = randomPoint.x();
        var randomZ = randomPoint.z();
        var minX = randomX - PLAYER_SPAWN_RADIUS;
        var maxX = randomX + PLAYER_SPAWN_RADIUS;
        var minZ = randomZ - PLAYER_SPAWN_RADIUS;
        var maxZ = randomZ + PLAYER_SPAWN_RADIUS;
        var randomRegion = playableArea.segment(
                new Point(minX, 0, minZ),
                new Point(maxX, 2, maxZ)
        );

        party.getAllMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            var spawnPoint = randomRegion.random();
            var world = game.getArena().forceGetWorld(); // empty case should have been handled by now
            var y = world.getHighestBlockYAt(spawnPoint.x(), spawnPoint.y());
            player.teleport(spawnPoint.setY(y).toLocation(world));
            playerManager.loadPlayer(uuid, warzonePlayer -> playerManager.setState(warzonePlayer, PlayerStates::aliveState));
        });
    }

    @Override
    public void onEnter(Game game) {
        game.getPartiesInGame().forEach(party -> spawn(game, party));
        game.getTimer().start();
        game.getGameBorder().begin();
        gameWorldUpdateTask.submit(game);
    }

    private final ArenaChestFiller arenaChestFiller;
    @Override
    public void onLeave(Game game) {
        gameWorldUpdateTask.remove(game);
        arenaChestFiller.unload(game.getArena());
    }
}
