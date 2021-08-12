package me.bristermitten.warzone.player;

import me.bristermitten.warzone.config.ConfigurationProvider;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerKillDeathListener implements Listener {
    private final PlayerManager playerManager;
    private final XPHandler xpHandler;
    private final ConfigurationProvider<XPConfig> xpConfig;
    private final GameManager gameManager;


    @Inject
    public PlayerKillDeathListener(@NotNull Plugin plugin, PlayerManager playerManager, XPHandler xpHandler, ConfigurationProvider<XPConfig> xpConfig, GameManager gameManager) {
        this.playerManager = playerManager;
        this.xpHandler = xpHandler;
        this.xpConfig = xpConfig;
        this.gameManager = gameManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onKill(@NotNull PlayerDeathEvent e) {
        playerManager.loadPlayer(e.getEntity().getUniqueId(),
                died -> died.setDeaths(died.getDeaths() + 1));

        Player killerPlayer = e.getEntity().getKiller();
        if (killerPlayer == null) {
            return;
        }
        playerManager.loadPlayer(killerPlayer.getUniqueId(), (killer -> {
            killer.setKills(killer.getKills() + 1);
            xpHandler.addXP(killer, xpConfig.get().kill());
            gameManager.getGameContaining(killerPlayer.getUniqueId())
                    .flatMap(game -> game.getInfo(killerPlayer.getUniqueId()))
                    .peek(information -> information.setKills(information.getKills() + 1));
        }));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true); // todo remove
    }
}
