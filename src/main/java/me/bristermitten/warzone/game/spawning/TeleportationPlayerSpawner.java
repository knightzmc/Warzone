package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class TeleportationPlayerSpawner implements PlayerSpawner {
    private static final int PLAYER_SPAWN_RADIUS = 10; // players in a party spawn within 20 blocks of each other
    private final PlayerManager playerManager;

    @Inject
    public TeleportationPlayerSpawner(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void spawn(Game game, Party party) {
        run(game, party.getAllMembers());
    }

    private void run(Game game, Iterable<UUID> party) {
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

        party.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            var spawnPoint = randomRegion.random();
            var world = game.getArena().getWorldOrThrow(); // empty case should have been handled by now
            var y = world.getHighestBlockYAt(spawnPoint.x(), spawnPoint.y());
            player.teleport(spawnPoint.setY(y).toLocation(world));
            playerManager.loadPlayer(uuid, warzonePlayer -> playerManager.setState(warzonePlayer, PlayerStates::aliveState));
        });
    }

    @Override
    public void spawn(Game game, WarzonePlayer player) {
        run(game, List.of(player.getPlayerId()));
    }
}
