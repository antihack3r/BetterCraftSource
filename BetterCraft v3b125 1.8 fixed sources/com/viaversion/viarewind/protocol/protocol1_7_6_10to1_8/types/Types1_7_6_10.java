/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types;

import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.CompressedNBTType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.ItemArrayType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.ItemType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.NBTType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.metadata.MetadataListType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.metadata.MetadataType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.primitive.ByteIntArrayType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.primitive.PositionUYType;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.List;

public class Types1_7_6_10 {
    public static final Type<int[]> BYTE_INT_ARRAY = new ByteIntArrayType();
    public static final Type<Position> SHORT_POSITION = new PositionUYType<Short>(Type.SHORT, value -> (short)value);
    public static final Type<Position> INT_POSITION = new PositionUYType<Integer>(Type.INT, value -> value);
    public static final Type<Position> BYTE_POSITION = new PositionUYType<Byte>(Type.BYTE, value -> (byte)value);
    public static final Type<Position> U_BYTE_POSITION = new PositionUYType<Short>(Type.UNSIGNED_BYTE, value -> (short)value);
    public static final Type<CompoundTag> NBT = new NBTType();
    public static final Type<Item> ITEM = new ItemType(false);
    public static final Type<CompoundTag> COMPRESSED_NBT = new CompressedNBTType();
    public static final Type<Item> COMPRESSED_NBT_ITEM = new ItemType(true);
    public static final Type<Item[]> COMPRESSED_NBT_ITEM_ARRAY = new ItemArrayType(true);
    public static final Type<Metadata> METADATA = new MetadataType();
    public static final Type<List<Metadata>> METADATA_LIST = new MetadataListType();
}

