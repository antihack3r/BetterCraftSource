/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.api.rewriter.item;

import com.viaversion.viarewind.api.minecraft.IdDataCombine;
import com.viaversion.viarewind.api.rewriter.item.Replacement;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;

public abstract class ReplacementItemRewriter<T extends AbstractProtocol<?, ?, ?, ?>>
implements ItemRewriter<T> {
    private final Int2ObjectMap<Replacement> ITEM_REPLACEMENTS = new Int2ObjectOpenHashMap<Replacement>();
    private final Int2ObjectMap<Replacement> BLOCK_REPLACEMENTS = new Int2ObjectOpenHashMap<Replacement>();
    private final T protocol;
    private final ProtocolVersion protocolVersion;

    public ReplacementItemRewriter(T protocol, ProtocolVersion protocolVersion) {
        this.protocol = protocol;
        this.protocolVersion = protocolVersion;
    }

    public void registerItem(int id2, Replacement replacement) {
        this.registerItem(id2, -1, replacement);
    }

    public void registerBlock(int id2, Replacement replacement) {
        this.registerBlock(id2, -1, replacement);
    }

    public void registerItemBlock(int id2, Replacement replacement) {
        this.registerItemBlock(id2, -1, replacement);
    }

    public void registerItem(int id2, int data, Replacement replacement) {
        this.ITEM_REPLACEMENTS.put(this.generateTrackingId(id2, data), replacement);
        replacement.buildNames(this.protocolVersion.getName());
    }

    public void registerBlock(int id2, int data, Replacement replacement) {
        this.BLOCK_REPLACEMENTS.put(this.generateTrackingId(id2, data), replacement);
        replacement.buildNames(this.protocolVersion.getName());
    }

    public void registerItemBlock(int id2, int data, Replacement replacement) {
        this.registerItem(id2, data, replacement);
        this.registerBlock(id2, data, replacement);
    }

    public Item replace(Item item) {
        Replacement replacement = (Replacement)this.ITEM_REPLACEMENTS.get(this.generateTrackingId(item.identifier(), item.data()));
        if (replacement == null) {
            replacement = (Replacement)this.ITEM_REPLACEMENTS.get(this.generateTrackingId(item.identifier(), -1));
        }
        return replacement == null ? item : replacement.replace(item);
    }

    public Replacement replace(int id2, int data) {
        Replacement replacement = (Replacement)this.BLOCK_REPLACEMENTS.get(this.generateTrackingId(id2, data));
        if (replacement == null) {
            replacement = (Replacement)this.BLOCK_REPLACEMENTS.get(this.generateTrackingId(id2, -1));
        }
        return replacement;
    }

    public int replace(int combined) {
        int data = IdDataCombine.dataFromCombined(combined);
        Replacement replace = this.replace(IdDataCombine.idFromCombined(combined), data);
        return replace != null ? IdDataCombine.toCombined(replace.getId(), replace.replaceData(data)) : combined;
    }

    private int generateTrackingId(int id2, int data) {
        return id2 << 16 | data & 0xFFFF;
    }

    @Override
    public T protocol() {
        return this.protocol;
    }
}

