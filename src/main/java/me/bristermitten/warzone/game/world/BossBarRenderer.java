package me.bristermitten.warzone.game.world;

import me.bristermitten.warzone.arena.ArenaConfig;
import me.bristermitten.warzone.chat.ChatFormatter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class BossBarRenderer {
    private final ChatFormatter formatter;
    private final Set<GameBossBar> managedBars = new HashSet<>();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    @Inject
    public BossBarRenderer(ChatFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Render all boss bars stored in {@link BossBarRenderer#managedBars}
     *
     * @see BossBarRenderer#render(GameBossBar)
     */
    public void renderAll() {
        managedBars.forEach(this::render);
    }

    /**
     * Render a given {@link GameBossBar}
     * This function is pure in that it does not require the given {@link GameBossBar} to be {@link BossBarRenderer#addBar(GameBossBar)}'d
     *
     * @param bossBar the boss bar to render
     */
    public void render(GameBossBar bossBar) {
        bossBar.getViewers().forEach(uuid -> {
            var player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            var bossBarConfig = bossBar.getGame().getArena().gameConfig().bossBarConfig();
            render(player, bossBarConfig, (float) bossBar.getProgress());
        });
    }

    private void render(Player player, ArenaConfig.GameConfig.BossBarConfig config, float progress) {
        var formatted = formatter.format(config.format(), player);
        var bar = bossBars.computeIfAbsent(player.getUniqueId(), unused -> {
            var bossBar = BossBar.bossBar(formatted, progress, config.color(), config.style(), config.flags());
            player.showBossBar(bossBar);
            return bossBar;
        });
        bar.name(formatted);
        bar.progress(progress);
        bar.color(config.color());
        bar.overlay(config.style());
        bar.flags(config.flags());
    }

    public void addBar(GameBossBar bossBar) {
        managedBars.add(bossBar);
    }

    public void removeBar(GameBossBar bossBar) {
        managedBars.remove(bossBar);
        for (UUID uuid : bossBar.getViewers()) {
            var player = Bukkit.getPlayer(uuid);
            var removed = bossBars.remove(uuid);
            if (player != null && removed != null) {
                player.hideBossBar(removed);
            }
        }
    }

}
