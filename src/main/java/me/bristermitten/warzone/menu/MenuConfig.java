package me.bristermitten.warzone.menu;

import com.google.gson.annotations.SerializedName;
import io.vavr.collection.List;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record MenuConfig(
        Map<String, PageConfig> pages
) {
    public record PageConfig(
            String title,
            int size,
            Map<String, ItemConfig> items
    ) {
        public record ItemConfig(
                Material type,
                String name,
                List<String> lore,
                List<Integer> slots,
                @SerializedName("head-owner") @Nullable String headOwner,
                @Nullable String action
        ) {
        }
    }

}
