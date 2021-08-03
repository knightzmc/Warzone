package me.bristermitten.warzone.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represents a Page in a Menu
 * Like {@link Menu} this class is ephemeral and so Page objects should not be stored
 */
public class Page implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Page.class.getName());
    private final String name;
    private final int size;
    private final Component title;
    private final Inventory inventory;
    private final Map<Integer, MenuItem> itemMap;
    private Menu menu;
    private Plugin plugin;

    public Page(String name, int size, Component title, List<MenuItem> items) {
        this.name = name;
        this.size = size;
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.itemMap = new HashMap<>();

        for (var item : items) {
            add(item);
        }
    }

    /**
     * Copy constructor
     */
    private Page(String name, int size, Component title, Map<Integer, MenuItem> itemMap) {
        this.name = name;
        this.size = size;
        this.title = title;
        this.itemMap = itemMap;
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public boolean addFirstEmpty(MenuItem item) {
        int firstEmpty = inventory.firstEmpty();
        if (firstEmpty == -1) {
            return false;
        }
        return add(new MenuItem(
                item.item(),
                List.of(firstEmpty),
                item.action()
        ));
    }

    int firstEmpty() {
        return inventory.firstEmpty();
    }

    boolean add(MenuItem item) {
        if (inventory.firstEmpty() == -1) {
            return false;
        }
        for (int slot : item.slots()) {
            if (this.itemMap.containsKey(slot)) {
                LOGGER.warning(() -> "Duplicate slots for page named %s. Items %s and %s share slot %d".formatted(name, itemMap.get(slot), item, slot));
            }
            itemMap.put(slot, item);
            inventory.setItem(slot, item.item());
        }
        return true;
    }

    void bind(Menu menu, Plugin plugin) {
        if (this.menu != null) {
            throw new IllegalStateException("Already bound!");
        }
        this.menu = menu;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public String getName() {
        return name;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != inventory) {
            return;
        }
        var clickedItem = itemMap.get(event.getSlot());
        if (clickedItem == null) {
            return;
        }
        clickedItem.action()
                .onClick(event, (Player) event.getWhoClicked(), menu, this);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);

        // I'm not sure how important this is but it might prevent a potential memory leak
        this.menu = null;
        this.plugin = null;

    }

    Page copy() {
        if (menu == null) {
            throw new IllegalStateException("Not bound");
        }
        var copy = new Page(name, size, title, new HashMap<>(itemMap));
        copy.bind(menu, plugin);
        return copy;
    }
}
