package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;


public class InLobbyState implements PlayerState {

    private static final Logger LOGGER = LoggerFactory.getLogger(InLobbyState.class);
    private final ChatChannel channel;
    private final ScoreboardManager scoreboardManager;
    private final Provider<ArenasConfigDAO> arenaConfigProvider;

    @Inject
    public InLobbyState(@Named("lobby") ChatChannel channel, ScoreboardManager scoreboardManager, Provider<ArenasConfigDAO> arenaConfigProvider) {
        this.channel = channel;
        this.scoreboardManager = scoreboardManager;
        this.arenaConfigProvider = arenaConfigProvider;
    }


    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        LOGGER.debug("Player {} entering InLobbyState", player);
        player.getPlayer().peek(p -> {
            LOGGER.debug("Running lobbystate hooks for {}", p);
            p.getInventory().clear();
            scoreboardManager.show(p, ScoreboardConfig::lobby);
            p.teleport(arenaConfigProvider.get().lobbySpawnpoint().toLocation());
            p.setGameMode(GameMode.SURVIVAL);
            p.setInvulnerable(false);
            LOGGER.debug("Finished lobbystate hooks for {}", p);
        });
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        // nothing to do
    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
