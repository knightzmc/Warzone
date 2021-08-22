package me.bristermitten.warzone.game.statistic;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Database;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

public class GameStatsDatabaseHook implements GamePersistence {
    private final Database database;

    @Inject
    public GameStatsDatabaseHook(Database database) {
        this.database = database;
    }

    @Override
    public Future<Void> initialise() {
        return database.execute("""
                CREATE TABLE IF NOT EXISTS arenas
                (
                    arena_name VARCHAR PRIMARY KEY DEFAULT '[deleted'
                );

                CREATE TABLE IF NOT EXISTS games
                (
                    uuid       TEXT(36) PRIMARY KEY,
                    arena      VARCHAR,
                    start_time datetime,
                    end_time   datetime,
                    FOREIGN KEY (arena) REFERENCES arenas (arena_name) ON DELETE SET DEFAULT ON UPDATE CASCADE
                );

                CREATE TABLE IF NOT EXISTS game_participants
                (
                    game       TEXT(36),
                    player_id  TEXT(36),
                    was_winner BOOLEAN,
                    PRIMARY KEY (game, player_id),
                    FOREIGN KEY (game) REFERENCES games (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY (player_id) REFERENCES players (uuid)
                );

                CREATE TABLE IF NOT EXISTS game_deaths
                (
                    game   TEXT(36) PRIMARY KEY,
                    died   TEXT(36) NOT NULL,
                    killer TEXT(36),
                    cause  STRING,
                    time   DATETIME,
                    FOREIGN KEY (game) REFERENCES games (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY (died) REFERENCES players (uuid),
                    FOREIGN KEY (killer) REFERENCES players (uuid)
                );

                CREATE TABLE IF NOT EXISTS game_stats
                (
                    game        TEXT(36),
                    player_id   TEXT(36),
                    shots_fired INTEGER,
                    accuracy    DECIMAL,
                    PRIMARY KEY (game, player_id),
                    FOREIGN KEY (game) REFERENCES games (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY (player_id) REFERENCES players (uuid)
                );
                """);
    }

    @Override
    public Future<Void> cleanup() {
        return Future.successful(null);
    }

    @Override
    public Future<GameStatistic> load(UUID gameId) {
        return database.query("""
                        SELECT uuid,
                               arena,
                               start_time,
                               end_time,
                               died,
                               killer,
                               cause,
                               time,
                               shots_fired,
                               accuracy
                        FROM games
                                 LEFT JOIN game_participants gp on games.uuid = gp.game
                                 LEFT JOIN game_deaths gd on games.uuid = gd.game and gd.killer = gp.player_id
                                 LEFT JOIN game_stats gs on games.uuid = gs.game and gs.player_id = gp.player_id
                        WHERE uuid = ?
                                        """.stripIndent(),
                statement -> statement.setString(1, gameId.toString()),
                resultSet -> {
                    while (resultSet.next()) {
// https://paste.developerden.net/sutibyxobi.sql
                    }
                    return null; //TODO
                });
    }

    @Override
    public Future<Collection<GameStatistic>> loadAll() {
        return null;
    }

    @Override
    public Future<Void> save(GameStatistic gameStatistic) {
        return null;
    }
}
