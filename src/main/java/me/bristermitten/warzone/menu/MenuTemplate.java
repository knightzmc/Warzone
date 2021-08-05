package me.bristermitten.warzone.menu;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record MenuTemplate(@NotNull String title, int size, Map<Integer, MenuConfig.MenuItemConfig> items) {

    public int emptySlots() {
        return (int) (size - items.entrySet().stream()
                .filter(e -> e.getValue().item().type() != Material.AIR)
                .count());
    }
}
