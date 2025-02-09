/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.chat;

public enum GameMode {
    SURVIVAL(0, "Survival Mode"),
    CREATIVE(1, "Creative Mode"),
    ADVENTURE(2, "Adventure Mode"),
    SPECTATOR(3, "Spectator Mode");

    private final int id;
    private final String text;

    private GameMode(int id2, String text) {
        this.id = id2;
        this.text = text;
    }

    public int getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public static GameMode getById(int id2) {
        for (GameMode gm2 : GameMode.values()) {
            if (gm2.getId() != id2) continue;
            return gm2;
        }
        return null;
    }
}

