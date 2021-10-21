package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.data.WorldAngledPoint;
import me.bristermitten.warzone.game.cleanup.GameEndingService;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.game.spawning.bus.BattleBusFactory;
import me.bristermitten.warzone.game.spawning.bus.BattleBusMoveTask;
import me.bristermitten.warzone.game.state.InLobbyState;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.Null;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandAlias("warzone")
@Subcommand("admin")
@CommandPermission("warzone.admin")
public class WarzoneAdminCommand extends BaseCommand {

    public static final String PLAYER_PLACEHOLDER = "{player}";
    private final LangService langService;
    private final PlayerManager playerManager;
    private final XPHandler xpHandler;
    private final GameRepository gameRepository;
    private final GameEndingService gameEndingService;
    private final BattleBusFactory battleBusFactory;
    private final BattleBusMoveTask battleBusMoveTask;

    @Inject
    public WarzoneAdminCommand(LangService langService, PlayerManager playerManager, XPHandler xpHandler, GameRepository gameRepository, GameEndingService gameEndingService, BattleBusFactory battleBusFactory, BattleBusMoveTask battleBusMoveTask) {
        this.langService = langService;
        this.playerManager = playerManager;
        this.xpHandler = xpHandler;
        this.gameRepository = gameRepository;
        this.gameEndingService = gameEndingService;
        this.battleBusFactory = battleBusFactory;
        this.battleBusMoveTask = battleBusMoveTask;
    }

    @Subcommand("test")
    public void test(Player player) {
        var direction = player.getLocation().getDirection().multiply(2).setY(0);
        var endPoint = WorldAngledPoint.fromLocation(player.getLocation().add(direction));

        var bus = battleBusFactory.createBus(
                WorldAngledPoint.fromLocation(player.getLocation()),
                endPoint,
                10000
        );
        battleBusMoveTask.start();
        bus.busEntity().addPassenger(player);
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
    public void forceStart(CommandSender sender, @Conditions("inUse") Arena arena, @Optional @Nullable Long timerSeconds) {
        var gameToStart = gameRepository.getGames()
                .filter(game -> game.getArena().equals(arena))
                .head(); // This will never throw because of the @Conditions("inUse")

        if (!(gameToStart.getState() instanceof InLobbyState) || gameToStart.getPreGameLobbyTimer().hasStarted()) {
            langService.sendMessage(sender, langConfig -> langConfig.errorLang().cannotStartGame(),
                    Map.of("{arena}", arena.name()));
            return;
        }

        if (timerSeconds != null) {
            gameToStart.getPreGameLobbyTimer().forceStart(TimeUnit.SECONDS.toMillis(timerSeconds));
        } else {
            gameToStart.getPreGameLobbyTimer().forceStart();
        }
    }

    @Subcommand("forceend")
    @CommandPermission("warzone.admin.forceend")
    @CommandCompletion("@arenas=inUse")
    @Description("Force a game to end")
    public void forceEnd(CommandSender sender, @Conditions("inUse") Arena arena) {
        var gameToEnd = gameRepository.getGames()
                .filter(game -> game.getArena().equals(arena))
                .head(); // This will never throw because of the @Conditions("inUse")

        if (!(gameToEnd.getState() instanceof InProgressState)) {
            langService.sendMessage(sender, langConfig -> langConfig.errorLang().cannotStartGame(),
                    Map.of("{arena}", arena.name()));
            return;
        }
        gameEndingService.end(gameToEnd);
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
