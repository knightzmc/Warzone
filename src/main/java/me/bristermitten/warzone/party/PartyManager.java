package me.bristermitten.warzone.party;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {
    private final Map<UUID, Party> partiesByOwner = new HashMap<>();
    private final Multimap<UUID, PartyInvite> outgoingInvites = HashMultimap.create();


    public void invite(UUID inviter, UUID receiver) {
        // TODO
    }
}
