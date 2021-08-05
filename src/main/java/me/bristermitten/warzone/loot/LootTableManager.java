package me.bristermitten.warzone.loot;

import io.vavr.collection.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class LootTableManager {
    private final LootTableLoader loader;
    private final Provider<LootTablesConfig> configProvider;

    @Inject
    public LootTableManager(LootTableLoader loader, Provider<LootTablesConfig> configProvider) {
        this.loader = loader;
        this.configProvider = configProvider;
    }

    public Map<String, LootTable> getTables() {
        return loader.load(configProvider.get());
    }
}
