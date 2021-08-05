package me.bristermitten.warzone.loot;

import org.bukkit.inventory.ItemStack;

public class NormalTableItem implements TableItem {
    private final ItemStack item;

    public NormalTableItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
}
