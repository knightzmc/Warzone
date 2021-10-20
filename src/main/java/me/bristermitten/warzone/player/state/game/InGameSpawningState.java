package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * When a player is in an active game, whilst spawning
 * This could include dropping with the {@link me.bristermitten.warzone.game.spawning.ElytraPlayerSpawner}
 */
public class InGameSpawningState extends InGameState {
    @Inject
    InGameSpawningState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, BossBarManager bossBarManager, GameRepository gameRepository) {
        super(scoreboardManager, channel, bossBarManager, gameRepository);
    }
}
