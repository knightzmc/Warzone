package me.bristermitten.warzone.chat.hook;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ChatHook {
    String format(String message, @Nullable Player player);
}
