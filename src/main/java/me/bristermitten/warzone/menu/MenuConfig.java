package me.bristermitten.warzone.menu;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MenuConfig(int size, String title, Map<String, ItemConfig> items) {
    public record ItemConfig(
            Material type,
            @Nullable Integer amount,
            @Nullable String name,
            @Nullable List<String> lore,
            @Nullable List<Integer> slots,
            @Nullable MenuAction action,
            @SerializedName("head-owner") @Nullable String headOwner
    ) {
    }
}
