// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry<T>
{
    private final T value;
    
    public UserListEntry(final T valueIn) {
        this.value = valueIn;
    }
    
    protected UserListEntry(final T valueIn, final JsonObject json) {
        this.value = valueIn;
    }
    
    T getValue() {
        return this.value;
    }
    
    boolean hasBanExpired() {
        return false;
    }
    
    protected void onSerialization(final JsonObject data) {
    }
}
