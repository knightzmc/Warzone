package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.GameMode;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;


public class InPreGameLobbyState implements PlayerState {


    private final ChatChannel channel;

    private final Provider<ArenasConfigDAO> arenaConfigProvider;
    private final GameManager gameManager;
    private final BossBarManager bossBarManager;

    @Inject
    public InPreGameLobbyState(ScoreboardManager scoreboardManager, @Named("preGameLobby") ChatChannel channel, Provider<ArenasConfigDAO> arenaConfigProvider, GameManager gameManager, BossBarManager bossBarManager) {
        this.channel = channel;
        this.arenaConfigProvider = arenaConfigProvider;
        this.gameManager = gameManager;
        this.bossBarManager = bossBarManager;
    }

    @Override
    public void onEnter(WarzonePlayer warzonePlayer) {
        warzonePlayer.getPlayer().peek(player -> {
            player.teleport(arenaConfigProvider.get().preGameLobbySpawnpoint().toLocation());
            player.setGameMode(GameMode.ADVENTURE);
            var game = gameManager.getGameContaining(warzonePlayer)
                    .getOrElseThrow(() -> new IllegalArgumentException("Player is not in a game"));
            game.getPreGameLobbyTimer().start();
            bossBarManager.show(player.getUniqueId(), game.getPreGameLobbyTimer().getBossBar());
        });
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        gameManager.getGameContaining(player).peek(game ->
                bossBarManager.hide(player.getPlayerId(), game.getPreGameLobbyTimer().getBossBar()));

    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
