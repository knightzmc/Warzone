package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.Warzone;
import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import me.bristermitten.warzone.util.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

public class ElytraPlayerSpawner implements PlayerSpawner {
    public static final ItemStack ELYTRA_ITEM = new ItemStack(Material.ELYTRA);
    public static final NamespacedKey ELYTRA_KEY = new NamespacedKey(JavaPlugin.getPlugin(Warzone.class), "elytra_drop_in");
    public static final int ELYTRA_Y = 100;

    static {
        ELYTRA_ITEM.editMeta(meta -> meta.getPersistentDataContainer().set(ELYTRA_KEY, PersistentDataType.BYTE, (byte) 1));
    }

    private final ProtocolWrapper protocolWrapper;
    private final PlayerManager playerManager;
    private final Schedule schedule;

    @Inject
    public ElytraPlayerSpawner(ProtocolWrapper protocolWrapper, PlayerManager playerManager, Schedule schedule) {
        this.protocolWrapper = protocolWrapper;
        this.playerManager = playerManager;
        this.schedule = schedule;
    }

    public static void giveElytra(Player player) {
        player.getInventory().setChestplate(ELYTRA_ITEM);
        player.setInvulnerable(true);
    }


    @Override
    public void spawn(Game game, Party party) {
        protocolWrapper.makePartyMembersGlow(party);

        party.getAllMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }

            playerManager.loadPlayer(uuid).flatMap(schedule.runSync(warzonePlayer -> spawn(game, warzonePlayer)));
        });
    }

    @Override
    public void spawn(Game game, WarzonePlayer player) {
        var playableArea = game.getArena().playableArea();
        var center = playableArea.center().setY(ELYTRA_Y);

        spawn(game, player, center);
    }

    public void spawn(Game game, WarzonePlayer player, Point spawnAt) {
        var bukkitPlayer = player.getPlayer().get();
        var world = game.getArena().getWorldOrThrow(); // empty case should have been handled by now
        giveElytra(bukkitPlayer);
        bukkitPlayer.teleport(spawnAt.toLocation(world));
    }
}
