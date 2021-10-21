package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameJoinLeaveService;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.leavemenu.LeaveRequeueMenuFactory;
import me.bristermitten.warzone.matchmaking.MatchmakingService;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.util.Consumers;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.function.BiConsumer;

@CommandAlias("warzone")
public class WarzoneCommand extends BaseCommand {
    private final MatchmakingService matchmakingService;
    private final PartyManager partyManager;
    private final LeaveRequeueMenuFactory leaveRequeueMenuFactory;
    private final LangService langService;
    private final GameRepository gameRepository;
    private final GameJoinLeaveService gameJoinLeaveService;

    @Inject
    public WarzoneCommand(MatchmakingService matchmakingService,
                          PartyManager partyManager,
                          LeaveRequeueMenuFactory leaveRequeueMenuFactory,
                          LangService langService,
                          GameRepository gameRepository,
                          GameJoinLeaveService gameJoinLeaveService) {
        this.matchmakingService = matchmakingService;
        this.partyManager = partyManager;
        this.leaveRequeueMenuFactory = leaveRequeueMenuFactory;
        this.langService = langService;
        this.gameRepository = gameRepository;
        this.gameJoinLeaveService = gameJoinLeaveService;
    }

    @Subcommand("join")
    @CommandPermission("warzone.join")
    @Description("Queue your party for a game")
    public void join(Player sender) {
        if (!gameRepository.getGameContaining(sender.getUniqueId()).isEmpty()) {
            langService.send(sender, langConfig -> langConfig.gameLang().alreadyInGame());
            return;
        }
        matchmakingService.queue(partyManager.getParty(sender));
        sender.sendMessage("You've been placed in a queue"); // TODO make this properly localized
    }

    @Subcommand("leave")
    @CommandPermission("warzone.leave")
    @Description("Leave your current game")
    public void leave(Player sender) {
        processGameLeave(sender,
                (game, isPartyOwner) -> leave(sender, game, isPartyOwner),
                "Leave Game");
    }

    private Future<Unit> leave(Player sender, Game game, Boolean isPartyOwner) {
        if (isPartyOwner) { //NOSONAR shush
            var party = partyManager.getParty(sender);
            return gameJoinLeaveService.leave(game, party);
        } else {
            return gameJoinLeaveService.leave(game, sender.getUniqueId());
        }
    }

    /**
     * Just for complying with DRY in leave() and requeue()
     * If the sender is the party owner, opens the confirmation menu, otherwise just runs the onComplete task
     */
    private void processGameLeave(Player sender, BiConsumer<Game, @NotNull Boolean> onComplete, String actionName) {
        var gameOption = gameRepository.getGameContaining(sender.getUniqueId());
        if (gameOption.isEmpty()) {
            langService.send(sender, langConfig -> langConfig.gameLang().notInGame());
            return;
        }
        var game = gameOption.get();
        var party = partyManager.getParty(sender);
        if (party.getSize() == PartySize.SINGLES) {
            onComplete.accept(game, true);
            return;
        }
        if (!party.getOwner().equals(sender.getUniqueId())) {
            onComplete.accept(game, false);
            return;
        }
        leaveRequeueMenuFactory.create(
                        () -> onComplete.accept(game, true),
                        actionName)
                .open(sender);
    }

    @Subcommand("requeue")
    @CommandPermission("warzone.requeue")
    @Description("Leave your current game and queue for a new one")
    public void requeue(Player sender) {
        processGameLeave(sender,
                (game, isPartyOwner) -> leave(sender, game, isPartyOwner).onSuccess(Consumers.run(() -> join(sender))),
                "Requeue");
    }


    @HelpCommand
    @Default
    public void help() {
        //noinspection deprecation
        showCommandHelp();
    }
}
