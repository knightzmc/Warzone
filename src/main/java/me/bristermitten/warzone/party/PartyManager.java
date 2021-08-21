package me.bristermitten.warzone.party;

import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.lang.LangService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class PartyManager {
    public static final String PLAYER_PLACEHOLDER = "{player}";
    private final Map<UUID, Party> partiesByMember = new HashMap<>();
    private final LangService langService;

    private final GameManager gameManager;

    @Inject
    public PartyManager(LangService langService, GameManager gameManager) {
        this.langService = langService;
        this.gameManager = gameManager;
    }

    @NotNull
    public Party getParty(@NotNull Player partyOwner) {
        return getParty(partyOwner.getUniqueId());
    }

    @NotNull
    public Party getParty(@NotNull UUID partyOwner) {
        return partiesByMember.computeIfAbsent(partyOwner,
                uid -> new Party(uid, new HashSet<>()));
    }

    public void invite(@NotNull Player inviter, @NotNull Player receiver) {
        if (inviter.getUniqueId().equals(receiver.getUniqueId())) {
            langService.sendMessage(inviter, langConfig -> langConfig.partyLang().cannotInviteSelf());
            return;
        }
        var party = getParty(inviter);
        if (party.isFull()) {
            langService.sendMessage(inviter, config -> config.partyLang().partyFull());
            return;
        }
        if (party.getOwner().equals(receiver.getUniqueId()) || party.getOtherPlayers().contains(receiver.getUniqueId())) {
            langService.sendMessage(inviter, config -> config.partyLang().alreadyInParty(),
                    Map.of(PLAYER_PLACEHOLDER, receiver.getName()));
            return;
        }
        if (party.getOutgoingInvites().stream().anyMatch(i -> i.receiver().equals(receiver.getUniqueId()))) {
            langService.sendMessage(
                    inviter, config -> config.partyLang().inviteAlreadySent(),
                    Map.of(PLAYER_PLACEHOLDER, receiver.getName())
            );
            return;
        }

        langService.sendMessage(
                receiver, config -> config.partyLang().inviteReceived(),
                Map.of("{inviter}", inviter.getName())
        );

        langService.sendMessage(inviter, config -> config.partyLang().inviteSent(),
                Map.of(PLAYER_PLACEHOLDER, receiver.getName()));

        party.getOutgoingInvites().add(new PartyInvite(
                party,
                inviter.getUniqueId(), receiver.getUniqueId()
        ));

    }

    public @Unmodifiable Collection<PartyInvite> getInvitesFor(@NotNull Player receiver) {
        return partiesByMember.values().stream()
                .flatMap(party -> party.getOutgoingInvites().stream())
                .filter(partyInvite -> partyInvite.receiver().equals(receiver.getUniqueId()))
                .toList();
    }

    public void accept(@NotNull PartyInvite invite) {
        var receivingPlayer = Bukkit.getPlayer(invite.receiver());
        if (receivingPlayer == null) {
            return;
        }
        if (!invite.invitingTo().getOutgoingInvites().contains(invite)) {
            langService.sendMessage(receivingPlayer, config -> config.partyLang().invalidInvite());
            return;
        }
        if (!gameManager.getGameContaining(invite.invitingTo()).isEmpty()) {
            langService.sendMessage(receivingPlayer, config -> config.partyLang().partyIsInGame());
            return;
        }

        add(invite.invitingTo(), receivingPlayer);
        invite.invitingTo().getOutgoingInvites().remove(invite);
    }

    private void add(@NotNull Party party, @NotNull Player joining) {
        if (party.isFull()) {
            langService.sendMessage(joining,
                    langConfig -> langConfig.partyLang().partyFullJoin(),
                    Map.of(PLAYER_PLACEHOLDER, Bukkit.getPlayer(party.getOwner()).getName()));
            return;
        }
        party.getAllMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> langService.sendMessage(player, config -> config.partyLang().partyJoinedBroadcast(),
                        Map.of(PLAYER_PLACEHOLDER, joining.getName())));

        party.add(joining.getUniqueId());
        partiesByMember.put(joining.getUniqueId(), party);


        langService.sendMessage(joining,
                config -> config.partyLang().partyJoined(),
                Map.of("{owner}", Bukkit.getPlayer(party.getOwner()).getName()));
    }

    public void leave(@NotNull Party party, @NotNull Player leaver) {

        if (party.isEmpty()) { // This is a bit of a lie, everyone is in a party, but if a party is just you, can it really be considered a party?
            langService.sendMessage(leaver, langConfig -> langConfig.partyLang().noParty());
            return;
        }

        langService.sendMessage(leaver, config -> config.partyLang().partyYouLeft());

        party.getOtherPlayers().remove(leaver.getUniqueId());
        partiesByMember.remove(leaver.getUniqueId());

        party.getOtherPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player ->
                        langService.sendMessage(player, config -> config.partyLang().partyUserLeft(),
                                Map.of("{leaver}", leaver.getName())));

        if (leaver.getUniqueId().equals(party.getOwner())) {
            // destroy one and another will take its place
            var nextOwnerOptional = party.getOtherPlayers()
                    .stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .findFirst();

            if (nextOwnerOptional.isEmpty()) {
                throw new IllegalStateException("what?");
            }

            var nextOwner = nextOwnerOptional.get();
            party.setOwner(nextOwner.getUniqueId());

            party.getOtherPlayers().remove(nextOwner.getUniqueId());
            langService.sendMessage(nextOwner, config -> config.partyLang().partyPromotedLeft(),
                    Map.of("{owner}", leaver.getName()));
        }

    }
}
