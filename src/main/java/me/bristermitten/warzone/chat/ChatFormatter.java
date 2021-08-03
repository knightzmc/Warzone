package me.bristermitten.warzone.chat;

import me.bristermitten.warzone.chat.hook.ChatHook;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChatFormatter {
    Component format(String message, @Nullable OfflinePlayer player);

    String preFormat(String message, @Nullable OfflinePlayer player);

    @NotNull ChatFormatter withHooks(ChatHook... hooks);
}
