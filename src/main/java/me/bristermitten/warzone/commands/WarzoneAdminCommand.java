package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.Null;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Map;

@CommandAlias("warzone")
@Subcommand("admin")
@CommandPermission("warzone.admin")
public class WarzoneAdminCommand extends BaseCommand {
    private final LangService langService;
    private final PlayerManager playerManager;
    private final XPHandler xpHandler;

    @Inject
    public WarzoneAdminCommand(LangService langService, PlayerManager playerManager, XPHandler xpHandler) {
        this.langService = langService;
        this.playerManager = playerManager;
        this.xpHandler = xpHandler;
    }

    @Subcommand("reset")
    @CommandPermission("warzone.admin.reset")
    public void reset(CommandSender sender, OfflinePlayer target) {
        playerManager.loadPlayer(target.getUniqueId(), targetPlayer -> {
            targetPlayer.setLevel(0);
            targetPlayer.setXp(0);

            langService.send(sender, langConfig -> langConfig.adminLang().statsReset(),
                    Map.of("{player}", Null.get(target.getName(), target.getUniqueId().toString())));
        });
    }

    @Subcommand("xp set")
    @CommandPermission("warzone.admin.xp.set")
    public void setXp(CommandSender sender, OfflinePlayer target, long newXP) {
        if (newXP < 0) {
            langService.sendMessage(sender,
                    config -> config.errorLang().xpNotPositive());
            return;
        }
        playerManager.loadPlayer(target.getUniqueId(), targetPlayer -> {
            xpHandler.setXP(targetPlayer, newXP);

            langService.send(sender, langConfig -> langConfig.adminLang().statsReset(),
                    Map.of("{player}", Null.get(target.getName(), target.getUniqueId().toString())));
        });
    }
}
