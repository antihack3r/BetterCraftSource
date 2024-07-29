/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import com.viaversion.viaversion.libs.kyori.adventure.internal.Internals;
import com.viaversion.viaversion.libs.kyori.adventure.util.HSVLike;
import com.viaversion.viaversion.libs.kyori.adventure.util.ShadyPines;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

final class HSVLikeImpl
implements HSVLike {
    private final float h;
    private final float s;
    private final float v;

    HSVLikeImpl(float h2, float s2, float v2) {
        HSVLikeImpl.requireInsideRange(h2, "h");
        HSVLikeImpl.requireInsideRange(s2, "s");
        HSVLikeImpl.requireInsideRange(v2, "v");
        this.h = h2;
        this.s = s2;
        this.v = v2;
    }

    @Override
    public float h() {
        return this.h;
    }

    @Override
    public float s() {
        return this.s;
    }

    @Override
    public float v() {
        return this.v;
    }

    private static void requireInsideRange(float number, String name) throws IllegalArgumentException {
        if (number < 0.0f || 1.0f < number) {
            throw new IllegalArgumentException(name + " (" + number + ") is not inside the required range: [0,1]");
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HSVLikeImpl)) {
            return false;
        }
        HSVLikeImpl that = (HSVLikeImpl)other;
        return ShadyPines.equals(that.h, this.h) && ShadyPines.equals(that.s, this.s) && ShadyPines.equals(that.v, this.v);
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.h), Float.valueOf(this.s), Float.valueOf(this.v));
    }

    public String toString() {
        return Internals.toString(this);
    }
}

