/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon;

import java.util.UUID;

public class About {
    public boolean deleted;
    public boolean loaded;
    public UUID uuid;
    public String name;

    public About(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}

