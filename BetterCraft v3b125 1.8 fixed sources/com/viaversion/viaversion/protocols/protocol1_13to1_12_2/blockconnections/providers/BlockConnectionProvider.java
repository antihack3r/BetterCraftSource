/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.UserBlockData;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlockConnectionProvider
implements Provider {
    public int getBlockData(UserConnection connection, int x2, int y2, int z2) {
        int oldId = this.getWorldBlockData(connection, x2, y2, z2);
        return Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId);
    }

    public int getWorldBlockData(UserConnection connection, int x2, int y2, int z2) {
        return -1;
    }

    public void storeBlock(UserConnection connection, int x2, int y2, int z2, int blockState) {
    }

    public void removeBlock(UserConnection connection, int x2, int y2, int z2) {
    }

    public void clearStorage(UserConnection connection) {
    }

    public void modifiedBlock(UserConnection connection, Position position) {
    }

    public void unloadChunk(UserConnection connection, int x2, int z2) {
    }

    public void unloadChunkSection(UserConnection connection, int chunkX, int chunkY, int chunkZ) {
    }

    public boolean storesBlocks(UserConnection user, @Nullable Position position) {
        return false;
    }

    public UserBlockData forUser(UserConnection connection) {
        return (x2, y2, z2) -> this.getBlockData(connection, x2, y2, z2);
    }
}

