package me.bristermitten.warzone.state;

import org.jetbrains.annotations.NotNull;

public interface Stateful<T, S extends State<T>> {
    void setCurrentState(@NotNull S currentState);
}
