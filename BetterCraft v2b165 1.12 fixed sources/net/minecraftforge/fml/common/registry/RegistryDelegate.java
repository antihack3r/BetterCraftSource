// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;

public interface RegistryDelegate<T>
{
    T get();
    
    ResourceLocation name();
    
    Class<T> type();
}
