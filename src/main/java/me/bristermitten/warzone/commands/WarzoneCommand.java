package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.leavemenu.LeaveRequeueMenuFactory;
import me.bristermitten.warzone.matchmaking.MatchmakingService;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import me.bristermitten.warzone.util.Consumers;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.function.BiConsumer;

@CommandAlias("warzone")
public class WarzoneCommand extends BaseCommand {
    private final MatchmakingService matchmakingService;
    private final PartyManager partyManager;
    private final InProgressState inProgressState;
    private final GameManager gameManager;
    private final LeaveRequeueMenuFactory leaveRequeueMenuFactory;
    private final LangService langService;
    private final ProtocolWrapper protocolWrapper;

    @Inject
    public WarzoneCommand(MatchmakingService matchmakingService, PartyManager partyManager, InProgressState inProgressState, GameManager gameManager, LeaveRequeueMenuFactory leaveRequeueMenuFactory, LangService langService, ProtocolWrapper protocolWrapper) {
        this.matchmakingService = matchmakingService;
        this.partyManager = partyManager;
        this.inProgressState = inProgressState;
        this.gameManager = gameManager;
        this.leaveRequeueMenuFactory = leaveRequeueMenuFactory;
        this.langService = langService;
        this.protocolWrapper = protocolWrapper;
    }

    @Subcommand("join")
    public void join(Player sender) {
        if (!gameManager.getGameContaining(sender.getUniqueId()).isEmpty()) {
            langService.send(sender, langConfig -> langConfig.gameLang().alreadyInGame());
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
                (game, isPartyOwner) -> gameManager.leave(game, sender, isPartyOwner), "Leave Game");
    }

    /**
     * Just for complying with DRY in leave() and requeue()
     * If the sender is the party owner, opens the confirmation menu, otherwise just runs the onComplete task
     */
    private void processGameLeave(Player sender, BiConsumer<Game, Boolean> onComplete, String actionName) {
        var gameOption = gameManager.getGameContaining(sender.getUniqueId());
        if (gameOption.isEmpty()) {
            langService.send(sender, langConfig -> langConfig.gameLang().notInGame());
            return;
        }
        var game = gameOption.get();
        var party = partyManager.getParty(sender);
        if (party.getSize() == PartySize.SINGLES) {
            onComplete.accept(game, false);
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
    public void requeue(Player sender) {
        processGameLeave(sender,
                (game, isPartyOwner) -> gameManager.leave(game, sender, isPartyOwner)
                        .onSuccess(Consumers.run(() -> join(sender))), "Requeue");
    }

    @Subcommand("test")
    public void test(Player sender, Player target) {
        protocolWrapper.makePlayerGlowing(target, sender);
    }
}
