/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import java.util.concurrent.ExecutorService;
import net.labymod.core.ServerPingerData;
import net.labymod.utils.Consumer;

public interface ServerPingerAdapter {
    public void pingServer(ExecutorService var1, long var2, String var4, Consumer<ServerPingerData> var5);

    public void pingServer(ServerPingerData var1, Consumer<ServerPingerData> var2) throws Throwable;

    public void tick();

    public void closePendingConnections();
}

