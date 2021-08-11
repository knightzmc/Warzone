package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.arena.ArenasConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class PlayerStateChangeListener implements Listener {

    private final Provider<ArenasConfig> arenasConfigProvider;

    @Inject
    PlayerStateChangeListener(@NotNull Plugin plugin, Provider<ArenasConfig> arenasConfigProvider) {
        this.arenasConfigProvider = arenasConfigProvider;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerStateChange(@NotNull PlayerStateChangeEvent event) {
        if (event.getNewState() instanceof InPreGameLobbyState) {
            event.getSubject().getPlayer().peek(player -> {
                player.teleport(arenasConfigProvider.get().preGameLobbySpawnpoint().toLocation());
                player.setGameMode(GameMode.ADVENTURE);
            });
            return;
        }
        if (event.getNewState() instanceof InLobbyState) {
            event.getSubject().getPlayer().peek(player -> {
                player.teleport(arenasConfigProvider.get().lobbySpawnpoint().toLocation());
                player.setGameMode(GameMode.SURVIVAL);
            });
            return;
        }

    }
}
