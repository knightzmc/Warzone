package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;

public class ElytraPlayerSpawner implements PlayerSpawner {
    private static final int ELYTRA_Y = 100;
    private final ProtocolWrapper protocolWrapper;

    @Inject
    public ElytraPlayerSpawner(ProtocolWrapper protocolWrapper) {
        this.protocolWrapper = protocolWrapper;
    }

    @Override
    public void spawn(Game game, Party party) {
        var playableArea = game.getArena().playableArea();
        var center = playableArea.center().setY(ELYTRA_Y);
        var world = game.getArena().forceGetWorld(); // empty case should have been handled by now

        party.getAllMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            party.getAllMembers().forEach(otherUUID -> {
                if (otherUUID.equals(uuid)) {
                    return;
                }
                var otherPlayer = Bukkit.getPlayer(otherUUID);
                if (otherPlayer == null) {
                    return;
                }
                protocolWrapper.makePlayerGlowing(otherPlayer, player);
            });
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
            player.teleport(center.toLocation(world));
        });
    }
}
