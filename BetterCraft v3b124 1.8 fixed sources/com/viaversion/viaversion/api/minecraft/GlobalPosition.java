/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

import com.viaversion.viaversion.api.minecraft.Position;

public final class GlobalPosition
extends Position {
    private final String dimension;

    public GlobalPosition(String dimension, int x2, int y2, int z2) {
        super(x2, y2, z2);
        this.dimension = dimension;
    }

    public String dimension() {
        return this.dimension;
    }

    @Override
    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        GlobalPosition position = (GlobalPosition)o2;
        if (this.x != position.x) {
            return false;
        }
        if (this.y != position.y) {
            return false;
        }
        if (this.z != position.z) {
            return false;
        }
        return this.dimension.equals(position.dimension);
    }

    @Override
    public int hashCode() {
        int result = this.dimension.hashCode();
        result = 31 * result + this.x;
        result = 31 * result + this.y;
        result = 31 * result + this.z;
        return result;
    }

    @Override
    public String toString() {
        return "GlobalPosition{dimension='" + this.dimension + '\'' + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}

