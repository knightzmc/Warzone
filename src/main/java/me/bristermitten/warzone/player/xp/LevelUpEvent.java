package me.bristermitten.warzone.player.xp;

import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LevelUpEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final WarzonePlayer levelling;
    private long newXP;
    private int newLevel;
    private boolean isCancelled = false;

    public LevelUpEvent(WarzonePlayer levelling, long newXP, int newLevel) {
        this.levelling = levelling;
        this.newXP = newXP;
        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public WarzonePlayer getLevelling() {
        return levelling;
    }

    public long getNewXP() {
        return newXP;
    }

    public void setNewXP(long newXP) {
        this.newXP = newXP;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
