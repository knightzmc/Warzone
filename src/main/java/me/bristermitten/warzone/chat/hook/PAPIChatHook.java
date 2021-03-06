package me.bristermitten.warzone.chat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIChatHook implements ChatHook {
    @Override
    public @NotNull String format(@NotNull String message, @Nullable OfflinePlayer player) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
