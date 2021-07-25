package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.storage.PlayerPersistence;

import javax.inject.Inject;

public class OfflineState implements PlayerState {

    private final PlayerPersistence playerPersistence;

    @Inject
    public OfflineState(PlayerPersistence playerPersistence) {
        this.playerPersistence = playerPersistence;
    }

    @Override
    public void onStateJoin(WarzonePlayer player) {
        playerPersistence.save(player); // Flush any data
        /*
        TODO should a WarzonePlayer be 1 object per uuid? if not, need some way of "invalidating" them so functions know to retrieve a new one
         If so, we should probably change PlayerPersistence#load so that it changes the existing WarzonePlayer
        */
    }

    @Override
    public void onStateLeave(WarzonePlayer player) {

    }

    @Override
    public ChatChannel getChannel() {
        return new ChatChannel("offline", () -> ""); // This doesn't really matter, nobody will be online to see it :)
    }
}
