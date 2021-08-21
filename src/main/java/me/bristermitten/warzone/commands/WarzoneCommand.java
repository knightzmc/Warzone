package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.init.ChunkChestFiller;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.loot.LootTableManager;
import me.bristermitten.warzone.matchmaking.MatchmakingService;
import me.bristermitten.warzone.party.PartyManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@CommandAlias("warzone")
public class WarzoneCommand extends BaseCommand {
    private final ChunkChestFiller chunkChestFiller;
    private final LootTableManager lootTableManager;
    private final MatchmakingService matchmakingService;
    private final PartyManager partyManager;
    private final InProgressState inProgressState;
    private final GameManager gameManager;

    @Inject
    public WarzoneCommand(ChunkChestFiller chunkChestFiller, LootTableManager lootTableManager, MatchmakingService matchmakingService, PartyManager partyManager, InProgressState inProgressState, GameManager gameManager) {
        this.chunkChestFiller = chunkChestFiller;
        this.lootTableManager = lootTableManager;
        this.matchmakingService = matchmakingService;
        this.partyManager = partyManager;
        this.inProgressState = inProgressState;
        this.gameManager = gameManager;
    }

    @Subcommand("join")
    public void join(Player sender) {
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
//        gameManager.getGameContaining(sender.getUniqueId())
//                .peek(game -> gameManager.jo)
        throw new UnsupportedOperationException("TODO");
    }

    @Subcommand("test")
    public void test(Player sender, @Default("0.5") float chestChance, @Default("basic") String lootTableName) {
        var table = lootTableManager.getTables().apply(lootTableName);
        chunkChestFiller.fill(sender.getChunk(), table, chestChance);
    }
}
