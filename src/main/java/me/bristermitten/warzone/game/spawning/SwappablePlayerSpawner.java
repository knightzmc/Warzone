package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.party.Party;

import javax.inject.Inject;
import javax.inject.Provider;

public class SwappablePlayerSpawner implements PlayerSpawner {
    private final Provider<GameConfig> gameConfigProvider;
    private final Provider<ElytraPlayerSpawner> elytraPlayerSpawnerProvider;
    private final Provider<TeleportationPlayerSpawner> teleportationPlayerSpawnerProvider;

    @Inject
    public SwappablePlayerSpawner(Provider<GameConfig> gameConfigProvider,
                                  Provider<ElytraPlayerSpawner> elytraPlayerSpawnerProvider,
                                  Provider<TeleportationPlayerSpawner> teleportationPlayerSpawnerProvider) {
        this.gameConfigProvider = gameConfigProvider;
        this.elytraPlayerSpawnerProvider = elytraPlayerSpawnerProvider;
        this.teleportationPlayerSpawnerProvider = teleportationPlayerSpawnerProvider;
    }

    @Override
    public void spawn(Game game, Party party) {
        var delegate = switch (gameConfigProvider.get().spawningMethod()) {
            case ELYTRA -> elytraPlayerSpawnerProvider.get();
            case TELEPORT -> teleportationPlayerSpawnerProvider.get();
        };
        delegate.spawn(game, party);
    }
}
