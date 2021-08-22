package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.bossbar.BossBarManager;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.Objects;

public class SpectatingState extends InGameState {
    private final LangService langService;
    private final Provider<GameConfig> gameConfigProvider;

    @Inject
    SpectatingState(ScoreboardManager scoreboardManager,
                    @Named("inGame") ChatChannel channel,
                    LangService langService,
                    GameManager gameManager,
                    BossBarManager bossBarManager,
                    Provider<GameConfig> gameConfigProvider) {
        super(scoreboardManager, channel, gameManager, bossBarManager);
        this.langService = langService;
        this.gameConfigProvider = gameConfigProvider;
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer warzonePlayer) {
        super.onEnter(warzonePlayer);
        var player = warzonePlayer.getPlayer().getOrElseThrow(
                () -> new IllegalStateException("Player must be online!")
        );

        player.getLocation().getBlock().setType(Material.CHEST);
        var chest = (Chest) player.getLocation().getBlock().getState();
        chest.getBlockInventory().setContents(

                Arrays.stream(player.getInventory().getContents())
                        .filter(Objects::nonNull)
                        .toArray(ItemStack[]::new)
        );
        player.getInventory().clear();

        var spectatorConfig = gameConfigProvider.get().spectatorConfig();
        if (spectatorConfig.allowFlight()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        player.setInvisible(spectatorConfig.invisible());

        player.setFireTicks(0);
        player.setInvulnerable(true);

        spectatorConfig.potionEffects().forEach(player::addPotionEffect);
        spectatorConfig.hotbarItems().forEach((slot, item) -> player.getInventory().setItem(slot, item.item()));

        langService.sendTitle(player, config -> config.gameLang().playerOut());
    }

    @Override
    public void onLeave(WarzonePlayer warzonePlayer) {
        super.onLeave(warzonePlayer);
        warzonePlayer.getPlayer().peek(player -> {
            player.clearTitle();
            player.getInventory().clear();
            player.setAllowFlight(false);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setInvisible(false);
            player.setInvulnerable(false);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        });
    }
}
