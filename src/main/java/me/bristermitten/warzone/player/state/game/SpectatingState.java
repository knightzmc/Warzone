package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.bossbar.BossBarManager;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Objects;

public class SpectatingState extends InGameState {
    private final LangService langService;

    @Inject
    SpectatingState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, LangService langService, GameManager gameManager, BossBarManager bossBarManager) {
        super(scoreboardManager, channel, gameManager, bossBarManager);
        this.langService = langService;
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer warzonePlayer) {
        super.onEnter(warzonePlayer);
        warzonePlayer.getPlayer().peek(player -> {

            player.getLocation().getBlock().setType(Material.CHEST);
            var chest = (Chest) player.getLocation().getBlock().getState();
            chest.getBlockInventory().setContents(
                    Arrays.stream(player.getInventory().getContents())
                            .filter(Objects::nonNull)
                            .toArray(ItemStack[]::new)
            );
            player.getInventory().clear();

            player.setAllowFlight(true);
            player.setFireTicks(0);
            player.setFlying(true);
            player.setInvisible(true);
            player.setInvulnerable(true);

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));

            langService.sendTitle(player, config -> config.gameLang().playerOut());
        });
        // TODO remove from game and stuff
    }

    @Override
    public void onLeave(WarzonePlayer warzonePlayer) {
        super.onLeave(warzonePlayer);
        warzonePlayer.getPlayer().peek(player -> {
            player.setAllowFlight(false);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setInvisible(false);
            player.setInvulnerable(false);
            player.removePotionEffect(PotionEffectType.SPEED);
        });
    }
}
