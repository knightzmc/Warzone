package me.bristermitten.warzone.game.statistic;

import com.google.common.base.Enums;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.database.Database;
import me.bristermitten.warzone.util.Functions;
import me.bristermitten.warzone.util.NoOp;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
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
                PRAGMA foreign_keys = ON;
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
                    shots_hit    INTEGER,
                    weapons_picked_up STRING,
                    medkits_used INTEGER,
                    times_reloaded INTEGER,
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
    public Future<Option<GameStatistic>> load(UUID gameId) {
        return database.query("""
                        SELECT uuid,
                               arena,
                               start_time,
                               end_time,
                               was_winner,
                               died,
                               killer,
                               cause,
                               time,
                               shots_fired,
                               shots_hit,
                               weapons_picked_up,
                               medkits_used,
                               times_reloaded
                        FROM games
                                 LEFT JOIN game_participants gp on games.uuid = gp.game
                                 LEFT JOIN game_deaths gd on games.uuid = gd.game and gd.killer = gp.player_id
                                 LEFT JOIN game_stats gs on games.uuid = gs.game and gs.player_id = gp.player_id
                        WHERE uuid = ?
                                        """.stripIndent(),
                statement -> statement.setString(1, gameId.toString()),
                this::loadGameStatistic);
    }

    private Option<GameStatistic> loadGameStatistic(ResultSet resultSet) throws SQLException {
        var all = loadGameStatistics(resultSet);
        if (all.size() > 1) {
            throw new SQLException("This shouldn't happen");
        }
        return all.headOption();
    }

    private Set<GameStatistic> loadGameStatistics(ResultSet resultSet) throws SQLException {
        var byUUID = new HashMap<UUID, GameStatistic.Builder>();
        while (resultSet.next()) {
            var uuid = UUID.fromString(resultSet.getString(1));
            var builder = byUUID.computeIfAbsent(uuid, Functions.constant(GameStatistic.Builder::new));
            builder.setArenaName(resultSet.getString(2));
            builder.setTimeStarted(resultSet.getTimestamp(3).toInstant());
            builder.setTimeFinished(resultSet.getTimestamp(4).toInstant());

            var wasWinner = resultSet.getBoolean(5);
            var killer = UUID.fromString(resultSet.getString(6));
            var died = UUID.fromString(resultSet.getString(7));
            var cause = resultSet.getString(8);
            var timeDied = resultSet.getTimestamp(9).toInstant();
            builder.getParticipants().add(killer);
            builder.getParticipants().add(died);
            if (wasWinner) {
                builder.getWinners().add(killer);
            }
            builder.getDeaths().add(
                    new PlayerDeath(
                            died,
                            killer,
                            timeDied,
                            Enums.getIfPresent(PlayerDeath.DeathCause.class, cause).or(PlayerDeath.DeathCause.UNKNOWN)
                    )
            );
            var shotsFired = resultSet.getInt(10);
            var shotsHit = resultSet.getInt(11);
            var weaponsPickedUp = Arrays.asList(resultSet.getString(12).split(","));
            var medkitsUsed = resultSet.getInt(13);
            var timesReloaded = resultSet.getInt(14);
            var playerStats = new PlayerStatistic(shotsFired, shotsHit, weaponsPickedUp, medkitsUsed, timesReloaded);
            builder.getPlayerStatistics().put(killer, playerStats);
        }
        return HashSet.ofAll(byUUID.values())
                .map(GameStatistic.Builder::build);
    }

    @Override
    public Future<Set<GameStatistic>> loadAll() {
        return database.query("""
                        SELECT uuid,
                               arena,
                               start_time,
                               end_time,
                               was_winner,
                               died,
                               killer,
                               cause,
                               time,
                               shots_fired,
                               shots_hit,
                               weapons_picked_up,
                               medkits_used,
                               times_reloaded
                        FROM games
                                 LEFT JOIN game_participants gp on games.uuid = gp.game
                                 LEFT JOIN game_deaths gd on games.uuid = gd.game and gd.killer = gp.player_id
                                 LEFT JOIN game_stats gs on games.uuid = gs.game and gs.player_id = gp.player_id
                                 """.stripIndent(),
                NoOp.consumer(),
                this::loadGameStatistics);
    }

    @Override
    public Future<Void> save(GameStatistic gameStatistic) {
        var gameQuery = database.update("""
                        INSERT OR REPLACE INTO games VALUES(?, ?, ?, ?);
                        """,
                statement -> {
                    statement.setString(1, gameStatistic.gameId().toString());
                    statement.setString(2, gameStatistic.arenaName());
                    statement.setTimestamp(3, Timestamp.from(gameStatistic.timeStarted()));
                    statement.setTimestamp(4, Timestamp.from(gameStatistic.timeFinished()));
                });
        var participantsQueries = List.ofAll(gameStatistic.participants())
                .map(uuid -> database.update("""
                        INSERT OR REPLACE INTO game_participants VALUES (?, ?, ?);
                        """.stripIndent(), statement -> {
                    statement.setString(1, gameStatistic.gameId().toString());
                    statement.setString(2, uuid.toString());
                    statement.setBoolean(3, gameStatistic.winners().contains(uuid));
                }));

        var deathsQueries = List.ofAll(gameStatistic.deaths())
                .map(death -> database.update("""
                        INSERT OR REPLACE INTO game_deaths VALUES (?, ?, ?, ?, ?);
                        """.stripIndent(), statement -> {
                    statement.setString(1, gameStatistic.gameId().toString());
                    statement.setString(2, death.died().toString());
                    statement.setString(3, Option.of(death.killer()).map(UUID::toString).getOrNull());
                    statement.setString(4, Option.of(death.cause()).map(Enum::toString).getOrNull());
                    statement.setTimestamp(5, Timestamp.from(death.timeOfDeath()));
                }));

        var statsQueries = io.vavr.collection.HashMap.ofAll(gameStatistic.playerStatistics())
                .map(t -> {
                    var uuid = t._1;
                    var stats = t._2;
                    return database.update("""
                            INSERT OR REPLACE INTO game_stats VALUES (?, ?, ?, ? ,?, ?, ?)
                            """, statement -> {
                        statement.setString(1, gameStatistic.gameId().toString());
                        statement.setString(2, uuid.toString());
                        statement.setInt(3, stats.shotsFired());
                        statement.setInt(4, stats.shotsHit());
                        statement.setString(5, String.join(",", stats.weaponsPickedUp()));
                        statement.setInt(6, stats.medkitsUsed());
                        statement.setInt(7, stats.timesReloaded());
                    });
                }).toList();

        return Future.sequence(
                statsQueries.prependAll(deathsQueries).prependAll(participantsQueries).prepend(gameQuery)
        ).map(Functions.constant((Void) null));
    }
}
