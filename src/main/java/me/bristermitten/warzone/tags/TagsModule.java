package me.bristermitten.warzone.tags;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.listener.ListenerBinding;

public class TagsModule extends AbstractModule {
    @Override
    protected void configure() {
        ListenerBinding.bindListener(binder()).to(TagsListener.class);
    }
}
