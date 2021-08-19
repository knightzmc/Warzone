package me.bristermitten.warzone.game.world;

import me.bristermitten.warzone.arena.ArenaConfig;
import me.bristermitten.warzone.chat.ChatFormatter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class BossBarRenderer {
    private final ChatFormatter formatter;
    private final Set<GameBossBar> managedBars = new HashSet<>();

    @Inject
    public BossBarRenderer(ChatFormatter formatter) {
        this.formatter = formatter;
    }

    public void update() {
        managedBars.forEach(bossBar -> bossBar.getViewers().forEach(uuid -> {
            var player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            var bossBarConfig = bossBar.getGame().getArena().gameConfig().bossBarConfig();
            update(player, bossBarConfig, (float) bossBar.getProgress());
        }));
    }

    private void update(Player player, ArenaConfig.GameConfig.BossBarConfig config, float progress) {
        var formatted = formatter.format(config.format(), player);
        var bar = BossBar.bossBar(formatted, progress, config.color(), config.style(), config.flags());
        player.showBossBar(bar);
    }

    public void addBar(GameBossBar bossBar) {
        if (managedBars.add(bossBar)) {
            update();
        }
    }

    public void removeBar(GameBossBar bossBar) {
        managedBars.remove(bossBar);
    }

}
