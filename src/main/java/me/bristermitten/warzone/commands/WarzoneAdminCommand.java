package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.state.InLobbyState;
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

    public static final String PLAYER_PLACEHOLDER = "{player}";
    private final LangService langService;
    private final PlayerManager playerManager;
    private final XPHandler xpHandler;
    private final GameManager gameManager;

    @Inject
    public WarzoneAdminCommand(LangService langService, PlayerManager playerManager, XPHandler xpHandler, GameManager gameManager) {
        this.langService = langService;
        this.playerManager = playerManager;
        this.xpHandler = xpHandler;
        this.gameManager = gameManager;
    }

    @HelpCommand
    @Default
    public void help() {
        //noinspection deprecation
        showCommandHelp();
    }

    @Subcommand("reset")
    @CommandPermission("warzone.admin.reset")
    @CommandCompletion("@offlinePlayers")
    @Description("Reset a player's Level and XP")
    public void reset(CommandSender sender, OfflinePlayer target) {
        playerManager.loadPlayer(target.getUniqueId(), targetPlayer -> {
            targetPlayer.setLevel(0);
            targetPlayer.setXp(0);

            langService.send(sender, langConfig -> langConfig.adminLang().statsReset(),
                    Map.of(PLAYER_PLACEHOLDER, Null.get(target.getName(), target.getUniqueId().toString())));
        });
    }

    @Subcommand("forcestart")
    @CommandPermission("warzone.admin.forcestart")
    @CommandCompletion("@arenas=inUse")
    @Description("Force a game to start the lobby timer, ignoring player limits")
    public void forceStart(CommandSender sender, @Conditions("inUse") Arena arena) {
        var gameToStart = gameManager.getGames().stream().filter(game -> game.getArena().equals(arena))
                .findFirst().orElseThrow();

        if (!(gameToStart.getState() instanceof InLobbyState)) {
            langService.sendMessage(sender, langConfig -> langConfig.errorLang().cannotStartGame(),
                    Map.of("{arena}", arena.name()));
            return;
        }
        gameToStart.getPreGameLobbyTimer().forceStart();
    }

    @Subcommand("xp set")
    @CommandPermission("warzone.admin.xp.set")
    @CommandCompletion("@offlinePlayers")
    @Description("Set a player's XP")
    public void setXp(CommandSender sender, OfflinePlayer target, long newXP) {
        if (newXP < 0) {
            langService.sendMessage(sender,
                    config -> config.errorLang().xpNotPositive());
            return;
        }
        playerManager.loadPlayer(target.getUniqueId(), targetPlayer -> {
            xpHandler.setXP(targetPlayer, newXP);

            langService.send(sender, langConfig -> langConfig.adminLang().xpSet(),
                    Map.of(PLAYER_PLACEHOLDER, Null.get(target.getName(), target.getUniqueId().toString()),
                            "{value}", newXP));
        });
    }

    @Subcommand("xp get")
    @CommandPermission("warzone.admin.xp.get")
    @CommandCompletion("@offlinePlayers")
    @Description("Get a player's XP")
    public void getXp(CommandSender sender, OfflinePlayer target) {
        playerManager.loadPlayer(target.getUniqueId(), targetPlayer ->
                langService.send(sender, langConfig -> langConfig.adminLang().xpGet(),
                        Map.of(PLAYER_PLACEHOLDER, Null.get(target.getName(), target.getUniqueId().toString()),
                                "{value}", targetPlayer.getXp())));
    }
}
