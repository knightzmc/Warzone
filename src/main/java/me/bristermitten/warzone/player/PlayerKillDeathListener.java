package me.bristermitten.warzone.player;

import me.bristermitten.warzone.config.ConfigurationProvider;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.game.InGulagArenaState;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerKillDeathListener implements EventListener {
    private final PlayerManager playerManager;
    private final XPHandler xpHandler;
    private final ConfigurationProvider<XPConfig> xpConfig;
    private final GameManager gameManager;


    @Inject
    public PlayerKillDeathListener(PlayerManager playerManager, XPHandler xpHandler, ConfigurationProvider<XPConfig> xpConfig, GameManager gameManager) {
        this.playerManager = playerManager;
        this.xpHandler = xpHandler;
        this.xpConfig = xpConfig;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onKill(@NotNull PlayerDeathEvent event) {
        event.setCancelled(true);
        playerManager.loadPlayer(event.getEntity().getUniqueId(),
                whoDied -> {
                    whoDied.setDeaths(whoDied.getDeaths() + 1);
                    var containingGame = gameManager.getGameContaining(whoDied.getPlayerId());
                    containingGame
                            .flatMap(game -> game.getInfo(whoDied.getPlayerId()))
                            .peek(playerInformation -> playerInformation.setDeathCount(playerInformation.getDeathCount() + 1))
                            .peek(unused -> gameManager.handleDeath(containingGame.get(), whoDied.getPlayerId(), event));
                });

        Player killerPlayer = event.getEntity().getKiller();
        if (killerPlayer == null) {
            return;
        }
        playerManager.loadPlayer(killerPlayer.getUniqueId(), killer -> {
            killer.setKills(killer.getKills() + 1);
            xpHandler.addXP(killer, xpConfig.get().kill());
            if (killer.getCurrentState() instanceof InGulagArenaState) {
                playerManager.setState(killer, PlayerStates::aliveState);
            }
            gameManager.getGameContaining(killerPlayer.getUniqueId())
                    .flatMap(game -> game.getInfo(killerPlayer.getUniqueId()))
                    .peek(information -> information.setKillCount(information.getKillCount() + 1));
        });
    }
}
