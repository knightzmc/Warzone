package me.bristermitten.warzone.game.death;

import io.vavr.Function3;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.statistic.PlayerDeath;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

/**
 * Death handler responsible for recording statistics
 */
public class StatisticsDeathHandler implements GameDeathHandler {
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    @Inject
    public StatisticsDeathHandler(PlayerManager playerManager, GameManager gameManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
    }

    private Unit recordDeath(UUID died, Player player, Game game) {
        game.recordDeath(new PlayerDeath(
                died,
                Option.of(Bukkit.getPlayer(died)).flatMap(p -> Option.of(p.getKiller())).map(Player::getUniqueId).getOrNull(),
                Instant.now(),
                Option.of(player.getLastDamageCause())
                        .map(EntityDamageEvent::getCause)
                        .map(cause -> switch (cause) {
                            case FALL -> PlayerDeath.DeathCause.FALL_DAMAGE;
                            case CUSTOM -> PlayerDeath.DeathCause.BORDER;
                            default -> PlayerDeath.DeathCause.OTHER;
                        }).getOrElse(PlayerDeath.DeathCause.UNKNOWN)
        ));
        return Unit.INSTANCE;
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        UUID died = event.getEntity().getUniqueId();
        playerManager.loadPlayer(died,
                whoDied -> {
                    whoDied.setDeaths(whoDied.getDeaths() + 1);
                    var containingGame = gameManager.getGameContaining(whoDied.getPlayerId());
                    containingGame
                            .peek(game -> Function3.of(this::recordDeath).apply(died, event.getEntity()))
                            .flatMap(game -> game.getInfo(whoDied.getPlayerId()))
                            .peek(playerInformation -> playerInformation.setDeathCount(playerInformation.getDeathCount() + 1));
                });

        Player killerPlayer = event.getEntity().getKiller();
        if (killerPlayer == null) {
            return;
        }

        playerManager.loadPlayer(killerPlayer.getUniqueId(), killer -> {
            killer.setKills(killer.getKills() + 1);
            gameManager.getGameContaining(killerPlayer.getUniqueId())
                    .flatMap(game -> game.getInfo(killerPlayer.getUniqueId()))
                    .peek(information -> information.setKillCount(information.getKillCount() + 1));
        });

    }
}
