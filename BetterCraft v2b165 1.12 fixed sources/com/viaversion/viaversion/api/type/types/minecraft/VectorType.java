// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.type.Type;

public class VectorType extends Type<Vector>
{
    public VectorType() {
        super(Vector.class);
    }
    
    @Override
    public Vector read(final ByteBuf buffer) throws Exception {
        final int x = Type.INT.read(buffer);
        final int y = Type.INT.read(buffer);
        final int z = Type.INT.read(buffer);
        return new Vector(x, y, z);
    }
    
    @Override
    public void write(final ByteBuf buffer, final Vector object) throws Exception {
        Type.INT.write(buffer, object.getBlockX());
        Type.INT.write(buffer, object.getBlockY());
        Type.INT.write(buffer, object.getBlockZ());
    }
}
