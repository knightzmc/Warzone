package me.bristermitten.warzone.lang;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

public record LangConfig(
        @SerializedName("party") PartyLang partyLang
) {
    public static final Configuration<LangConfig> CONFIG = new Configuration<>(LangConfig.class, "lang.yml");

    public record PartyLang(
            @SerializedName("invite-sent") String inviteSent,
            @SerializedName("invite-already-sent") String inviteAlreadySent
    ) {
    }
}
