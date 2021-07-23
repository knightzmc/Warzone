package me.bristermitten.warzone.aspect;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A group of functionality that provides a Guice module
 * It may need to be supplied with some data before generating the module
 * and should be provided with an injector after module generation to finalize any injections
 * that might depend on the wider scope of the Injector.
 */
public interface Aspect {
    Module generateModule() throws IllegalStateException;

    default void finalizeInjections(Injector injector) {
    }
}
