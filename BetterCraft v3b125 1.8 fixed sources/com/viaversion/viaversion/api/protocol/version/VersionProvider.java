/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.protocol.version;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;

@FunctionalInterface
public interface VersionProvider
extends Provider {
    public int getClosestServerProtocol(UserConnection var1) throws Exception;
}

