package me.bristermitten.warzone.game.statistic;

import io.vavr.collection.Set;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.database.Persistence;

import java.util.UUID;

public interface GamePersistence extends Persistence {
    Future<Option<GameStatistic>> load(UUID gameId);

    Future<Set<GameStatistic>> loadAll();

    Future<Void> save(GameStatistic gameStatistic);
}
