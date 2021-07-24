package me.bristermitten.warzone.database;

import io.vavr.concurrent.Future;

public interface Persistence {
    Future<Void> initialise();

    Future<Void> cleanup();
}
