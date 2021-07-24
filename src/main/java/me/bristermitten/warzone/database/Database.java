package me.bristermitten.warzone.database;

import io.vavr.CheckedConsumer;
import io.vavr.concurrent.Future;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

public interface Database {
    Future<ResultSet> query(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer);

    Future<Void> update(@Language("SQL") String query, CheckedConsumer<PreparedStatement> initializer);

}
