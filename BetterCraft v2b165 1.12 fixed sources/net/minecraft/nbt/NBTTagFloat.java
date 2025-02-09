// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.nbt;

import net.minecraft.util.math.MathHelper;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class NBTTagFloat extends NBTPrimitive
{
    private float data;
    
    NBTTagFloat() {
    }
    
    public NBTTagFloat(final float data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput output) throws IOException {
        output.writeFloat(this.data);
    }
    
    @Override
    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(96L);
        this.data = input.readFloat();
    }
    
    @Override
    public byte getId() {
        return 5;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.data) + "f";
    }
    
    @Override
    public NBTTagFloat copy() {
        return new NBTTagFloat(this.data);
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        return super.equals(p_equals_1_) && this.data == ((NBTTagFloat)p_equals_1_).data;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }
    
    @Override
    public long getLong() {
        return (long)this.data;
    }
    
    @Override
    public int getInt() {
        return MathHelper.floor(this.data);
    }
    
    @Override
    public short getShort() {
        return (short)(MathHelper.floor(this.data) & 0xFFFF);
    }
    
    @Override
    public byte getByte() {
        return (byte)(MathHelper.floor(this.data) & 0xFF);
    }
    
    @Override
    public double getDouble() {
        return this.data;
    }
    
    @Override
    public float getFloat() {
        return this.data;
    }
}
