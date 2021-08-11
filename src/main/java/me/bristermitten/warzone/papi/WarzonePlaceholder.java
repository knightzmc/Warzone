package me.bristermitten.warzone.papi;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public interface WarzonePlaceholder {
    @Nullable
    default Pattern getPattern() {
        return null;
    }

    @Nullable String onPlaceholderRequest(@Nullable OfflinePlayer player, @NotNull String params);
}
