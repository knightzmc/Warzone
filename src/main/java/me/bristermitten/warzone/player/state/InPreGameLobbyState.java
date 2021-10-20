package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.GameMode;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;


public class InPreGameLobbyState implements PlayerState {


    private final ScoreboardManager scoreboardManager;
    private final ChatChannel channel;

    private final Provider<ArenasConfigDAO> arenaConfigProvider;
    private final BossBarManager bossBarManager;
    private final GameRepository gameRepository;

    @Inject
    public InPreGameLobbyState(ScoreboardManager scoreboardManager,
                               @Named("preGameLobby") ChatChannel channel,
                               Provider<ArenasConfigDAO> arenaConfigProvider,
                               BossBarManager bossBarManager,
                               GameRepository gameRepository) {
        this.scoreboardManager = scoreboardManager;
        this.channel = channel;
        this.arenaConfigProvider = arenaConfigProvider;
        this.bossBarManager = bossBarManager;
        this.gameRepository = gameRepository;
    }

    @Override
    public void onEnter(WarzonePlayer warzonePlayer) {
        warzonePlayer.getPlayer().peek(player -> {
            scoreboardManager.show(player, ScoreboardConfig::preGameLobby);
            player.teleport(arenaConfigProvider.get().preGameLobbySpawnpoint().toLocation());
            player.setGameMode(GameMode.ADVENTURE);
            var game = gameRepository.getGameContaining(warzonePlayer)
                    .getOrElseThrow(() -> new IllegalArgumentException("Player is not in a game"));
            game.getPreGameLobbyTimer().start();
            bossBarManager.show(player.getUniqueId(), game.getPreGameLobbyTimer().getBossBar());
        });
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        gameRepository.getGameContaining(player).peek(game ->
                bossBarManager.hide(player.getPlayerId(), game.getPreGameLobbyTimer().getBossBar()));

    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
