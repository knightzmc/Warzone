package me.bristermitten.warzone.database;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

public record DatabaseConfig(DatabaseType type,
                             String host,
                             String database,
                             String username,
                             String password,
                             int port) {

    public static final Configuration<DatabaseConfig> CONFIG = new Configuration<>(
            DatabaseConfig.class,
            "database.yml"
    );


    public enum DatabaseType {
        @SerializedName("sqlite") SQLITE {
            @Override
            public String createJdbcUrl(DatabaseConfig config) {
                return "jdbc:sqlite:plugins/Warzone/warzone.db";
            }
        },
        @SerializedName("mysql") MYSQL {
            @Override
            public String createJdbcUrl(DatabaseConfig config) {
                return "jdbc:mysql://%s:%d/%s".formatted(config.host(), config.port(), config.database());
            }
        };

        public abstract String createJdbcUrl(DatabaseConfig config);
    }
}
