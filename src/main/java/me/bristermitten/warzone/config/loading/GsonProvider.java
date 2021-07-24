package me.bristermitten.warzone.config.loading;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Provider;

public class GsonProvider implements Provider<Gson> {
    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create();
    }
}
