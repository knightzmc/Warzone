package me.bristermitten.warzone.menu;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record MenuTemplate(@NotNull String title, int size, Map<Integer, MenuConfig.ItemConfig> items) {

    public int emptySlots() {
        return size - items.size();
    }
}
