package me.bristermitten.warzone.game.statistic;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Persistence;

import java.util.Collection;
import java.util.UUID;

public interface GamePersistence extends Persistence {
    Future<GameStatistic> load(UUID gameId);

    Future<Collection<GameStatistic>> loadAll();

    Future<Void> save(GameStatistic gameStatistic);
}
