package me.bristermitten.warzone.party;

import me.bristermitten.warzone.lang.LangService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.inject.Inject;
import java.util.*;

public class PartyManager {
    private final Map<UUID, Party> partiesByMember = new HashMap<>();
    private final LangService langService;

    @Inject
    public PartyManager(LangService langService) {
        this.langService = langService;
    }

    @NotNull
    public Party getParty(Player partyOwner) {
        return partiesByMember.computeIfAbsent(partyOwner.getUniqueId(),
                uid -> new Party(uid, new HashSet<>()));
    }


    public void invite(Player inviter, Player receiver) {
        var party = getParty(inviter);
        if (party.getOutgoingInvites().stream().anyMatch(i -> i.receiver().equals(receiver.getUniqueId()))) {
            langService.sendMessage(
                    inviter, config -> config.partyLang().inviteAlreadySent(),
                    Map.of("{player}", receiver.getName())
            );
            return;
        }

        langService.sendMessage(
                receiver, config -> config.partyLang().inviteReceived(),
                Map.of("{inviter}", inviter.getName(), "{inviter_uuid}", inviter.getUniqueId())
        );

        party.getOutgoingInvites().add(
                new PartyInvite(
                        party,
                        inviter.getUniqueId(), receiver.getUniqueId()
                )
        );

    }

    public @Unmodifiable Collection<PartyInvite> getInvitesFor(Player receiver) {
        return Set.copyOf(getParty(receiver).getOutgoingInvites());
    }

    public void accept(PartyInvite invite) {
        var receivingPlayer = Bukkit.getPlayer(invite.receiver());
        if (receivingPlayer == null) {
            return;
        }
        if (!invite.invitingTo().getOutgoingInvites().contains(invite)) {
            langService.sendMessage(receivingPlayer, config -> config.partyLang().invalidInvite());
            return;
        }

        add(invite.invitingTo(), invite.receiver());
    }

    private void add(Party party, UUID joining) {
        party.add(joining);
        partiesByMember.put(joining, party);
        // TODO messages
    }

    public void leave(Party party, @NotNull Player leaver) {

        if (party.isEmpty()) { // This is a bit of a lie, everyone is in a party, but if a party is just you, can it really be considered a party?
            langService.sendMessage(leaver, langConfig -> langConfig.partyLang().noParty());
            return;
        }

        party.getOtherPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player ->
                        langService.sendMessage(player, config -> config.partyLang().partyUserLeft(),
                                Map.of("{leaver}", leaver.getName())));

        party.getOtherPlayers().remove(leaver.getUniqueId());
        langService.sendMessage(leaver, config -> config.partyLang().partyYouLeft());

        if (leaver.getUniqueId().equals(party.getOwner())) {
            // destroy one and another will take its place
            var nextOwnerOptional = party.getOtherPlayers()
                    .stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .findFirst();

            if (nextOwnerOptional.isEmpty()) {
                throw new IllegalStateException("what?");
            }

            var nextOwner = nextOwnerOptional.get();
            party.setOwner(nextOwner.getUniqueId());

            party.getOtherPlayers().remove(nextOwner.getUniqueId());
            langService.sendMessage(nextOwner, config -> config.partyLang().partyPromotedLeft());
        }

    }
}
