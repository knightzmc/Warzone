package me.bristermitten.warzone.arena;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ArenaManager {
    private final ArenaLoader loader;
    private final Provider<ArenasConfig> configProvider;

    @Inject
    public ArenaManager(ArenaLoader loader, Provider<ArenasConfig> configProvider) {
        this.loader = loader;
        this.configProvider = configProvider;
    }
}
