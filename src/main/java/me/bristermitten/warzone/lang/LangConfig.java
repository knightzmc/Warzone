package me.bristermitten.warzone.lang;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import org.jetbrains.annotations.NotNull;

public record LangConfig(
        @SerializedName("party") PartyLang partyLang,
        @SerializedName("game") GameLang gameLang,
        @SerializedName("admin") AdminLang adminLang,
        @SerializedName("errors") ErrorLang errorLang,
        @SerializedName("tags") TagsLang tagsLang
) {
    public static final Configuration<LangConfig> CONFIG = new Configuration<>(LangConfig.class, "lang.yml");

    public record TitleConfig(@NotNull LangElement title, @NotNull LangElement subtitle) {
    }

    public record ErrorLang(
            @SerializedName("xp-not-positive") String xpNotPositive
            , @SerializedName("arena-in-use") String arenaInUse
            , @SerializedName("unknown-arena") String unknownArena
            , @SerializedName("no-game-running") String noGameRunning
            , @SerializedName("unknown-player") String unknownPlayer
    ) {
    }

    public record TagsLang (
            @SerializedName("tag-gained") LangElement tagGained
            , @SerializedName("no-more-tags") LangElement noMoreTags
    ){}

    public record GameLang(
            @SerializedName("player-out") LangElement playerOut
            , @SerializedName("not-in-game") LangElement notInGame
            , @SerializedName("already-in-game") LangElement alreadyInGame
            , @SerializedName("winner") LangElement winner
            , @SerializedName("winner-broadcast") LangElement winnerBroadcast
            , @SerializedName("no-time-remaining") String noTimeRemaining
    ) {
    }

    public record AdminLang(
            @SerializedName("stats-reset") LangElement statsReset
            , @SerializedName("xp-set") LangElement xpSet
            , @SerializedName("xp-get") LangElement xpGet
    ) {
    }

    public record PartyLang(
            @SerializedName("invite-received") LangElement inviteReceived
            , @SerializedName("invite-already-sent") LangElement inviteAlreadySent
            , @SerializedName("invite-sent") LangElement inviteSent
            , @SerializedName("no-invites") LangElement noInvites
            , @SerializedName("no-invites-from-player") LangElement noInvitesFromPlayer
            , @SerializedName("invalid-invite") LangElement invalidInvite
            , @SerializedName("multiple-invites") LangElement multipleInvites
            , @SerializedName("no-party") LangElement noParty
            , @SerializedName("party-promoted-left") LangElement partyPromotedLeft
            , @SerializedName("party-user-left") LangElement partyUserLeft
            , @SerializedName("party-you-left") LangElement partyYouLeft
            , @SerializedName("party-joined") LangElement partyJoined
            , @SerializedName("party-joined-broadcast") LangElement partyJoinedBroadcast
            , @SerializedName("cant-invite-self") LangElement cannotInviteSelf
            , @SerializedName("already-in-party") LangElement alreadyInParty
            , @SerializedName("party-full") LangElement partyFull
            , @SerializedName("party-full-join") LangElement partyFullJoin
            , @SerializedName("party-is-in-game") LangElement partyIsInGame
    ) {
    }
}
