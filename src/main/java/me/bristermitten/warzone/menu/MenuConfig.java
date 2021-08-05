package me.bristermitten.warzone.menu;

import me.bristermitten.warzone.item.ItemConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MenuConfig(@Nullable Integer size, @Nullable String title, Map<String, MenuItemConfig> items) {
    public record MenuItemConfig(
            ItemConfig item,
            @Nullable List<Integer> slots,
            @Nullable MenuAction action) {
    }
}
