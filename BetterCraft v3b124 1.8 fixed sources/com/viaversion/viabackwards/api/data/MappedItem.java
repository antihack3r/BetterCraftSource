/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.data;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MappedItem {
    private final int id;
    private final String jsonName;
    private final Integer customModelData;

    public MappedItem(int id2, String name) {
        this(id2, name, null);
    }

    public MappedItem(int id2, String name, @Nullable Integer customModelData) {
        this.id = id2;
        this.jsonName = ChatRewriter.legacyTextToJsonString("\u00a7f" + name, true);
        this.customModelData = customModelData;
    }

    public int getId() {
        return this.id;
    }

    public String getJsonName() {
        return this.jsonName;
    }

    public @Nullable Integer customModelData() {
        return this.customModelData;
    }
}

