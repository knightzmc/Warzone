package me.bristermitten.warzone.menu;

import java.util.Map;

public record MenuTemplate(String title, int size, Map<Integer, MenuConfig.ItemConfig> items) {

    public int emptySlots() {
        return size - items.size();
    }
}
