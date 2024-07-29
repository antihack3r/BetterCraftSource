/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.vector;

import net.minecraft.util.Vector3d;

public class FBPVector3d
extends Vector3d {
    public double z;
    public double y;
    public double x;

    public FBPVector3d() {
    }

    public FBPVector3d(double x2, double y2, double z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public FBPVector3d(FBPVector3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void copyFrom(Vector3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void add(Vector3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public FBPVector3d partialVec(FBPVector3d prevRot, float partialTicks) {
        FBPVector3d v2 = new FBPVector3d();
        v2.x = prevRot.x + (this.x - prevRot.x) * (double)partialTicks;
        v2.y = prevRot.y + (this.y - prevRot.y) * (double)partialTicks;
        v2.z = prevRot.z + (this.z - prevRot.z) * (double)partialTicks;
        return v2;
    }

    public FBPVector3d multiply(double d2) {
        FBPVector3d fbpVector3d;
        FBPVector3d v2 = fbpVector3d = new FBPVector3d(this);
        fbpVector3d.x *= d2;
        FBPVector3d fbpVector3d2 = v2;
        fbpVector3d2.y *= d2;
        FBPVector3d fbpVector3d3 = v2;
        fbpVector3d3.z *= d2;
        return v2;
    }
}

