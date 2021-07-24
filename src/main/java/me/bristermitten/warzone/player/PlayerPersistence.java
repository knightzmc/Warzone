package me.bristermitten.warzone.player;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Persistence;

import java.util.UUID;

public interface PlayerPersistence extends Persistence {
    Future<WarzonePlayer> load(UUID id);

    Future<Void> save(WarzonePlayer player);
}
