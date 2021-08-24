package me.bristermitten.warzone.game.config;


import com.google.inject.Inject;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import me.bristermitten.warzone.game.spawning.PlayerSpawningMethod;
import me.bristermitten.warzone.item.ItemRenderer;
import me.bristermitten.warzone.util.Null;
import org.bukkit.potion.PotionEffect;

import javax.inject.Provider;

public class GameConfigLoader implements Provider<GameConfig> {
    private final Provider<GameConfigDAO> daoProvider;
    private final ItemRenderer itemRenderer;

    @Inject
    public GameConfigLoader(Provider<GameConfigDAO> daoProvider, ItemRenderer itemRenderer) {
        this.daoProvider = daoProvider;
        this.itemRenderer = itemRenderer;
    }

    private GameConfig.SpectatorConfig loadSpectatorConfig(GameConfigDAO.SpectatorConfigDAO dao) {
        var allowFlight = Null.get(dao.allowFlight(), false);
        var invisible = Null.get(dao.invisible(), false);
        var potionEffects = List.ofAll(dao.potionEffects())
                .map(potionEffectDAO -> new PotionEffect(potionEffectDAO.type(), Integer.MAX_VALUE, potionEffectDAO.amplifier()))
                .toJavaList();
        var items = HashMap.ofAll(dao.items())
                .mapValues(item -> new GameConfig.SpectatorConfig.SpectatorItem(itemRenderer.render(item.item(), null), item.action()))
                .toJavaMap();

        return new GameConfig.SpectatorConfig(
                allowFlight,
                invisible,
                potionEffects,
                items
        );
    }

    @Override
    public GameConfig get() {
        var dao = daoProvider.get();
        var spectator = loadSpectatorConfig(dao.spectatorMode());
        return new GameConfig(spectator, Null.get(dao.playerSpawningMethod(), PlayerSpawningMethod.ELYTRA));
    }
}
