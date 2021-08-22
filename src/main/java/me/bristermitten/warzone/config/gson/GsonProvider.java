package me.bristermitten.warzone.config.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.bristermitten.warzone.lang.LangElement;
import me.bristermitten.warzone.lang.LangElementTypeAdapter;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class GsonProvider implements Provider<Gson> {
    private final PotionEffectTypeAdapter typeAdapter;
    private final LangElementTypeAdapter langElementTypeAdapter;

    @Inject
    public GsonProvider(PotionEffectTypeAdapter typeAdapter, LangElementTypeAdapter langElementTypeAdapter) {
        this.typeAdapter = typeAdapter;
        this.langElementTypeAdapter = langElementTypeAdapter;
    }

    @Override
    public @NotNull Gson get() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                .registerTypeAdapter(PotionEffectType.class, typeAdapter)
                .registerTypeAdapter(LangElement.class, langElementTypeAdapter)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create();
    }
}
