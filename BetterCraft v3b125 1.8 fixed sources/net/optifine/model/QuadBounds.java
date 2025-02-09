/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.model;

import net.minecraft.util.EnumFacing;

public class QuadBounds {
    private float minX = Float.MAX_VALUE;
    private float minY = Float.MAX_VALUE;
    private float minZ = Float.MAX_VALUE;
    private float maxX = -3.4028235E38f;
    private float maxY = -3.4028235E38f;
    private float maxZ = -3.4028235E38f;

    public QuadBounds(int[] vertexData) {
        int i2 = vertexData.length / 4;
        int j2 = 0;
        while (j2 < 4) {
            int k2 = j2 * i2;
            float f2 = Float.intBitsToFloat(vertexData[k2 + 0]);
            float f1 = Float.intBitsToFloat(vertexData[k2 + 1]);
            float f22 = Float.intBitsToFloat(vertexData[k2 + 2]);
            if (this.minX > f2) {
                this.minX = f2;
            }
            if (this.minY > f1) {
                this.minY = f1;
            }
            if (this.minZ > f22) {
                this.minZ = f22;
            }
            if (this.maxX < f2) {
                this.maxX = f2;
            }
            if (this.maxY < f1) {
                this.maxY = f1;
            }
            if (this.maxZ < f22) {
                this.maxZ = f22;
            }
            ++j2;
        }
    }

    public float getMinX() {
        return this.minX;
    }

    public float getMinY() {
        return this.minY;
    }

    public float getMinZ() {
        return this.minZ;
    }

    public float getMaxX() {
        return this.maxX;
    }

    public float getMaxY() {
        return this.maxY;
    }

    public float getMaxZ() {
        return this.maxZ;
    }

    public boolean isFaceQuad(EnumFacing face) {
        float f2;
        float f1;
        float f3;
        switch (face) {
            case DOWN: {
                f3 = this.getMinY();
                f1 = this.getMaxY();
                f2 = 0.0f;
                break;
            }
            case UP: {
                f3 = this.getMinY();
                f1 = this.getMaxY();
                f2 = 1.0f;
                break;
            }
            case NORTH: {
                f3 = this.getMinZ();
                f1 = this.getMaxZ();
                f2 = 0.0f;
                break;
            }
            case SOUTH: {
                f3 = this.getMinZ();
                f1 = this.getMaxZ();
                f2 = 1.0f;
                break;
            }
            case WEST: {
                f3 = this.getMinX();
                f1 = this.getMaxX();
                f2 = 0.0f;
                break;
            }
            case EAST: {
                f3 = this.getMinX();
                f1 = this.getMaxX();
                f2 = 1.0f;
                break;
            }
            default: {
                return false;
            }
        }
        return f3 == f2 && f1 == f2;
    }

    public boolean isFullQuad(EnumFacing face) {
        float f3;
        float f2;
        float f1;
        float f4;
        switch (face) {
            case DOWN: 
            case UP: {
                f4 = this.getMinX();
                f1 = this.getMaxX();
                f2 = this.getMinZ();
                f3 = this.getMaxZ();
                break;
            }
            case NORTH: 
            case SOUTH: {
                f4 = this.getMinX();
                f1 = this.getMaxX();
                f2 = this.getMinY();
                f3 = this.getMaxY();
                break;
            }
            case WEST: 
            case EAST: {
                f4 = this.getMinY();
                f1 = this.getMaxY();
                f2 = this.getMinZ();
                f3 = this.getMaxZ();
                break;
            }
            default: {
                return false;
            }
        }
        return f4 == 0.0f && f1 == 1.0f && f2 == 0.0f && f3 == 1.0f;
    }
}

