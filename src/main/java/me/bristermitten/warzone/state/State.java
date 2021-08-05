package me.bristermitten.warzone.state;

/**
 * Generic state machine interface
 *
 * @param <T> the type that holds this state
 */
public interface State<T> {
    /**
     * Called when a given T changes to this state
     *
     * @param t The value whose state changed
     */
    void onEnter(T t);

    /**
     * Called when a given T leaves this state, <b>BEFORE</b> the respective {@link State#onEnter(Object)} is called
     *
     * @param t The value whose state changed
     */
    void onLeave(T t);
}
