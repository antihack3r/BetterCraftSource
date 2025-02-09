// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class NBTTagByte extends NBTPrimitive
{
    private byte data;
    
    NBTTagByte() {
    }
    
    public NBTTagByte(final byte data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput output) throws IOException {
        output.writeByte(this.data);
    }
    
    @Override
    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(72L);
        this.data = input.readByte();
    }
    
    @Override
    public byte getId() {
        return 1;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.data) + "b";
    }
    
    @Override
    public NBTTagByte copy() {
        return new NBTTagByte(this.data);
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        return super.equals(p_equals_1_) && this.data == ((NBTTagByte)p_equals_1_).data;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }
    
    @Override
    public long getLong() {
        return this.data;
    }
    
    @Override
    public int getInt() {
        return this.data;
    }
    
    @Override
    public short getShort() {
        return this.data;
    }
    
    @Override
    public byte getByte() {
        return this.data;
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
