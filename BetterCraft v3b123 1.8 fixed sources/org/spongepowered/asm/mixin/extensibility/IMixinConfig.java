// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.asm.mixin.extensibility;

import java.util.Set;
import org.spongepowered.asm.mixin.MixinEnvironment;

public interface IMixinConfig
{
    public static final int DEFAULT_PRIORITY = 1000;
    
    MixinEnvironment getEnvironment();
    
    String getName();
    
    String getMixinPackage();
    
    int getPriority();
    
    IMixinConfigPlugin getPlugin();
    
    boolean isRequired();
    
    Set<String> getTargets();
    
     <V> void decorate(final String p0, final V p1);
    
    boolean hasDecoration(final String p0);
    
     <V> V getDecoration(final String p0);
}
