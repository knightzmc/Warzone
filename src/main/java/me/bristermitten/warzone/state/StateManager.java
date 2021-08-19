package me.bristermitten.warzone.state;

import java.util.function.Function;

public interface StateManager<T extends Stateful<T, S>, S extends State<T>, A extends me.bristermitten.warzone.state.States<T, S>> {

    void setState(T t, Function<A, S> stateFunction);

    default void setState(T t, S state) {
        setState(t, unused -> state);
    }
}
