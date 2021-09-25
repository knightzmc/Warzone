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

public class PlayerInventoryBlocker implements EventListener {
    private static final ItemStack BARRIER_ITEM = new ItemStack(Material.BARRIER);
    private static final NamespacedKey BARRIER_KEY = new NamespacedKey(JavaPlugin.getPlugin(Warzone.class), "inventory_barrier");
    private static final int FIRST_NON_HOTBAR_SLOT = 9;

    static {
        BARRIER_ITEM.editMeta(meta -> meta.getPersistentDataContainer().set(BARRIER_KEY, PersistentDataType.BYTE, (byte) 1));
    }

    public void blockInventory(Player player) {
        var inventory = player.getInventory();
        for (int i = FIRST_NON_HOTBAR_SLOT; i < inventory.getSize(); i++) {
            inventory.setItem(i, BARRIER_ITEM.clone());
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
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(BARRIER_KEY, PersistentDataType.BYTE)) {
            event.setCancelled(true);
        }
    }

}
