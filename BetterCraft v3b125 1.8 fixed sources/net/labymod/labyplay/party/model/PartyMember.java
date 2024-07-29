/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party.model;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class PartyMember {
    private String name;
    private UUID uuid;
    private boolean owner;
    private boolean member;
    private long timestamp;

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isOwner() {
        return this.owner;
    }

    public boolean isMember() {
        return this.member;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @ConstructorProperties(value={"name", "uuid", "owner", "member", "timestamp"})
    public PartyMember(String name, UUID uuid, boolean owner, boolean member, long timestamp) {
        this.name = name;
        this.uuid = uuid;
        this.owner = owner;
        this.member = member;
        this.timestamp = timestamp;
    }
}

