/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

public class CacheLocal {
    private int maxX = 18;
    private int maxY = 128;
    private int maxZ = 18;
    private int offsetX = 0;
    private int offsetY = 0;
    private int offsetZ = 0;
    private int[][][] cache = null;
    private int[] lastZs = null;
    private int lastDz = 0;

    public CacheLocal(int maxX, int maxY, int maxZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.cache = new int[maxX][maxY][maxZ];
        this.resetCache();
    }

    public void resetCache() {
        int i2 = 0;
        while (i2 < this.maxX) {
            int[][] aint = this.cache[i2];
            int j2 = 0;
            while (j2 < this.maxY) {
                int[] aint1 = aint[j2];
                int k2 = 0;
                while (k2 < this.maxZ) {
                    aint1[k2] = -1;
                    ++k2;
                }
                ++j2;
            }
            ++i2;
        }
    }

    public void setOffset(int x2, int y2, int z2) {
        this.offsetX = x2;
        this.offsetY = y2;
        this.offsetZ = z2;
        this.resetCache();
    }

    public int get(int x2, int y2, int z2) {
        try {
            this.lastZs = this.cache[x2 - this.offsetX][y2 - this.offsetY];
            this.lastDz = z2 - this.offsetZ;
            return this.lastZs[this.lastDz];
        }
        catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
            arrayindexoutofboundsexception.printStackTrace();
            return -1;
        }
    }

    public void setLast(int val) {
        try {
            this.lastZs[this.lastDz] = val;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

