package me.bristermitten.warzone.player.storage;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Database;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.WarzonePlayerBuilder;
import me.bristermitten.warzone.util.NoOp;

public record PlayerDatabaseHook(Database database) implements PlayerPersistence {
    @Inject
    public PlayerDatabaseHook {
    }

    @Override
    public Future<WarzonePlayer> load(@NotNull UUID id) {
        return database.query(
                """
                        SELECT
                            players.*,
                            (SELECT COUNT(*) FROM game_deaths d WHERE d.killer = players.uuid) AS kills,
                            (SELECT COUNT(*) FROM game_deaths d WHERE d.died = players.uuid) AS deaths,
                            (SELECT COUNT(*) FROM game_participants p WHERE p.player_id = players.uuid AND p.was_winner = 1) AS wins,
                            (SELECT COUNT(*) FROM game_participants p WHERE p.player_id = players.uuid AND p.was_winner = 0) AS losses
                        FROM
                            players WHERE players.uuid = ?
                        """,
                statement -> statement.setString(1, id.toString()),
                resultSet -> {
                    if (!resultSet.next()) {
                        return new WarzonePlayerBuilder().setPlayerId(id).createWarzonePlayer();
                    }
                    return loadPlayerFromResultSet(resultSet);
                }
        );
    }

    private WarzonePlayer loadPlayerFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString(1));
        int kills = resultSet.getInt(2);
        int deaths = resultSet.getInt(3);
        int level = resultSet.getInt(4);
        long xp = resultSet.getLong(5);
        int wins = resultSet.getInt(6);
        int losses = resultSet.getInt(7);
        return new WarzonePlayerBuilder()
                .setPlayerId(id)
                .setKills(kills)
                .setDeaths(deaths)
                .setLevel(level)
                .setWins(wins)
                .setLosses(losses)
                .setXp(xp)
                .createWarzonePlayer();
    }

    @Override
    public Future<Void> save(@NotNull WarzonePlayer player) {
        return flush(List.of(player));
    }

    @Override
    public Future<Void> flush(Iterable<WarzonePlayer> players) {
        return database.runTransactionally("""
                        insert or replace into players (uuid, level, xp) values (?, ?, ?)
                """, statement -> {
            int i = 0;
            for (WarzonePlayer player : players) {
                statement.setString(1, player.getPlayerId().toString());
                statement.setInt(2, player.getLevel());
                statement.setLong(3, player.getXp());
                statement.addBatch();
                i++;
                if (i % 50 == 0) {
                    statement.executeBatch();
                }
            }
            statement.executeBatch();
        });
    }

    @Override
    public Future<Seq<WarzonePlayer>> getAll() {
        return database.query("""
                        SELECT
                            players.*,
                            (SELECT COUNT(*) FROM game_deaths d WHERE d.killer = players.uuid) AS kills,
                            (SELECT COUNT(*) FROM game_deaths d WHERE d.died = players.uuid) AS deaths,
                            (SELECT COUNT(*) FROM game_participants p WHERE p.player_id = players.uuid AND p.was_winner = 1) AS wins,
                            (SELECT COUNT(*) FROM game_participants p WHERE p.player_id = players.uuid AND p.was_winner = 0) AS losses
                        FROM
                            players
                        """, NoOp.consumer(),
                resultSet -> {
                    var players = new LinkedList<WarzonePlayer>();
                    while (resultSet.next()) {
                        players.add(loadPlayerFromResultSet(resultSet));
                    }
                    return io.vavr.collection.List.ofAll(players);
                });
    }

    @Override
    public Future<Void> initialise() {
        return database.execute("""
                CREATE TABLE IF NOT EXISTS players
                (
                    uuid   TEXT(36) PRIMARY KEY,
                    level  INT,
                    xp     BIGINT
                );
                """.stripIndent());
    }

    @Override
    public @NotNull Future<Void> cleanup() {
        return Future.successful(null);
    }
}
