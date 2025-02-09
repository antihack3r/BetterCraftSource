/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item;

import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class ItemArrayType
extends Type<Item[]> {
    private final boolean compressed;

    public ItemArrayType(boolean compressed) {
        super(Item[].class);
        this.compressed = compressed;
    }

    @Override
    public Item[] read(ByteBuf buffer) throws Exception {
        int amount = Type.SHORT.readPrimitive(buffer);
        Item[] items = new Item[amount];
        for (int i2 = 0; i2 < amount; ++i2) {
            items[i2] = (Item)(this.compressed ? Types1_7_6_10.COMPRESSED_NBT_ITEM : Types1_7_6_10.ITEM).read(buffer);
        }
        return items;
    }

    @Override
    public void write(ByteBuf buffer, Item[] items) throws Exception {
        Type.SHORT.writePrimitive(buffer, (short)items.length);
        for (Item item : items) {
            (this.compressed ? Types1_7_6_10.COMPRESSED_NBT_ITEM : Types1_7_6_10.ITEM).write(buffer, item);
        }
    }
}

