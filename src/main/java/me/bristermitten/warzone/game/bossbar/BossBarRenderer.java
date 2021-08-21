package me.bristermitten.warzone.game.bossbar;

import me.bristermitten.warzone.arena.ArenaConfig;
import me.bristermitten.warzone.chat.ChatFormatter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.function.Supplier;

public class BossBarRenderer {
    private final ChatFormatter formatter;
    private final Supplier<BossBar> emptyBossBar = () -> BossBar.bossBar(Component.empty(), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);

    @Inject
    public BossBarRenderer(ChatFormatter formatter) {
        this.formatter = formatter;
    }


    public void renderInto(BossBar bar, Player player, GameBossBar gameBossBar) {
        var bossBarConfig = gameBossBar.game().getArena().gameConfig().bossBarConfig();
        render(bar, player, bossBarConfig, (float) gameBossBar.getProgress());
    }

    public BossBar render(Player player, GameBossBar gameBossBar) {
        var bar = emptyBossBar.get();
        renderInto(bar, player, gameBossBar);
        return bar;
    }

    private void render(BossBar bar, Player player, ArenaConfig.GameConfig.BossBarConfig config, float progress) {
        var formatted = formatter.format(config.format(), player);
        bar.name(formatted);
        bar.progress(progress);
        bar.color(config.color());
        bar.overlay(config.style());
        bar.flags(config.flags());
    }

}
