package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

public class InGulagQueuingState extends InGulagState {
    private final GameRepository gameRepository;

    @Inject
    InGulagQueuingState(ScoreboardManager scoreboardManager,
                        @Named("inGame") ChatChannel channel,
                        BossBarManager bossBarManager,
                        GameRepository gameRepository) {
        super(scoreboardManager, channel, gameRepository, bossBarManager);
        this.gameRepository = gameRepository;
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        super.onEnter(player);
        var game = gameRepository.getGameContaining(player.getPlayerId())
                .getOrElseThrow(() -> new IllegalStateException("Player is not in a game"));

        var spawnArea = game.getArena().gulagConfig().spawnArea().toLocation(game.getArena().getWorldOrThrow());
        player.getPlayer().peek(p -> p.teleport(spawnArea));
    }
}
