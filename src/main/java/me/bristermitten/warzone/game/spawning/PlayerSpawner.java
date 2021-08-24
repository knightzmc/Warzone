package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;

public interface PlayerSpawner {
    void spawn(Game game, Party party);
}
