package me.bristermitten.warzone.loot;

import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QATableItem implements TableItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(QATableItem.class);
    private final String name;

    public QATableItem(String name) {
        this.name = name;
    }


    @Override
    public ItemStack getItem() {
        var item = QualityArmory.getCustomItemByName(name);
        if (item == null) {
            LOGGER.error("Couldn't find QualityArmory item named {}. Check your loot.yml for typos!", name);
            return new ItemStack(Material.AIR);
        }
        return QualityArmory.getCustomItemAsItemStack(item);
    }
}
