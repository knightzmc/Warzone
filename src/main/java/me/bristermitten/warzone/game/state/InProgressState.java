package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.world.GameBorder;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class InProgressState implements GameState {
    private static final int PLAYER_SPAWN_RADIUS = 10; // players in a party spawn within 20 blocks of each other
    private final PlayerManager playerManager;
    private final Plugin plugin;

    @Inject
    public InProgressState(PlayerManager playerManager, Plugin plugin) {
        this.playerManager = playerManager;
        this.plugin = plugin;
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

        new GameBorder(game.getArena(), plugin).begin();
    }

    @Override
    public void onEnter(Game game) {
        game.getPartiesInGame().forEach(party -> spawn(game, party));
        game.getTimer().start();
    }

    @Override
    public void onLeave(Game game) {

    }
}
