package me.bristermitten.warzone.loot;

import io.vavr.collection.List;

public class LootTable {
    private final String name;
    private final List<TableEntry> items;

    public LootTable(String name, List<TableEntry> items) {
        this.name = name;
        this.items = items;
    }

    public List<TableEntry> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }
}
