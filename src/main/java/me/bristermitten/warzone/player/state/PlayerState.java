package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.state.State;

/**
 * Different forms of state that a player can be in
 * Despite the name, these are generally stateless, and serve as a hook to perform certain functions for when players change state,
 * such as flushing data when the player goes offline
 */
public interface PlayerState extends State<WarzonePlayer> {

    /**
     * @return the Chat Channel associated with this state
     */
    ChatChannel getChannel();
}
