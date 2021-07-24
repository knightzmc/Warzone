package me.bristermitten.warzone.player;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Database;
import me.bristermitten.warzone.util.NoOp;

import javax.inject.Inject;
import java.util.UUID;

public record PlayerDatabaseHook(Database database) implements PlayerPersistence {
    @Inject
    public PlayerDatabaseHook {
    }

    @Override
    public Future<WarzonePlayer> load(UUID id) {
        return database.query(
                "SELECT * FROM players WHERE uuid = ?",
                statement -> statement.setString(1, id.toString())
        ).mapTry(resultSet -> {
            if (!resultSet.next()) {
                return new WarzonePlayer(id);
            }
            int kills = resultSet.getInt(2);
            int deaths = resultSet.getInt(3);
            int level = resultSet.getInt(4);
            long xp = resultSet.getLong(5);
            return new WarzonePlayer(id, kills, deaths, level, xp);
        });
    }

    @Override
    public Future<Void> save(WarzonePlayer player) {
        return database.update(
                """
                            insert or replace into players (uuid, kills, deaths, level, xp) values (?, ?, ?, ?, ?)
                        """.stripIndent(),
                statement -> {
                    statement.setString(1, player.getPlayerId().toString());
                    statement.setInt(2, player.getKills());
                    statement.setInt(3, player.getDeaths());
                    statement.setInt(4, player.getLevel());
                    statement.setLong(5, player.getXp());
                }
        );
    }

    @Override
    public Future<Void> initialise() {
        return database.update("""
                CREATE TABLE IF NOT EXISTS players
                (
                    uuid   VARCHAR (36) UNIQUE PRIMARY KEY,
                    kills  INT,
                    deaths INT,
                    level  INT,
                    xp     BIGINT
                );
                """.stripIndent(), NoOp.consumer());
    }

    @Override
    public Future<Void> cleanup() {
        return Future.run(NoOp.runnable());
    }
}
