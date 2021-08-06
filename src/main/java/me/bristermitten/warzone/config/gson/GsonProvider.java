package me.bristermitten.warzone.config.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class GsonProvider implements Provider<Gson> {
    private final PotionEffectTypeAdapter typeAdapter;

    @Inject
    public GsonProvider(PotionEffectTypeAdapter typeAdapter) {
        this.typeAdapter = typeAdapter;
    }

    @Override
    public @NotNull Gson get() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                .registerTypeAdapter(PotionEffectType.class, typeAdapter)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create();
    }
}
