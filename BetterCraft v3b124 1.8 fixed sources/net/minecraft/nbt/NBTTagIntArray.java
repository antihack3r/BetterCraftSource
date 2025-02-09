/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;

public class NBTTagIntArray
extends NBTBase {
    private int[] intArray;

    NBTTagIntArray() {
    }

    public NBTTagIntArray(int[] p_i45132_1_) {
        this.intArray = p_i45132_1_;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeInt(this.intArray.length);
        int i2 = 0;
        while (i2 < this.intArray.length) {
            output.writeInt(this.intArray[i2]);
            ++i2;
        }
    }

    @Override
    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(192L);
        int i2 = input.readInt();
        sizeTracker.read(32 * i2);
        this.intArray = new int[i2];
        int j2 = 0;
        while (j2 < i2) {
            this.intArray[j2] = input.readInt();
            ++j2;
        }
    }

    @Override
    public byte getId() {
        return 11;
    }

    @Override
    public String toString() {
        String s2 = "[";
        int[] nArray = this.intArray;
        int n2 = this.intArray.length;
        int n3 = 0;
        while (n3 < n2) {
            int i2 = nArray[n3];
            s2 = String.valueOf(s2) + i2 + ",";
            ++n3;
        }
        return String.valueOf(s2) + "]";
    }

    @Override
    public NBTBase copy() {
        int[] aint = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
        return new NBTTagIntArray(aint);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return super.equals(p_equals_1_) ? Arrays.equals(this.intArray, ((NBTTagIntArray)p_equals_1_).intArray) : false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }

    public int[] getIntArray() {
        return this.intArray;
    }
}

