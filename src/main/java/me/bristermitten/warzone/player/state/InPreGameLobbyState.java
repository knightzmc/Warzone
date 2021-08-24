package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.GameMode;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;


public class InPreGameLobbyState implements PlayerState {


    private final ScoreboardManager scoreboardManager;
    private final ChatChannel channel;

    private final Provider<ArenasConfigDAO> arenaConfigProvider;

    @Inject
    public InPreGameLobbyState(ScoreboardManager scoreboardManager, @Named("preGameLobby") ChatChannel channel, Provider<ArenasConfigDAO> arenaConfigProvider) {
        this.scoreboardManager = scoreboardManager;
        this.channel = channel;
        this.arenaConfigProvider = arenaConfigProvider;
    }


    @Override
    public void onEnter(WarzonePlayer warzonePlayer) {
        warzonePlayer.getPlayer().peek(player -> {
            player.teleport(arenaConfigProvider.get().preGameLobbySpawnpoint().toLocation());
            player.setGameMode(GameMode.ADVENTURE);
        });
    }

    @Override
    public void onLeave(WarzonePlayer player) {

    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
