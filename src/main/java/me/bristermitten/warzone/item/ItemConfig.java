package me.bristermitten.warzone.item;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ItemConfig(
        Material type,
        @Nullable Integer amount,
        @Nullable String name,
        @Nullable List<String> lore,
        @SerializedName("head-owner") @Nullable String headOwner,
        @Nullable PotionConfig potion
) {

    public record PotionConfig(
            PotionEffectType effect,
            int duration,
            int amplifier
    ) {
    }
}
