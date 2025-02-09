/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChunkLightStorage
implements StorableObject {
    public static final byte[] FULL_LIGHT = new byte[2048];
    public static final byte[] EMPTY_LIGHT = new byte[2048];
    private static Constructor<?> fastUtilLongObjectHashMap;
    private final Map<Long, ChunkLight> storedLight = this.createLongObjectMap();

    public void setStoredLight(byte[][] skyLight, byte[][] blockLight, int x2, int z2) {
        this.storedLight.put(this.getChunkSectionIndex(x2, z2), new ChunkLight(skyLight, blockLight));
    }

    public ChunkLight getStoredLight(int x2, int z2) {
        return this.storedLight.get(this.getChunkSectionIndex(x2, z2));
    }

    public void clear() {
        this.storedLight.clear();
    }

    public void unloadChunk(int x2, int z2) {
        this.storedLight.remove(this.getChunkSectionIndex(x2, z2));
    }

    private long getChunkSectionIndex(int x2, int z2) {
        return ((long)x2 & 0x3FFFFFFL) << 38 | (long)z2 & 0x3FFFFFFL;
    }

    private Map<Long, ChunkLight> createLongObjectMap() {
        if (fastUtilLongObjectHashMap != null) {
            try {
                return (Map)fastUtilLongObjectHashMap.newInstance(new Object[0]);
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
                e2.printStackTrace();
            }
        }
        return new HashMap<Long, ChunkLight>();
    }

    static {
        Arrays.fill(FULL_LIGHT, (byte)-1);
        Arrays.fill(EMPTY_LIGHT, (byte)0);
        try {
            fastUtilLongObjectHashMap = Class.forName("com.viaversion.viaversion.libs.fastutil.longs.Long2ObjectOpenHashMap").getConstructor(new Class[0]);
        }
        catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
            // empty catch block
        }
    }

    public static class ChunkLight {
        private final byte[][] skyLight;
        private final byte[][] blockLight;

        public ChunkLight(byte[][] skyLight, byte[][] blockLight) {
            this.skyLight = skyLight;
            this.blockLight = blockLight;
        }

        public byte[][] skyLight() {
            return this.skyLight;
        }

        public byte[][] blockLight() {
            return this.blockLight;
        }
    }
}

