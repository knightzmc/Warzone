package me.bristermitten.warzone.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.CheckedConsumer;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLDatabase implements Database {
    private final HikariDataSource dataSource;

    @Inject
    public SQLDatabase(HikariConfig config) {
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public Future<ResultSet> query(String query, CheckedConsumer<PreparedStatement> initializer) {
        return Future.of(() -> Try.withResources(dataSource::getConnection)
                .of(con -> con.prepareStatement(query))
                .andThenTry(initializer)
                .mapTry(PreparedStatement::executeQuery)
                .get());
    }

    @Override
    public Future<Void> update(String query, CheckedConsumer<PreparedStatement> initializer) {
        return Future.run(() -> Try.withResources(dataSource::getConnection)
                .of(con -> con.prepareStatement(query))
                .andThenTry(initializer)
                .mapTry(PreparedStatement::executeUpdate)
                .get());
    }


}
