package me.bristermitten.warzone.game.statistic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public record PlayerDeath(
        @NotNull UUID died,
        @Nullable UUID killer,
        @NotNull Instant timeOfDeath,
        @Nullable DeathCause cause
) {
    public enum DeathCause {
        BORDER,
        FALL_DAMAGE,
        OTHER,
        /**
         * Used in the case when an invalid value is used in the database
         * or the death cause could not be determined
         */
        UNKNOWN
    }
}
