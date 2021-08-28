package me.bristermitten.warzone.hooks;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import xyz.oribuin.eternaltags.EternalAPI;
import xyz.oribuin.eternaltags.libs.command.util.HexUtils;
import xyz.oribuin.eternaltags.obj.Tag;

import java.util.Arrays;

public class TagFormatter {
    public String format(String tagPermission) {
        if (Bukkit.getPluginManager().isPluginEnabled("EternalTags")) {
            return EternalAPI.getInstance().getTagManager()
                    .getTags().stream().filter(tag -> tag.getPermission().equalsIgnoreCase(tagPermission))
                    .findFirst()
                    .map(Tag::getTag)
                    .map(HexUtils::colorify)
                    .orElse(tagPermission);
        }
        return Iterables.getLast(Arrays.asList(tagPermission.split("\\.")));
    }
}
