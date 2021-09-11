package me.bristermitten.warzone.player.xp;

import io.vavr.control.Option;
import me.bristermitten.warzone.config.ConfigurationProvider;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.util.Cached;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.ToIntFunction;

@Singleton
public class XPHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(XPHandler.class);
    private final @NotNull ConfigurationProvider<XPConfig> xpConfig;

    private final @NotNull Cached<CompiledExpression> levelAlgorithm;

    @Inject
    public XPHandler(@NotNull ConfigurationProvider<XPConfig> xpConfig) {
        this.xpConfig = xpConfig;
        levelAlgorithm = new Cached<>(() -> {

            var env = new EvaluationEnvironment();
            env.setVariableNames("level", "xp");
            String expression = xpConfig.get().levelAlgorithm();
            LOGGER.debug("Compiling XP Formula {}", expression);
            return Crunch.compileExpression(expression, env);
        });
        xpConfig.addInvalidationHook(ignored -> levelAlgorithm.invalidate());
    }

    public long xpRequiredForLevel(int level) {
        return (long) levelAlgorithm.get().evaluate(level);
    }


    public void addXP(@NotNull WarzonePlayer player, ToIntFunction<XPConfig> xp) {
        addXP(player, xp.applyAsInt(this.xpConfig.get()));
    }

    public void addXP(@NotNull WarzonePlayer warzonePlayer, long xp) {
        setXP(warzonePlayer, warzonePlayer.getXp() + xp);
    }

    public void setXP(@NotNull WarzonePlayer warzonePlayer, long newXP) {
        if (newXP < 0) {
            throw new IllegalArgumentException("XP cannot be negative");
        }

        // find the new level that they should be at
        int newLevel = -1;
        if (newXP == 0) {
            newLevel = 0;
        } else {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                var xpForLevel = xpRequiredForLevel(i); // should we cache this?
                if (xpForLevel <= newXP) {
                    newLevel = i;
                } else {
                    break;
                }
            }
        }
        if (newLevel == -1) {
            throw new IllegalStateException("Could not determine new level for XP value " + newXP);
        }
        LOGGER.debug("Setting XP for {} to {}", warzonePlayer, newXP);
        var oldLevel = warzonePlayer.getLevel();
        warzonePlayer.setLevel(newLevel);
        warzonePlayer.setXp(newXP);
        if (newLevel > oldLevel) {
            levelUp(warzonePlayer, newXP, newLevel);
        }
    }

    private void levelUp(@NotNull WarzonePlayer warzonePlayer, long newXP, int newLevel) {
        LOGGER.debug("Levelling up {} to level {} (new XP = {})", warzonePlayer, newLevel, newXP);
        var event = new LevelUpEvent(warzonePlayer, newXP, newLevel);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            LOGGER.debug("LevelUpEvent was cancelled");
            return;
        }
        warzonePlayer.setXp(event.getNewXP());
        warzonePlayer.setLevel(event.getNewLevel());
        LOGGER.debug("Finished levelling up {}", warzonePlayer);
        playLevelUpSound(warzonePlayer);
    }

    private void playLevelUpSound(@NotNull WarzonePlayer warzonePlayer) {
        Option.of(xpConfig.get().levelUpSound())
                .peek(levelUpSound ->
                        warzonePlayer.getPlayer()
                                .peek(player ->
                                        player.playSound(player.getLocation(), levelUpSound, 1f, 1f)));
    }
}
