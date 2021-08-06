package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.init.ArenaChestFiller;

public class InLobbyState implements GameState {
    private final ArenaChestFiller arenaChestFiller;

    public InLobbyState(ArenaChestFiller arenaChestFiller) {
        this.arenaChestFiller = arenaChestFiller;
    }

    @Override
    public void onEnter(Game game) {
        arenaChestFiller.add(game.getArena()); // start adding chests
    }

    @Override
    public void onLeave(Game game) {

    }
}
