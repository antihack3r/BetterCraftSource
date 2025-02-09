/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

public class BlockPosition {
    private int x;
    private int y;
    private int z;

    public BlockPosition(int x2, int y2, int z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void setX(int x2) {
        this.x = x2;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public void setZ(int z2) {
        this.z = z2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockPosition)) {
            return false;
        }
        BlockPosition pos = (BlockPosition)obj;
        return pos.getX() == this.x && pos.getY() == this.y && pos.getZ() == this.z;
    }
}

