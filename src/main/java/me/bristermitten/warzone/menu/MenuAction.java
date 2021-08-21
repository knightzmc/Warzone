package me.bristermitten.warzone.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

public enum MenuAction {
    NEXT_PAGE,
    PREVIOUS_PAGE,
    LEAVE_PARTY_ACTION,
    BRING_PARTY_ACTION,
    NOTHING {
        @Override
        public void execute(InventoryClickEvent event) {
            event.setCancelled(true);
        }
    },
    CLOSE_MENU {
        @Override
        public void execute(InventoryClickEvent event) {
            event.getWhoClicked().closeInventory();
        }
    };

    public void execute(InventoryClickEvent event) {
        throw new UnsupportedOperationException("This action cannot be defined generically");
    }
}
