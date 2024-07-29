/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.chunk;

import java.beans.ConstructorProperties;

public class Extracted {
    public short dataSize;
    public byte[] data;
    public int decompressedLength;

    @ConstructorProperties(value={"dataSize", "data", "decompressedLength"})
    public Extracted(short dataSize, byte[] data, int decompressedLength) {
        this.dataSize = dataSize;
        this.data = data;
        this.decompressedLength = decompressedLength;
    }
}

