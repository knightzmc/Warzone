package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.state.State;

public interface GameState extends State<Game> {
    void onEnter(Game game);

    void onLeave(Game game);
}
