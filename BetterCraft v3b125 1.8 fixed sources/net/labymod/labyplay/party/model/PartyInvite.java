/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party.model;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class PartyInvite {
    private String username;
    private UUID partyUUID;
    private String partyName;

    public String getUsername() {
        return this.username;
    }

    public UUID getPartyUUID() {
        return this.partyUUID;
    }

    public String getPartyName() {
        return this.partyName;
    }

    @ConstructorProperties(value={"username", "partyUUID", "partyName"})
    public PartyInvite(String username, UUID partyUUID, String partyName) {
        this.username = username;
        this.partyUUID = partyUUID;
        this.partyName = partyName;
    }
}

