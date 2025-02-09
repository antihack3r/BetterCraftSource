// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.datasync;

import java.io.IOException;
import net.minecraft.network.PacketBuffer;

public interface DataSerializer<T>
{
    void write(final PacketBuffer p0, final T p1);
    
    T read(final PacketBuffer p0) throws IOException;
    
    DataParameter<T> createKey(final int p0);
    
    T func_192717_a(final T p0);
}
