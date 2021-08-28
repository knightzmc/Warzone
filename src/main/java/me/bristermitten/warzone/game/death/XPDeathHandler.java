package me.bristermitten.warzone.game.death;

import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.inject.Inject;

/**
 * Death handler responsible for giving XP to a killer
 */
public class XPDeathHandler implements GameDeathHandler {
    private final PlayerManager playerManager;
    private final XPHandler xpHandler;

    @Inject
    public XPDeathHandler(PlayerManager playerManager, XPHandler xpHandler) {
        this.playerManager = playerManager;
        this.xpHandler = xpHandler;
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        Player killerPlayer = event.getEntity().getKiller();
        if (killerPlayer == null) {
            return;
        }
        playerManager.loadPlayer(killerPlayer.getUniqueId(),
                killer -> xpHandler.addXP(killer, XPConfig::kill));
    }
}
