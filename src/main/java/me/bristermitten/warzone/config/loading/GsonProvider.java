package me.bristermitten.warzone.config.loading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Provider;

public class GsonProvider implements Provider<Gson> {
    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                .create();
    }
}
