package me.bristermitten.warzone.game.spawning;

import io.vavr.collection.Set;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.WarzonePlayer;

/**
 * Responsible for spawning a Player into a Game world
 * <b>Assumptions</b>
 * <ul>
 * <li>When either <code>spawn()</code> method is called, it can be assumed that any applicable {@link WarzonePlayer}s will be in the
 * {@link me.bristermitten.warzone.player.state.game.InGameSpawningState} </li>
 * <li> Once the player has finished spawning, they should be set to the {@link me.bristermitten.warzone.player.state.game.AliveState}
 * by the implementation </li>
 * </ul>
 */
public interface PlayerSpawner {
    void spawn(Game game, Party party) throws UnsupportedOperationException;

    default void spawn(Game game, Set<Party> parties) {
        parties.forEach(party -> spawn(game, party));
    }

    /**
     * Spawn a single player
     * This is done in the case when a player's party should not be spawned with them, for example
     * when a single player wins the gulag
     * <p>
     * The player should be online
     */
    void spawn(Game game, WarzonePlayer player);
}
