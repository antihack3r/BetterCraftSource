/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.viaversion.viaversion.libs.kyori.adventure.bossbar;

import com.viaversion.viaversion.libs.kyori.adventure.bossbar.BossBar;
import com.viaversion.viaversion.libs.kyori.adventure.bossbar.BossBarImpl;
import com.viaversion.viaversion.libs.kyori.adventure.bossbar.BossBarViewer;
import java.util.Collections;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface BossBarImplementation {
    @ApiStatus.Internal
    @NotNull
    public static <I extends BossBarImplementation> I get(@NotNull BossBar bar, @NotNull Class<I> type) {
        return BossBarImpl.ImplementationAccessor.get(bar, type);
    }

    @ApiStatus.Internal
    @NotNull
    default public Iterable<? extends BossBarViewer> viewers() {
        return Collections.emptyList();
    }

    @ApiStatus.Internal
    public static interface Provider {
        @ApiStatus.Internal
        @NotNull
        public BossBarImplementation create(@NotNull BossBar var1);
    }
}

