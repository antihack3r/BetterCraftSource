/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ShortBinaryTagImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface ShortBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static ShortBinaryTag shortBinaryTag(short value) {
        return new ShortBinaryTagImpl(value);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static ShortBinaryTag of(short value) {
        return new ShortBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<ShortBinaryTag> type() {
        return BinaryTagTypes.SHORT;
    }

    public short value();
}

