package me.bristermitten.warzone.config.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectTypeAdapter extends TypeAdapter<PotionEffectType> {
    @Override
    public void write(JsonWriter out, PotionEffectType value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public PotionEffectType read(JsonReader in) throws IOException {
        return PotionEffectType.getByName(in.nextString());
    }
}
