package me.bristermitten.warzone.listener;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

public class ListenerBinding {
    private ListenerBinding() {

    }

    public static LinkedBindingBuilder<EventListener> bindListener(Binder binder) {
        return Multibinder.newSetBinder(binder, EventListener.class).addBinding();
    }
}
