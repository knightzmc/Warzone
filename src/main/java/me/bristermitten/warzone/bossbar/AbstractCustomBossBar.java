package me.bristermitten.warzone.bossbar;

public abstract class AbstractCustomBossBar implements CustomBossBar {
    private boolean paused = false;
    private BossBarConfig previous = null;

    protected abstract BossBarConfig getNewConfig();

    @Override
    public BossBarConfig getBossBar() {
        if (paused && previous != null) {
            return previous;
        }
        previous = getNewConfig();
        return previous;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
