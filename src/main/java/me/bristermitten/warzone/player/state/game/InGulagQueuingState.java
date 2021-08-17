package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class InGulagQueuingState extends InGulagState {
    private final GameManager gameManager;

    @Inject
    InGulagQueuingState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, GameManager gameManager) {
        super(scoreboardManager, channel);
        this.gameManager = gameManager;
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        super.onEnter(player);
        var game = gameManager.getGameContaining(player.getPlayerId())
                .getOrElseThrow(() -> new IllegalStateException("Player is not in a game"));

        var spawnArea = game.getArena().gulagConfig().spawnArea().toLocation(game.getArena().forceGetWorld());
        player.getPlayer().peek(p -> p.teleport(spawnArea));
    }
}
