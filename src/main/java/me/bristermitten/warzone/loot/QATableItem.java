package me.bristermitten.warzone.loot;

import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.inventory.ItemStack;

public class QATableItem implements TableItem {
   private final String name;

    public QATableItem(String name) {
        this.name = name;
    }


    @Override
    public ItemStack getItem() {
        return QualityArmory.getCustomItemAsItemStack(name);
    }
}
