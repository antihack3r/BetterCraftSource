/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.chunks;

import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;

public final class DataPaletteImpl
implements DataPalette {
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private final IntList palette;
    private final Int2IntMap inversePalette;
    private final int sizeBits;
    private ChunkData values;

    public DataPaletteImpl(int valuesLength) {
        this(valuesLength, 16);
    }

    public DataPaletteImpl(int valuesLength, int initialSize) {
        this.values = new EmptyChunkData(valuesLength);
        this.sizeBits = Integer.numberOfTrailingZeros(valuesLength) / 3;
        this.palette = new IntArrayList(initialSize);
        this.inversePalette = new Int2IntOpenHashMap((int)((float)initialSize * 0.75f));
        this.inversePalette.defaultReturnValue(-1);
    }

    @Override
    public int index(int x2, int y2, int z2) {
        return (y2 << this.sizeBits | z2) << this.sizeBits | x2;
    }

    @Override
    public int idAt(int sectionCoordinate) {
        int index = this.values.get(sectionCoordinate);
        return this.palette.getInt(index);
    }

    @Override
    public void setIdAt(int sectionCoordinate, int id2) {
        int index = this.inversePalette.get(id2);
        if (index == -1) {
            index = this.palette.size();
            this.palette.add(id2);
            this.inversePalette.put(id2, index);
        }
        this.values.set(sectionCoordinate, index);
    }

    @Override
    public int paletteIndexAt(int packedCoordinate) {
        return this.values.get(packedCoordinate);
    }

    @Override
    public void setPaletteIndexAt(int sectionCoordinate, int index) {
        this.values.set(sectionCoordinate, index);
    }

    @Override
    public int size() {
        return this.palette.size();
    }

    @Override
    public int idByIndex(int index) {
        return this.palette.getInt(index);
    }

    @Override
    public void setIdByIndex(int index, int id2) {
        int oldId = this.palette.set(index, id2);
        if (oldId == id2) {
            return;
        }
        this.inversePalette.put(id2, index);
        if (this.inversePalette.get(oldId) == index) {
            this.inversePalette.remove(oldId);
            for (int i2 = 0; i2 < this.palette.size(); ++i2) {
                if (this.palette.getInt(i2) != oldId) continue;
                this.inversePalette.put(oldId, i2);
                break;
            }
        }
    }

    @Override
    public void replaceId(int oldId, int newId) {
        int index = this.inversePalette.remove(oldId);
        if (index == -1) {
            return;
        }
        this.inversePalette.put(newId, index);
        for (int i2 = 0; i2 < this.palette.size(); ++i2) {
            if (this.palette.getInt(i2) != oldId) continue;
            this.palette.set(i2, newId);
        }
    }

    @Override
    public void addId(int id2) {
        this.inversePalette.put(id2, this.palette.size());
        this.palette.add(id2);
    }

    @Override
    public void clear() {
        this.palette.clear();
        this.inversePalette.clear();
    }

    private static class ShortChunkData
    implements ChunkData {
        private final short[] data;

        public ShortChunkData(byte[] data) {
            this.data = new short[data.length];
            for (int i2 = 0; i2 < data.length; ++i2) {
                this.data[i2] = (short)(data[i2] & 0xFF);
            }
        }

        @Override
        public int get(int idx) {
            return this.data[idx];
        }

        @Override
        public void set(int idx, int val) {
            this.data[idx] = (short)val;
        }
    }

    private class ByteChunkData
    implements ChunkData {
        private final byte[] data;

        public ByteChunkData(int size) {
            this.data = new byte[size];
        }

        @Override
        public int get(int idx) {
            return this.data[idx] & 0xFF;
        }

        @Override
        public void set(int idx, int val) {
            if (val > 255) {
                DataPaletteImpl.this.values = new ShortChunkData(this.data);
                DataPaletteImpl.this.values.set(idx, val);
                return;
            }
            this.data[idx] = (byte)val;
        }
    }

    private class EmptyChunkData
    implements ChunkData {
        private final int size;

        public EmptyChunkData(int size) {
            this.size = size;
        }

        @Override
        public int get(int idx) {
            return 0;
        }

        @Override
        public void set(int idx, int val) {
            if (val != 0) {
                DataPaletteImpl.this.values = new ByteChunkData(this.size);
                DataPaletteImpl.this.values.set(idx, val);
            }
        }
    }

    static interface ChunkData {
        public int get(int var1);

        public void set(int var1, int var2);
    }
}

