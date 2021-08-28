package me.bristermitten.warzone.game.death;

import org.bukkit.event.entity.PlayerDeathEvent;

public interface GameDeathHandler {
    void onDeath(PlayerDeathEvent event);
}
