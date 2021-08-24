package me.bristermitten.warzone.bossbar;

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


    public void renderInto(BossBar bar, Player player, CustomBossBar customBossBar) {
        var bossBarConfig = customBossBar.getBossBar();
        render(bar, player, bossBarConfig);
    }

    public BossBar render(Player player, CustomBossBar customBossBar) {
        var bar = emptyBossBar.get();
        renderInto(bar, player, customBossBar);
        return bar;
    }

    private void render(BossBar bar, Player player, BossBarConfig config) {
        var formatted = formatter.format(config.name(), player);
        bar.name(formatted);
        bar.progress(config.progress());
        bar.color(config.color());
        bar.overlay(config.overlay());
        bar.flags(config.flags());
    }

}
