package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameJoinLeaveService;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.storage.PlayerPersistence;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class OfflineState implements PlayerState {

    private final PlayerPersistence playerPersistence;
    private final GameRepository gameRepository;
    private final GameJoinLeaveService gameJoinLeaveService;
    private final PartyManager partyManager;

    @Inject
    public OfflineState(PlayerPersistence playerPersistence, GameRepository gameRepository, GameJoinLeaveService gameJoinLeaveService, PartyManager partyManager) {
        this.playerPersistence = playerPersistence;
        this.gameRepository = gameRepository;
        this.gameJoinLeaveService = gameJoinLeaveService;
        this.partyManager = partyManager;
    }

    @Override
    public void onEnter(WarzonePlayer player) {
        playerPersistence.save(player); // Flush any data
        var offlinePlayer = player.getOfflinePlayer();
        gameRepository.getGameContaining(offlinePlayer.getUniqueId())
                .peek(game -> gameJoinLeaveService.leave(game, offlinePlayer.getUniqueId()))
                .onEmpty(() -> partyManager.leave(partyManager.getParty(player), offlinePlayer));
        /*
        TODO should a WarzonePlayer be 1 object per uuid? if not, need some way of "invalidating" them so functions know to retrieve a new one
         If so, we should probably change PlayerPersistence#load so that it changes the existing WarzonePlayer
        */
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        // Nothing to do
    }

    @Override
    public @NotNull
    ChatChannel getChannel() {
        return new ChatChannel("offline", () -> ""); // This doesn't really matter, nobody will be online to see it :)
    }
}
