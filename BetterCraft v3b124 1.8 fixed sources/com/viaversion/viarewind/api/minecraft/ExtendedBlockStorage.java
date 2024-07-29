/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.api.minecraft;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;

public class ExtendedBlockStorage {
    private final byte[] blockLSBArray = new byte[4096];
    private final NibbleArray blockMetadataArray = new NibbleArray(this.blockLSBArray.length);
    private final NibbleArray blockLightArray = new NibbleArray(this.blockLSBArray.length);
    private NibbleArray blockMSBArray;
    private NibbleArray skyLightArray;

    public ExtendedBlockStorage(boolean skylight) {
        if (skylight) {
            this.skyLightArray = new NibbleArray(this.blockLSBArray.length);
        }
    }

    public void setBlockId(int x2, int y2, int z2, int value) {
        this.blockLSBArray[ChunkSection.index((int)x2, (int)y2, (int)z2)] = (byte)(value & 0xFF);
        if (value > 255) {
            this.getOrCreateBlockMSBArray().set(x2, y2, z2, (value & 0xF00) >> 8);
        } else if (this.blockMSBArray != null) {
            this.blockMSBArray.set(x2, y2, z2, 0);
        }
    }

    public void setBlockMetadata(int x2, int y2, int z2, int value) {
        this.blockMetadataArray.set(x2, y2, z2, value);
    }

    public boolean hasBlockMSBArray() {
        return this.blockMSBArray != null;
    }

    public byte[] getBlockLSBArray() {
        return this.blockLSBArray;
    }

    public NibbleArray getOrCreateBlockMSBArray() {
        if (this.blockMSBArray == null) {
            this.blockMSBArray = new NibbleArray(this.blockLSBArray.length);
            return this.blockMSBArray;
        }
        return this.blockMSBArray;
    }

    public NibbleArray getBlockMetadataArray() {
        return this.blockMetadataArray;
    }

    public NibbleArray getBlockLightArray() {
        return this.blockLightArray;
    }

    public NibbleArray getSkyLightArray() {
        return this.skyLightArray;
    }
}

