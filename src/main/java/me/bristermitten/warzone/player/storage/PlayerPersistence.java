package me.bristermitten.warzone.player.storage;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.player.WarzonePlayer;

import java.util.UUID;

public interface PlayerPersistence extends Persistence {
    Future<WarzonePlayer> load(UUID id);

    Future<Void> save(WarzonePlayer player);
}
