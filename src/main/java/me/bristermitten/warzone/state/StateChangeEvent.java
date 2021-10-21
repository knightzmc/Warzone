package me.bristermitten.warzone.state;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class StateChangeEvent<S extends State<T>, T> extends Event implements Cancellable {
    private final S oldState;
    private final S newState;
    private final T subject;
    private boolean cancelled = false;

    protected StateChangeEvent(S oldState, S newState, T subject) {
        this.oldState = oldState;
        this.newState = newState;
        this.subject = subject;
    }

    public S getOldState() {
        return oldState;
    }

    public S getNewState() {
        return newState;
    }

    public T getSubject() {
        return subject;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
