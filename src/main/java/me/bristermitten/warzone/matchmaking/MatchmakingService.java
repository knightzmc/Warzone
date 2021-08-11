package me.bristermitten.warzone.matchmaking;

import me.bristermitten.warzone.party.Party;

public interface MatchmakingService {
    void queue(Party party);

    void unqueue(Party party);
}
