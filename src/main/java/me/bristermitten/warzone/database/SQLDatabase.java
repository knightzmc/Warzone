package me.bristermitten.warzone.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.CheckedConsumer;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLDatabase implements Database {
    private final @NotNull HikariDataSource dataSource;

    @Inject
    public SQLDatabase(@NotNull HikariConfig config) {
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public @NotNull Future<ResultSet> query(String query, CheckedConsumer<PreparedStatement> initializer) {
        return Future.of(() -> Try.withResources(dataSource::getConnection)
                .of(con -> con.prepareStatement(query))
                .andThenTry(initializer)
                .mapTry(PreparedStatement::executeQuery)
                .get());
    }

    @Override
    public @NotNull Future<Void> update(String query, CheckedConsumer<PreparedStatement> initializer) {
        return Future.run(() -> Try.withResources(dataSource::getConnection)
                .of(con -> con.prepareStatement(query))
                .andThenTry(initializer)
                .mapTry(PreparedStatement::executeUpdate)
                .get());
    }


}