package me.bristermitten.warzone.party;

import java.util.UUID;

public record PartyInvite(Party invitingTo, UUID sender, UUID receiver) {
}
