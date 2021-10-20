package me.bristermitten.warzone.game;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.util.Unit;

import java.util.UUID;

/**
 * Handles parties joining and leaving games
 */
public interface GameJoinLeaveService {

    /**
     * Makes the entire party leave the game
     *
     * @return A future completed once the leave request is completed. This is a future because blocking calls
     * like {@link me.bristermitten.warzone.player.PlayerManager#lookupPlayer(UUID)} may be needed
     */
    Future<Unit> leave(Game game, Party party);

    /**
     * Makes a single player leave the game. If the player is in a party, they will leave their party
     *
     * @return A future completed once the leave request is completed. This is a future because blocking calls
     * like {@link me.bristermitten.warzone.player.PlayerManager#lookupPlayer(UUID)} may be needed
     */
    Future<Unit> leave(Game game, UUID leaver);


    void join(Game game, Party party);

}
