/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.labymod.api.protocol.chunk.ChunkCachingProtocol;
import net.labymod.api.protocol.chunk.Extracted;
import net.labymod.api.protocol.shadow.ShadowProtocol;

public interface ProtocolAdapter {
    public void onReceiveChunkPacket(Object var1, Object var2);

    public void loadChunk(ChunkCachingProtocol var1, Extracted var2, int var3, int var4, boolean var5);

    public void loadChunkBulk(ChunkCachingProtocol var1, Extracted[] var2, int[] var3, int[] var4);

    public boolean handleOutgoingPacket(Object var1, ShadowProtocol var2);
}

