/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.identity;

import com.viaversion.viaversion.libs.kyori.adventure.identity.Identity;
import com.viaversion.viaversion.libs.kyori.adventure.internal.Internals;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class IdentityImpl
implements Examinable,
Identity {
    private final UUID uuid;

    IdentityImpl(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    @NotNull
    public UUID uuid() {
        return this.uuid;
    }

    public String toString() {
        return Internals.toString(this);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Identity)) {
            return false;
        }
        Identity that = (Identity)other;
        return this.uuid.equals(that.uuid());
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }
}

