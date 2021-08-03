package me.bristermitten.warzone.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Sync {
    private Sync() {}
    public static void run(@NotNull Runnable r, @NotNull Plugin p) {
        Bukkit.getScheduler().runTask(p, r);
    }
}
