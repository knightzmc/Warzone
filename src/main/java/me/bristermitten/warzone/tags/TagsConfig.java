package me.bristermitten.warzone.tags;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

import java.util.List;

public record TagsConfig(@SerializedName("tag-rate") int tagRate, float chance, List<String> tags) {
    public static final Configuration<TagsConfig> CONFIG = new Configuration<>(TagsConfig.class, "tags.yml");
}
