/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.FloatBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface FloatBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static FloatBinaryTag floatBinaryTag(float value) {
        return new FloatBinaryTagImpl(value);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static FloatBinaryTag of(float value) {
        return new FloatBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<FloatBinaryTag> type() {
        return BinaryTagTypes.FLOAT;
    }

    public float value();
}

