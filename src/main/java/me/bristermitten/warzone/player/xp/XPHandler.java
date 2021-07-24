package me.bristermitten.warzone.player.xp;

import io.vavr.control.Option;
import me.bristermitten.warzone.config.ConfigurationProvider;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.util.Cached;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class XPHandler {
    private final ConfigurationProvider<XPConfig> xpConfig;

    private final Cached<CompiledExpression> levelAlgorithm;

    @Inject
    public XPHandler(ConfigurationProvider<XPConfig> xpConfig) {
        this.xpConfig = xpConfig;
        levelAlgorithm = new Cached<>(() -> {
            var env = new EvaluationEnvironment();
            env.setVariableNames("level", "xp");
            return Crunch.compileExpression(xpConfig.get().levelAlgorithm(), env);
        });
        xpConfig.addInvalidationHook(ignored -> levelAlgorithm.invalidate());
    }

    public void addXP(WarzonePlayer player, int xp) {
        player.setXp(player.getXp() + xp);
        var xpToNextLevel = levelAlgorithm.get().evaluate(player.getLevel(), player.getXp());
        if (player.getXp() >= xpToNextLevel) {
            levelUp(player);
        }
    }

    private void levelUp(WarzonePlayer warzonePlayer) {
        warzonePlayer.setLevel(warzonePlayer.getLevel() + 1);
        Option.of(xpConfig.get().levelUpSound())
                .forEach(levelUpSound ->
                        warzonePlayer.getPlayer()
                                .forEach(player ->
                                        player.playSound(player.getLocation(), levelUpSound, 1f, 1f)));
    }
}
