package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;


public class InLobbyState implements PlayerState {


    private final ChatChannel channel;
    private final ScoreboardManager scoreboardManager;

    @Inject
    public InLobbyState(@Named("lobby") ChatChannel channel, ScoreboardManager scoreboardManager) {
        this.channel = channel;
        this.scoreboardManager = scoreboardManager;
    }


    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        player.getPlayer().peek(p -> {
            for (PotionEffect potionEffect : p.getActivePotionEffects()) {
                p.removePotionEffect(potionEffect.getType());
            }
            scoreboardManager.show(p, ScoreboardConfig::lobby);
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
