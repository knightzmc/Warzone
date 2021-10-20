package me.bristermitten.warzone.game.repository;

import me.bristermitten.warzone.game.Game;

public interface MutableGameRepository extends GameRepository {
    void add(Game game);

    void remove(Game game);
}
