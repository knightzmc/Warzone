package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.WarzonePlayer;

/**
 * Different forms of state that a player can be in
 * Despite the name, these are generally stateless, and serve as a hook to perform certain functions for when players change state,
 * such as flushing data when the player goes offline
 */
public interface PlayerState {

    /**
     * Called when a player's state becomes this state
     *
     * @param player the player whose state changed
     */
    void onStateJoin(WarzonePlayer player);

    /**
     * Called when a player's state becomes another state
     * <b><i>before</i></b> the respective {@link PlayerState#onStateJoin(WarzonePlayer)} is called
     *
     * @param player the player whose state changed
     */
    void onStateLeave(WarzonePlayer player);
}
