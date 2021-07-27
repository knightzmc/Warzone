package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyInvite;
import me.bristermitten.warzone.party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandAlias("party|p")
public class PartyCommand extends BaseCommand {
    private final PartyManager partyManager;
    private final LangService langService;

    @Inject
    public PartyCommand(PartyManager partyManager, LangService langService) {
        this.partyManager = partyManager;
        this.langService = langService;
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void invite(Player inviter, OnlinePlayer invitee) {
        partyManager.invite(inviter, inviter.getPlayer());
    }

    @Subcommand("join|accept")
    public void join(Player sender, @Optional OnlinePlayer inviter) {
        var invites = partyManager.getInvitesFor(sender);
        if (invites.isEmpty()) {
            langService.sendMessage(sender, config -> config.partyLang().noInvites());
            return;
        }
        if (inviter != null) {
            var fromPlayer = invites.stream()
                    .filter(invite -> invite.sender().equals(inviter.getPlayer().getUniqueId()))
                    .findFirst();
            if (fromPlayer.isEmpty()) {
                langService.sendMessage(sender, config -> config.partyLang().noInvitesFromPlayer(),
                        Map.of("{inviter}", inviter.getPlayer().getName()));
                return;
            }
            partyManager.accept(fromPlayer.get());
            return;
        }

        if (invites.size() == 1) {
            var nextInvite = invites.iterator().next();
            partyManager.accept(nextInvite);
            return;
        }

        langService.sendMessage(sender, config -> config.partyLang().multipleInvites(),
                Map.of("{invitations}", invites.stream().map(PartyInvite::sender)
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.joining(","))));
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        Party party = partyManager.getParty(sender);
        partyManager.leave(party, sender);
    }
}
