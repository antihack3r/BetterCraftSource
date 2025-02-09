// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.api.platform;

import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;

public interface ViaInjector
{
    void inject() throws Exception;
    
    void uninject() throws Exception;
    
    default boolean lateProtocolVersionSetting() {
        return false;
    }
    
    int getServerProtocolVersion() throws Exception;
    
    default IntSortedSet getServerProtocolVersions() throws Exception {
        return IntSortedSets.singleton(this.getServerProtocolVersion());
    }
    
    String getEncoderName();
    
    String getDecoderName();
    
    JsonObject getDump();
}
