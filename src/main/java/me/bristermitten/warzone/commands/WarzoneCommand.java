package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.bristermitten.warzone.game.init.ChunkChestFiller;
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

    @Inject
    public WarzoneCommand(ChunkChestFiller chunkChestFiller, LootTableManager lootTableManager, MatchmakingService matchmakingService, PartyManager partyManager) {
        this.chunkChestFiller = chunkChestFiller;
        this.lootTableManager = lootTableManager;
        this.matchmakingService = matchmakingService;
        this.partyManager = partyManager;
    }

    @Subcommand("join")
    public void join(Player sender) {
        matchmakingService.queue(partyManager.getParty(sender));
        sender.sendMessage("You've been placed in a queue");
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        throw new UnsupportedOperationException("TODO");
    }

    @Subcommand("test")
    public void test(Player sender, @Default("0.5") float chestChance, @Default("basic") String lootTableName) {
        var table = lootTableManager.getTables().apply(lootTableName);
        chunkChestFiller.fill(sender.getChunk(), table, chestChance);
    }
}
