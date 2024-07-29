/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities;

public enum DiscordBuild {
    CANARY("//canary.discord.com/api"),
    PTB("//ptb.discord.com/api"),
    STABLE("//discord.com/api"),
    ANY;

    private final String endpoint;

    private DiscordBuild(String endpoint) {
        this.endpoint = endpoint;
    }

    private DiscordBuild() {
        this(null);
    }

    public static DiscordBuild from(int index) {
        for (DiscordBuild value : DiscordBuild.values()) {
            if (value.ordinal() != index) continue;
            return value;
        }
        return ANY;
    }

    public static DiscordBuild from(String endpoint) {
        for (DiscordBuild value : DiscordBuild.values()) {
            if (value.endpoint == null || !value.endpoint.equals(endpoint)) continue;
            return value;
        }
        return ANY;
    }
}

