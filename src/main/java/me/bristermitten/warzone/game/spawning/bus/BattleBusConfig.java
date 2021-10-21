package me.bristermitten.warzone.game.spawning.bus;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public record BattleBusConfig(
        Material type,
        @SerializedName("model-data") int modelData
) {
    public ItemStack getModel() {
        var busItem = new ItemStack(type, 1);
        busItem.editMeta(meta -> meta.setCustomModelData(modelData));
        return busItem;
    }
}
