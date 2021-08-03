package me.bristermitten.warzone.menu;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class MenuListener implements Listener {
    private final Inventory inventory;
    private final Consumer<InventoryClickEvent> onClick;

    public MenuListener(Inventory inventory, Consumer<InventoryClickEvent> onClick) {
        this.inventory = inventory;
        this.onClick = onClick;

    }

    public void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == inventory) {
            onClick.accept(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}
