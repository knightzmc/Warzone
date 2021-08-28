package me.bristermitten.warzone.game.config;


import com.google.inject.Inject;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import me.bristermitten.warzone.game.spawning.PlayerSpawningMethod;
import me.bristermitten.warzone.item.ItemRenderer;
import me.bristermitten.warzone.util.Null;
import org.bukkit.potion.PotionEffect;

import javax.inject.Provider;
import java.util.concurrent.TimeUnit;

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

    public GameConfig.GameStartTimerConfig load(GameConfigDAO.GameStartTimerConfigDAO dao) {
        return new GameConfig.GameStartTimerConfig(
                dao.threshold() / 100, // convert to a percentage
                Math.toIntExact(TimeUnit.SECONDS.toMillis(dao.length())),
                dao.bossBar().loadBossBarConfig()
        );
    }

    @Override
    public GameConfig get() {
        var dao = daoProvider.get();
        var spectator = loadSpectatorConfig(dao.spectatorMode());
        var startTimer = load(dao.gameStartTimerConfigDAO());
        return new GameConfig(spectator, Null.get(dao.playerSpawningMethod(), PlayerSpawningMethod.ELYTRA), startTimer);
    }
}
