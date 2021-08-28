package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import io.vavr.control.Option;
import io.vavr.control.Try;
import me.bristermitten.warzone.commands.CommandErrors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class OfflinePlayerProcessor implements ArgumentProcessor<OfflinePlayer> {
    private final CommandErrors commandErrors;

    @Inject
    public OfflinePlayerProcessor(CommandErrors commandErrors) {
        this.commandErrors = commandErrors;
    }

    @Override
    public Class<OfflinePlayer> getType() {
        return OfflinePlayer.class;
    }

    @Override
    public OfflinePlayer getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        var arg = context.popFirstArg();
        if (arg.contains("-")) {
            // try interpret it as a uuid
            return Try.of(() -> UUID.fromString(arg))
                    .map(Bukkit::getOfflinePlayer)
                    .getOrElse(() -> commandErrors.commandError(context, langConfig -> langConfig.errorLang().unknownPlayer(),
                            Map.of("{value}", arg)));
        }
        //noinspection deprecation SHUT UP
        return Option.of(Bukkit.getOfflinePlayer(arg))
                .getOrElse(() -> commandErrors.commandError(context, langConfig -> langConfig.errorLang().unknownPlayer(),
                        Map.of("{value}", arg)));
    }

    @Override
    public String getId() {
        return "offlinePlayers";
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
    }
}
