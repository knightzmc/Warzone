package me.bristermitten.warzone.lang;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

public record LangConfig(
        @SerializedName("party") PartyLang partyLang
) {
    public static final Configuration<LangConfig> CONFIG = new Configuration<>(LangConfig.class, "lang.yml");

    public record PartyLang(
            @SerializedName("invite-received") String inviteReceived
            , @SerializedName("invite-already-sent") String inviteAlreadySent
            , @SerializedName("no-invites") String noInvites
            , @SerializedName("no-invites-from-player") String noInvitesFromPlayer
            , @SerializedName("invalid-invite") String invalidInvite
            , @SerializedName("multiple-invites") String multipleInvites
            , @SerializedName("no-party") String noParty
            , @SerializedName("party-promoted-left") String partyPromotedLeft
            , @SerializedName("party-user-left") String partyUserLeft
            , @SerializedName("party-you-left") String partyYouLeft
    ) {
    }
}