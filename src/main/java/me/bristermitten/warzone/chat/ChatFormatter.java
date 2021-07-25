package me.bristermitten.warzone.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ChatFormatter {
    Component format(String message, @Nullable Player player);

    String preFormat(String message, @Nullable Player player);
}
