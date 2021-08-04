package me.bristermitten.warzone.player.storage;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Database;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.WarzonePlayerBuilder;
import me.bristermitten.warzone.util.NoOp;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public record PlayerDatabaseHook(Database database) implements PlayerPersistence {
    @Inject
    public PlayerDatabaseHook {
    }

    @Override
    public Future<WarzonePlayer> load(@NotNull UUID id) {
        return database.query(
                "SELECT * FROM players WHERE uuid = ?",
                statement -> statement.setString(1, id.toString()),
                resultSet -> {
                    if (!resultSet.next()) {
                        return new WarzonePlayerBuilder().setPlayerId(id).createWarzonePlayer();
                    }
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
        );
    }

    @Override
    public Future<Void> save(@NotNull WarzonePlayer player) {
        return flush(List.of(player));
    }

    @Override
    public Future<Void> flush(Iterable<WarzonePlayer> players) {
        return database.runTransactionally("""
                        insert or replace into players (uuid, kills, deaths, level, xp, wins, losses) values (?, ?, ?, ?, ?, ?, ?)
                """, statement -> {
            int i = 0;
            for (WarzonePlayer player : players) {
                statement.setString(1, player.getPlayerId().toString());
                statement.setInt(2, player.getKills());
                statement.setInt(3, player.getDeaths());
                statement.setInt(4, player.getLevel());
                statement.setLong(5, player.getXp());
                statement.setLong(6, player.getWins());
                statement.setLong(7, player.getLosses());
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
    public Future<Void> initialise() {
        return database.execute("""
                CREATE TABLE IF NOT EXISTS players
                (
                    uuid   VARCHAR (36) UNIQUE PRIMARY KEY,
                    kills  INT,
                    deaths INT,
                    level  INT,
                    xp     BIGINT,
                    wins INT,
                    losses INT
                );
                """.stripIndent(), NoOp.consumer());
    }

    @Override
    public @NotNull Future<Void> cleanup() {
        return Future.run(NoOp.runnable());
    }
}
