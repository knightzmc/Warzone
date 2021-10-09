package me.bristermitten.warzone.player;

import me.bristermitten.warzone.Warzone;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.state.game.InGameState;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;

public class PlayerInventoryBlocker implements EventListener {
    private static final ItemStack BARRIER_ITEM = new ItemStack(Material.BARRIER);
    private static final NamespacedKey BARRIER_KEY = new NamespacedKey(JavaPlugin.getPlugin(Warzone.class), "inventory_barrier");
    private static final int FIRST_NON_HOTBAR_SLOT = 9;
    private static final Set<Integer> BLOCKED_SLOTS = Set.of(36, 37, 38, 39, 40);

    private static ItemStack createBarrierItem() {
        var item = BARRIER_ITEM.clone();
        // Make every item unique
        item.editMeta(meta ->
                meta.getPersistentDataContainer().set(BARRIER_KEY, PersistentDataType.STRING, UUID.randomUUID().toString()));
        return item;
    }

    public void blockInventory(Player player) {
        var inventory = player.getInventory();
        for (int i = FIRST_NON_HOTBAR_SLOT; i < inventory.getSize(); i++) {
            if (BLOCKED_SLOTS.contains(i)) {
                continue;
            }
            inventory.setItem(i, createBarrierItem());
        }
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent e) {
        if (e.getNewState() instanceof InGameState) {
            e.getSubject().getPlayer().peek(this::blockInventory);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(BARRIER_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }

}
