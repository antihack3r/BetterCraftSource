/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.sticker.data;

import net.labymod.user.sticker.data.Sticker;

public class StickerPack {
    private short id;
    private String name;
    private Sticker[] stickers;

    public short getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Sticker[] getStickers() {
        return this.stickers;
    }
}

