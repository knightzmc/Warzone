package me.bristermitten.warzone.game.config;

import me.bristermitten.warzone.bossbar.BossBarConfig;
import me.bristermitten.warzone.game.spawning.PlayerSpawningMethod;
import me.bristermitten.warzone.game.spawning.bus.BattleBusConfig;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public record GameConfig(
        SpectatorConfig spectatorConfig,
        PlayerSpawningMethod spawningMethod,
        GameStartTimerConfig gameStartTimerConfig,
        BattleBusConfig battleBusConfig,
        int gameEndTimer
) {
    public record GameStartTimerConfig(
            double threshold,
            int lengthMillis,
            BossBarConfig bossBarConfig
    ) {
    }

    public record SpectatorConfig(
            boolean allowFlight,
            boolean invisible,
            List<PotionEffect> potionEffects,
            Map<Integer, SpectatorItem> hotbarItems
    ) {
        public record SpectatorItem(ItemStack item, SpectatorModeHotbarItemAction action) {

        }
    }
}
