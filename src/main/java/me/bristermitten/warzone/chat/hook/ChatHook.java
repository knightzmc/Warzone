package me.bristermitten.warzone.chat.hook;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public interface ChatHook {
    String format(String message, @Nullable OfflinePlayer player);
}
