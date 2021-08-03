package me.bristermitten.warzone.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;
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
    @NotNull
    public <T> Future<T> query(String query, @NotNull CheckedConsumer<PreparedStatement> initializer, @NotNull CheckedFunction1<ResultSet, T> process) {
        return Future.of(() -> Try.withResources(dataSource::getConnection)
                .of(con -> Try.withResources(() -> con.prepareStatement(query))
                        .of(statement -> {
                            initializer.accept(statement);
                            var results = statement.executeQuery();
                            return process.apply(results);
                        })).get()
                .get());
    }

    @Override
    public @NotNull Future<Void> update(String query, @NotNull CheckedConsumer<PreparedStatement> initializer) {
        return Future.run(() ->
                Try.withResources(dataSource::getConnection)
                        .of(con -> Try.withResources(() -> con.prepareStatement(query))
                                .of(statement -> {
                                    initializer.accept(statement);
                                    statement.executeUpdate();
                                    return null;
                                })).get()
                        .get());
    }


    @Override
    public @NotNull Future<Void> execute(String query, @NotNull CheckedConsumer<PreparedStatement> initializer) {
        return Future.run(() ->
                Try.withResources(dataSource::getConnection)
                        .of(con -> Try.withResources(() -> con.prepareStatement(query))
                                .of(statement -> {
                                    initializer.accept(statement);
                                    statement.execute();
                                    return null;
                                })).get()
                        .get());
    }

}
