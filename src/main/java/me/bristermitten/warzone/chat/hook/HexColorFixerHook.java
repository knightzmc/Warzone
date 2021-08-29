package me.bristermitten.warzone.chat.hook;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a truly outrageous hack
 * Essentially, when a papi expansion returns a hex color, it doesn't get parsed properly by MiniMessage
 * and instead it just takes it to the nearest legacy color
 * This class tries to parse the §x§a... hex format that {@link net.md_5.bungee.api.ChatColor} uses
 * and turns it into a format that minimessage can recognise
 */
public class HexColorFixerHook implements ChatHook {
    private static final Pattern HEX_PATTERN = Pattern.compile("§x((§[0-9a-f]){6})");

    @Override
    public String format(String message, @Nullable OfflinePlayer player) {
        final Matcher matcher = HEX_PATTERN.matcher(message);
        if (!matcher.find()) {
            return message;
        }
        return matcher.replaceAll(result -> {
            var stripped = result.group(1).replace("" + ChatColor.COLOR_CHAR, "");
            return "<#" + stripped + ">";
        });
    }
}
