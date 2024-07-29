/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IntBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static IntBinaryTag intBinaryTag(int value) {
        return new IntBinaryTagImpl(value);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static IntBinaryTag of(int value) {
        return new IntBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<IntBinaryTag> type() {
        return BinaryTagTypes.INT;
    }

    public int value();
}

