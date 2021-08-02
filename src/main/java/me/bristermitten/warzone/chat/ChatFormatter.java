package me.bristermitten.warzone.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public interface ChatFormatter {
    Component format(String message, @Nullable OfflinePlayer player);

    String preFormat(String message, @Nullable OfflinePlayer player);
}
