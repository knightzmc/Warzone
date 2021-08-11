package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class InProgressState implements GameState {
    private static final int PLAYER_SPAWN_RADIUS = 10; // players in a party spawn within 20 blocks of each other
    private final PlayerManager playerManager;

    @Inject
    public InProgressState(PlayerManager playerManager) {
        this.playerManager = playerManager;
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
            var world = game.getArena().getWorld().get(); // empty case should have been handled by now
            var y = world.getHighestBlockYAt(spawnPoint.x(), spawnPoint.y());
            player.teleport(spawnPoint.setY(y).toLocation(world));
            playerManager.loadPlayer(uuid, warzonePlayer -> playerManager.setState(warzonePlayer, PlayerStates::inGameState));
        });
    }

    @Override
    public void onEnter(Game game) {
        game.getPlayersInGame().forEach(party -> spawn(game, party));
    }

    @Override
    public void onLeave(Game game) {

    }
}
