package me.bristermitten.warzone.bossbar;

import me.bristermitten.warzone.bossbar.game.GameBossBar;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface BossBarManager {
    /**
     * Renders a given {@link GameBossBar} and shows it to a given {@link Player}
     * If the player is already viewing bossBar, nothing will happen.
     * <p>
     * This will keep the boss bar visible until
     */
    void show(UUID player, CustomBossBar bossBar);

    /**
     * Hides a given {@link GameBossBar} from a given {@link Player}
     */
    void hide(UUID player, CustomBossBar bossBar);

    void updateAll();

    void update(CustomBossBar bossBar);
}
