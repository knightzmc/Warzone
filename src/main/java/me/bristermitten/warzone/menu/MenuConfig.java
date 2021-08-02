package me.bristermitten.warzone.menu;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MenuConfig(
        Map<String, PageConfig> pages
) {
    public record PageConfig(
            @Nullable String title,
            @Nullable Integer size,
            @Nullable Map<String, ItemConfig> items
    ) {

        public static final PageConfig DEFAULT = new PageConfig(
                "No title defined",
                54,
                Map.of()
        );

        public record ItemConfig(
                @NotNull Material type,
                @Nullable Integer amount,
                @Nullable String name,
                @Nullable List<String> lore,
                @Nullable List<Integer> slots,
                @Nullable @SerializedName("head-owner") String headOwner,
                @Nullable MenuAction action
        ) {
        }
    }

}
