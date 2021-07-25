package me.bristermitten.warzone.database;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;
import io.vavr.concurrent.Future;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface Database {
    <T> Future<T> query(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer, CheckedFunction1<ResultSet, T> process);

    Future<Void> update(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer);

    Future<Void> execute(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer);

}
