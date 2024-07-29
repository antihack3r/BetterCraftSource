/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.item.BaseItemArrayType;
import io.netty.buffer.ByteBuf;

public class ItemShortArrayType1_13_2
extends BaseItemArrayType {
    @Override
    public Item[] read(ByteBuf buffer) throws Exception {
        int amount = Type.SHORT.readPrimitive(buffer);
        Item[] array = new Item[amount];
        for (int i2 = 0; i2 < amount; ++i2) {
            array[i2] = (Item)Type.ITEM1_13_2.read(buffer);
        }
        return array;
    }

    @Override
    public void write(ByteBuf buffer, Item[] object) throws Exception {
        Type.SHORT.writePrimitive(buffer, (short)object.length);
        for (Item o2 : object) {
            Type.ITEM1_13_2.write(buffer, o2);
        }
    }
}

