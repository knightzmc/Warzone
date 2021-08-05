package me.bristermitten.warzone.loot;

import io.vavr.collection.List;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.item.ItemConfig;

import java.util.Map;

public record LootTablesConfig(
        Map<String, ItemConfig> items,
        Map<String, List<LootTableEntryConfig>> tables
) {
    public static final Configuration<LootTablesConfig> CONFIG = new Configuration<>(LootTablesConfig.class, "loot.yml");

    public record LootTableEntryConfig(
            String item,
            float chance,
            int min,
            int max
    ) {
    }
}
