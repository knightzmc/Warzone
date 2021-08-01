package me.bristermitten.warzone.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public enum MenuAction {
    CLOSE_MENU {
        @Override
        public void onClick(InventoryClickEvent event, Player player, Menu menu, Page page) {
            event.setCancelled(true);
            player.closeInventory();
        }
    },
    NEXT_PAGE {
        @Override
        public void onClick(InventoryClickEvent event, Player player, Menu menu, Page page) {
            event.setCancelled(true);
            final List<Page> pages = menu.getPages();
            final int i = pages.indexOf(page);
            int next = i + 1;
            if (next >= pages.size()) {
                return;
            }
            final Page nextPage = pages.get(next);
            nextPage.open(player);
        }
    },
    NOTHING {
        @Override
        public void onClick(InventoryClickEvent event, Player player, Menu menu, Page page) {
            event.setCancelled(true);
        }
    },
    PREVIOUS_PAGE {
        @Override
        public void onClick(InventoryClickEvent event, Player player, Menu menu, Page page) {
            event.setCancelled(true);
            final List<Page> pages = menu.getPages();
            final int i = pages.indexOf(page);
            int previous = i - 1;
            if (previous < 0) {
                return;
            }
            final Page previousPage = pages.get(previous);
            previousPage.open(player);
        }
    },
    TAKE {
        @Override
        public void onClick(InventoryClickEvent event, Player player, Menu menu, Page page) {
// Do nothing
        }
    };

    public abstract void onClick(InventoryClickEvent event, Player player, Menu menu, Page page);
}
