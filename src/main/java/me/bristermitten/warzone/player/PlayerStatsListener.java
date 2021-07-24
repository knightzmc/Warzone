package me.bristermitten.warzone.player;

import me.bristermitten.warzone.database.StorageException;
import me.bristermitten.warzone.player.xp.XPConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;

public class PlayerStatsListener implements Listener {
    private final PlayerStorage storage;
    private final Provider<XPConfig> xpConfig;

    @Inject
    public PlayerStatsListener(PlayerStorage storage, Plugin plugin, Provider<XPConfig> xpConfig) {
        this.storage = storage;
        this.xpConfig = xpConfig;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        storage.load(e.getEntity().getUniqueId())
                .onFailure(t -> {
                    throw new StorageException("Could not load player data for death", t);
                })
                .onSuccess(died -> died.setDeaths(died.getDeaths() + 1));
        Player killerPlayer = e.getEntity().getKiller();
        if (killerPlayer == null) {
            return;
        }
        storage.load(killerPlayer.getUniqueId())
                .onFailure(t -> {
                    throw new StorageException("Could not load player data for killer", t);
                })
                .onSuccess(killer -> {
                    killer.setKills(killer.getKills() + 1);
                    killer.setXp(killer.getXp() + xpConfig.get().kill());
                });
    }
}
