package me.bristermitten.warzone.database;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).to(SQLDatabase.class);
        bind(HikariConfig.class).toProvider(HikariConfigurationProvider.class);
    }
}
