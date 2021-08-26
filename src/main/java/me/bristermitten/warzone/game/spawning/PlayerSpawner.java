package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.WarzonePlayer;

public interface PlayerSpawner {
    void spawn(Game game, Party party);

    /**
     * Spawn a single player
     * This is done in the case when a player's party should not be spawned with them, for example
     * when a single player wins the gulag
     * <p>
     * The player should be online
     */
    void spawn(Game game, WarzonePlayer player);
}
