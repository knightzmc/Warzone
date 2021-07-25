package me.bristermitten.warzone.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ChatManager {

    String format(String message, @Nullable Player player);
}
