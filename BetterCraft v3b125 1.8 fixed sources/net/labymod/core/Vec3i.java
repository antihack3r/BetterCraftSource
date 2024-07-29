/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.labymod.core.LabyModCore;

public class Vec3i
implements Comparable<Vec3i> {
    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int xIn, int yIn, int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public Vec3i(double xIn, double yIn, double zIn) {
        this(LabyModCore.getMath().floor_double(xIn), LabyModCore.getMath().floor_double(yIn), LabyModCore.getMath().floor_double(zIn));
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof Vec3i)) {
            return false;
        }
        Vec3i vec3i = (Vec3i)p_equals_1_;
        return this.getX() == vec3i.getX() && this.getY() == vec3i.getY() && this.getZ() == vec3i.getZ();
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public int compareTo(Vec3i p_compareTo_1_) {
        return this.getY() == p_compareTo_1_.getY() ? (this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ()) : this.getY() - p_compareTo_1_.getY();
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

    public Vec3i crossProduct(Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public double distanceSq(double toX, double toY, double toZ) {
        double d0 = (double)this.getX() - toX;
        double d2 = (double)this.getY() - toY;
        double d3 = (double)this.getZ() - toZ;
        return d0 * d0 + d2 * d2 + d3 * d3;
    }

    public double distanceSqToCenter(double xIn, double yIn, double zIn) {
        double d0 = (double)this.getX() + 0.5 - xIn;
        double d2 = (double)this.getY() + 0.5 - yIn;
        double d3 = (double)this.getZ() + 0.5 - zIn;
        return d0 * d0 + d2 * d2 + d3 * d3;
    }

    public double distanceSq(Vec3i to2) {
        return this.distanceSq(to2.getX(), to2.getY(), to2.getZ());
    }

    public String toString() {
        return LabyModCore.getMinecraft().vectoString(this);
    }
}

