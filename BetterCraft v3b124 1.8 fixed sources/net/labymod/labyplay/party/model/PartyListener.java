/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party.model;

import java.util.UUID;
import net.labymod.labyplay.party.model.PartyActionTypes;
import net.labymod.labyplay.party.model.PartyMember;

public interface PartyListener {
    public void onInvitedPlayer(String var1, UUID var2, String var3);

    public void onInviteSuccess(String var1, UUID var2);

    public void onChatMessage(String var1, String var2);

    public void onSystemMessage(PartyActionTypes.Message var1, String[] var2);

    public void onPartyLeft(UUID var1);

    public void onMemberList(UUID var1, PartyMember[] var2);
}

