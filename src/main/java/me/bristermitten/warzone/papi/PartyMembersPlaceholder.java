package me.bristermitten.warzone.papi;

import me.bristermitten.warzone.party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PartyMembersPlaceholder implements WarzonePlaceholder {
    public static final Pattern PARTY_MEMBERS_PATTERN = Pattern.compile("party_members(:(?<prefix>[^:]+))?(:(?<suffix>[^:]+))?");
    private final PartyManager partyManager;

    @Inject
    public PartyMembersPlaceholder(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public Pattern getPattern() {
        return PARTY_MEMBERS_PATTERN;
    }

    @Override
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }
        var matcher = PARTY_MEMBERS_PATTERN.matcher(params);
        if (!matcher.find()) {
            return null;
        }
        var prefix = matcher.group("prefix");
        var suffix = matcher.group("suffix");

        return partyManager.getParty(player)
                .getAllMembers()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(HumanEntity::getName)
                .map(s -> prefix + s + suffix)
                .collect(Collectors.joining("\n"));
    }
}
