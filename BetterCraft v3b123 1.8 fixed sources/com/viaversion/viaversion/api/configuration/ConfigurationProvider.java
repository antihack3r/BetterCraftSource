// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.api.configuration;

import java.util.Map;

public interface ConfigurationProvider
{
    void set(final String p0, final Object p1);
    
    void saveConfig();
    
    void reloadConfig();
    
    Map<String, Object> getValues();
}
