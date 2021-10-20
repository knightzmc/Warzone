package me.bristermitten.warzone.game;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;

import java.util.Set;

interface GameFactory {
    Game createGame(Arena arena, Set<Party> players, PartySize acceptedSize);
}
