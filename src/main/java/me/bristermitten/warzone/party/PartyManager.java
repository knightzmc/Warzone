package me.bristermitten.warzone.party;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.bristermitten.warzone.lang.LangService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.*;

public class PartyManager {
    private final Map<UUID, Party> partiesByOwner = new HashMap<>();
    private final Multimap<UUID, PartyInvite> outgoingInvites = HashMultimap.create();
    private final LangService langService;

    @Inject
    public PartyManager(LangService langService) {
        this.langService = langService;
    }

    private Party getParty(Player partyOwner) {
        return partiesByOwner.computeIfAbsent(partyOwner.getUniqueId(),
                uid -> new Party(uid, new HashSet<>()));
    }

    public void invite(Player inviter, Player receiver) {
        if (outgoingInvites.get(inviter.getUniqueId())
                .stream().anyMatch(i -> i.receiver().equals(receiver.getUniqueId()))) {
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

        outgoingInvites.put(inviter.getUniqueId(), new PartyInvite(
                inviter.getUniqueId(), receiver.getUniqueId()
        ));

    }

    public Collection<PartyInvite> getInvitesFor(Player receiver) {
        return outgoingInvites.get(receiver.getUniqueId());
    }

    public void accept(PartyInvite invite) {
//TODO
    }
}
