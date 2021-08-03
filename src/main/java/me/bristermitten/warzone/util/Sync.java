package me.bristermitten.warzone.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Sync {
    private Sync() {}
    public static void run(Runnable r, Plugin p) {
        Bukkit.getScheduler().runTask(p, r);
    }
}
