package me.bristermitten.warzone.menu;

import org.bukkit.entity.Player;

public interface Menu {
    void open(Player player);
    void close(Player player);
}
