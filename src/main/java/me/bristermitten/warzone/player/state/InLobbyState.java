package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;


public class InLobbyState implements PlayerState {


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
        player.getPlayer().peek(p -> {
            scoreboardManager.show(p, ScoreboardConfig::lobby);
            p.teleport(arenaConfigProvider.get().lobbySpawnpoint().toLocation());
            p.setGameMode(GameMode.SURVIVAL);
            p.setInvulnerable(false);
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
