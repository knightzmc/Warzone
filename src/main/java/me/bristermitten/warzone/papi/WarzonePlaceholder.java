package me.bristermitten.warzone.papi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public interface WarzonePlaceholder {
    Pattern getPattern();

    String onPlaceholderRequest(@Nullable Player player, @NotNull String params);
}
