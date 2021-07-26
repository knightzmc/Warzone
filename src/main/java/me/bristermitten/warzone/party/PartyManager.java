package me.bristermitten.warzone.party;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.bristermitten.warzone.lang.LangService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {
    private final Map<UUID, Party> partiesByOwner = new HashMap<>();
    private final Multimap<UUID, PartyInvite> outgoingInvites = HashMultimap.create();
    private final LangService langService;

    @Inject
    public PartyManager(LangService langService) {
        this.langService = langService;
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
                inviter, config -> config.partyLang().inviteSent(),
                Map.of("{inviter}", inviter.getName())
        );
    }
}
