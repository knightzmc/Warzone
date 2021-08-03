package me.bristermitten.warzone.database;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;
import io.vavr.concurrent.Future;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface Database {
    <T> @NotNull Future<T> query(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer, CheckedFunction1<ResultSet, T> process);

    @NotNull Future<Void> update(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer);

    @NotNull Future<Void> execute(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer);

}
