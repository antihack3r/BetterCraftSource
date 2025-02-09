/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import com.viaversion.viaversion.libs.kyori.adventure.internal.properties.AdventureProperties;
import com.viaversion.viaversion.libs.kyori.adventure.util.Services0;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;

public final class Services {
    private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.TRUE.equals(AdventureProperties.SERVICE_LOAD_FAILURES_ARE_FATAL.value());

    private Services() {
    }

    @NotNull
    public static <P> Optional<P> service(@NotNull Class<P> type) {
        ServiceLoader<P> loader = Services0.loader(type);
        Iterator<P> it2 = loader.iterator();
        while (it2.hasNext()) {
            P instance;
            try {
                instance = it2.next();
            }
            catch (Throwable t2) {
                if (!SERVICE_LOAD_FAILURES_ARE_FATAL) continue;
                throw new IllegalStateException("Encountered an exception loading service " + type, t2);
            }
            if (it2.hasNext()) {
                throw new IllegalStateException("Expected to find one service " + type + ", found multiple");
            }
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    @NotNull
    public static <P> Optional<P> serviceWithFallback(@NotNull Class<P> type) {
        ServiceLoader<P> loader = Services0.loader(type);
        Iterator<P> it2 = loader.iterator();
        Object firstFallback = null;
        while (it2.hasNext()) {
            P instance;
            try {
                instance = it2.next();
            }
            catch (Throwable t2) {
                if (!SERVICE_LOAD_FAILURES_ARE_FATAL) continue;
                throw new IllegalStateException("Encountered an exception loading service " + type, t2);
            }
            if (instance instanceof Fallback) {
                if (firstFallback != null) continue;
                firstFallback = instance;
                continue;
            }
            return Optional.of(instance);
        }
        return Optional.ofNullable(firstFallback);
    }

    public static interface Fallback {
    }
}

