package me.bristermitten.warzone.chat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PAPIChatHook implements ChatHook {
    @Override
    public String format(String message, @Nullable OfflinePlayer player) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
