/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PooledByteBuf;

final class PoolChunk<T> {
    final PoolArena<T> arena;
    final T memory;
    final boolean unpooled;
    private final byte[] memoryMap;
    private final byte[] depthMap;
    private final PoolSubpage<T>[] subpages;
    private final int subpageOverflowMask;
    private final int pageSize;
    private final int pageShifts;
    private final int maxOrder;
    private final int chunkSize;
    private final int log2ChunkSize;
    private final int maxSubpageAllocs;
    private final byte unusable;
    private int freeBytes;
    PoolChunkList<T> parent;
    PoolChunk<T> prev;
    PoolChunk<T> next;

    PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
        this.unpooled = false;
        this.arena = arena;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.maxOrder = maxOrder;
        this.chunkSize = chunkSize;
        this.unusable = (byte)(maxOrder + 1);
        this.log2ChunkSize = PoolChunk.log2(chunkSize);
        this.subpageOverflowMask = ~(pageSize - 1);
        this.freeBytes = chunkSize;
        assert (maxOrder < 30) : "maxOrder should be < 30, but is: " + maxOrder;
        this.maxSubpageAllocs = 1 << maxOrder;
        this.memoryMap = new byte[this.maxSubpageAllocs << 1];
        this.depthMap = new byte[this.memoryMap.length];
        int memoryMapIndex = 1;
        for (int d2 = 0; d2 <= maxOrder; ++d2) {
            int depth = 1 << d2;
            for (int p2 = 0; p2 < depth; ++p2) {
                this.memoryMap[memoryMapIndex] = (byte)d2;
                this.depthMap[memoryMapIndex] = (byte)d2;
                ++memoryMapIndex;
            }
        }
        this.subpages = this.newSubpageArray(this.maxSubpageAllocs);
    }

    PoolChunk(PoolArena<T> arena, T memory, int size) {
        this.unpooled = true;
        this.arena = arena;
        this.memory = memory;
        this.memoryMap = null;
        this.depthMap = null;
        this.subpages = null;
        this.subpageOverflowMask = 0;
        this.pageSize = 0;
        this.pageShifts = 0;
        this.maxOrder = 0;
        this.unusable = (byte)(this.maxOrder + 1);
        this.chunkSize = size;
        this.log2ChunkSize = PoolChunk.log2(this.chunkSize);
        this.maxSubpageAllocs = 0;
    }

    private PoolSubpage<T>[] newSubpageArray(int size) {
        return new PoolSubpage[size];
    }

    int usage() {
        int freeBytes = this.freeBytes;
        if (freeBytes == 0) {
            return 100;
        }
        int freePercentage = (int)((long)freeBytes * 100L / (long)this.chunkSize);
        if (freePercentage == 0) {
            return 99;
        }
        return 100 - freePercentage;
    }

    long allocate(int normCapacity) {
        if ((normCapacity & this.subpageOverflowMask) != 0) {
            return this.allocateRun(normCapacity);
        }
        return this.allocateSubpage(normCapacity);
    }

    private void updateParentsAlloc(int id2) {
        while (id2 > 1) {
            byte val2;
            int parentId = id2 >>> 1;
            byte val1 = this.value(id2);
            byte val = val1 < (val2 = this.value(id2 ^ 1)) ? val1 : val2;
            this.setValue(parentId, val);
            id2 = parentId;
        }
    }

    private void updateParentsFree(int id2) {
        int logChild = this.depth(id2) + 1;
        while (id2 > 1) {
            int parentId = id2 >>> 1;
            byte val1 = this.value(id2);
            byte val2 = this.value(id2 ^ 1);
            if (val1 == --logChild && val2 == logChild) {
                this.setValue(parentId, (byte)(logChild - 1));
            } else {
                byte val = val1 < val2 ? val1 : val2;
                this.setValue(parentId, val);
            }
            id2 = parentId;
        }
    }

    private int allocateNode(int d2) {
        int id2 = 1;
        int initial = -(1 << d2);
        byte val = this.value(id2);
        if (val > d2) {
            return -1;
        }
        while (val < d2 || (id2 & initial) == 0) {
            val = this.value(id2 <<= 1);
            if (val <= d2) continue;
            val = this.value(id2 ^= 1);
        }
        byte value = this.value(id2);
        assert (value == d2 && (id2 & initial) == 1 << d2) : String.format("val = %d, id & initial = %d, d = %d", value, id2 & initial, d2);
        this.setValue(id2, this.unusable);
        this.updateParentsAlloc(id2);
        return id2;
    }

    private long allocateRun(int normCapacity) {
        int d2 = this.maxOrder - (PoolChunk.log2(normCapacity) - this.pageShifts);
        int id2 = this.allocateNode(d2);
        if (id2 < 0) {
            return id2;
        }
        this.freeBytes -= this.runLength(id2);
        return id2;
    }

    private long allocateSubpage(int normCapacity) {
        int d2 = this.maxOrder;
        int id2 = this.allocateNode(d2);
        if (id2 < 0) {
            return id2;
        }
        PoolSubpage<T>[] subpages = this.subpages;
        int pageSize = this.pageSize;
        this.freeBytes -= pageSize;
        int subpageIdx = this.subpageIdx(id2);
        PoolSubpage<T> subpage = subpages[subpageIdx];
        if (subpage == null) {
            subpage = new PoolSubpage(this, id2, this.runOffset(id2), pageSize, normCapacity);
            subpages[subpageIdx] = subpage;
        } else {
            subpage.init(normCapacity);
        }
        return subpage.allocate();
    }

    void free(long handle) {
        int memoryMapIdx = (int)handle;
        int bitmapIdx = (int)(handle >>> 32);
        if (bitmapIdx != 0) {
            PoolSubpage<T> subpage = this.subpages[this.subpageIdx(memoryMapIdx)];
            assert (subpage != null && subpage.doNotDestroy);
            if (subpage.free(bitmapIdx & 0x3FFFFFFF)) {
                return;
            }
        }
        this.freeBytes += this.runLength(memoryMapIdx);
        this.setValue(memoryMapIdx, this.depth(memoryMapIdx));
        this.updateParentsFree(memoryMapIdx);
    }

    void initBuf(PooledByteBuf<T> buf, long handle, int reqCapacity) {
        int memoryMapIdx = (int)handle;
        int bitmapIdx = (int)(handle >>> 32);
        if (bitmapIdx == 0) {
            byte val = this.value(memoryMapIdx);
            assert (val == this.unusable) : String.valueOf(val);
            buf.init(this, handle, this.runOffset(memoryMapIdx), reqCapacity, this.runLength(memoryMapIdx));
        } else {
            this.initBufWithSubpage(buf, handle, bitmapIdx, reqCapacity);
        }
    }

    void initBufWithSubpage(PooledByteBuf<T> buf, long handle, int reqCapacity) {
        this.initBufWithSubpage(buf, handle, (int)(handle >>> 32), reqCapacity);
    }

    private void initBufWithSubpage(PooledByteBuf<T> buf, long handle, int bitmapIdx, int reqCapacity) {
        assert (bitmapIdx != 0);
        int memoryMapIdx = (int)handle;
        PoolSubpage<T> subpage = this.subpages[this.subpageIdx(memoryMapIdx)];
        assert (subpage.doNotDestroy);
        assert (reqCapacity <= subpage.elemSize);
        buf.init(this, handle, this.runOffset(memoryMapIdx) + (bitmapIdx & 0x3FFFFFFF) * subpage.elemSize, reqCapacity, subpage.elemSize);
    }

    private byte value(int id2) {
        return this.memoryMap[id2];
    }

    private void setValue(int id2, byte val) {
        this.memoryMap[id2] = val;
    }

    private byte depth(int id2) {
        return this.depthMap[id2];
    }

    private static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    private int runLength(int id2) {
        return 1 << this.log2ChunkSize - this.depth(id2);
    }

    private int runOffset(int id2) {
        int shift = id2 ^ 1 << this.depth(id2);
        return shift * this.runLength(id2);
    }

    private int subpageIdx(int memoryMapIdx) {
        return memoryMapIdx ^ this.maxSubpageAllocs;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Chunk(");
        buf.append(Integer.toHexString(System.identityHashCode(this)));
        buf.append(": ");
        buf.append(this.usage());
        buf.append("%, ");
        buf.append(this.chunkSize - this.freeBytes);
        buf.append('/');
        buf.append(this.chunkSize);
        buf.append(')');
        return buf.toString();
    }
}

