package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.init.ChunkChestFiller;
import me.bristermitten.warzone.game.leavemenu.LeaveRequeueMenuFactory;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.loot.LootTableManager;
import me.bristermitten.warzone.matchmaking.MatchmakingService;
import me.bristermitten.warzone.party.PartyManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.function.Consumer;

@CommandAlias("warzone")
public class WarzoneCommand extends BaseCommand {
    private final ChunkChestFiller chunkChestFiller;
    private final LootTableManager lootTableManager;
    private final MatchmakingService matchmakingService;
    private final PartyManager partyManager;
    private final InProgressState inProgressState;
    private final GameManager gameManager;
    private final LeaveRequeueMenuFactory leaveRequeueMenuFactory;
    private final LangService langService;

    @Inject
    public WarzoneCommand(ChunkChestFiller chunkChestFiller, LootTableManager lootTableManager, MatchmakingService matchmakingService, PartyManager partyManager, InProgressState inProgressState, GameManager gameManager, LeaveRequeueMenuFactory leaveRequeueMenuFactory, LangService langService) {
        this.chunkChestFiller = chunkChestFiller;
        this.lootTableManager = lootTableManager;
        this.matchmakingService = matchmakingService;
        this.partyManager = partyManager;
        this.inProgressState = inProgressState;
        this.gameManager = gameManager;
        this.leaveRequeueMenuFactory = leaveRequeueMenuFactory;
        this.langService = langService;
    }

    @Subcommand("join")
    public void join(Player sender) {
        if (!gameManager.getGameContaining(sender.getUniqueId()).isEmpty()) {
            langService.sendMessage(sender, langConfig -> langConfig.gameLang().alreadyInGame());
            return;
        }
        matchmakingService.queue(partyManager.getParty(sender));
        sender.sendMessage("You've been placed in a queue");
    }

    @Subcommand("start")
    public void start(Player sender) {
        gameManager.getGames().forEach(game -> {
            if (!(game.getState() instanceof InProgressState))
                game.setCurrentState(inProgressState);
        });
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        processGameLeave(sender,
                game -> gameManager.leave(game, sender, true), "Leave Game");
    }

    /**
     * Just for complying with DRY in leave() and requeue()
     * If the sender is the party owner, opens the confirmation menu, otherwise just runs the onComplete task
     */
    private void processGameLeave(Player sender, Consumer<Game> onComplete, String actionName) {
        var gameOption = gameManager.getGameContaining(sender.getUniqueId());
        if (gameOption.isEmpty()) {
            langService.sendMessage(sender, langConfig -> langConfig.gameLang().notInGame());
            return;
        }
        var game = gameOption.get();
        var party = partyManager.getParty(sender);
        if (party.getOwner().equals(sender.getUniqueId())) {
            leaveRequeueMenuFactory.create(
                    () -> onComplete.accept(game),
                    actionName)
                    .open(sender);
        } else {
            onComplete.accept(game);
        }
    }

    @Subcommand("requeue")
    public void requeue(Player sender) {
        processGameLeave(sender,
                game -> {
                    gameManager.leave(game, sender, true);
                    join(sender);
                }, "Requeue");
    }

    @Subcommand("test")
    public void test(Player sender, @Default("0.5") float chestChance, @Default("basic") String lootTableName) {
        var table = lootTableManager.getTables().apply(lootTableName);
        chunkChestFiller.fill(sender.getChunk(), table, chestChance);
    }
}
