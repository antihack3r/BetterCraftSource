/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.sticker.data;

import java.beans.ConstructorProperties;

public class Sticker {
    private short id;
    private String name;
    private String[] tags;

    public short getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String[] getTags() {
        return this.tags;
    }

    @ConstructorProperties(value={"id", "name", "tags"})
    public Sticker(short id2, String name, String[] tags) {
        this.id = id2;
        this.name = name;
        this.tags = tags;
    }
}

