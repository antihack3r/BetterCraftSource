// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.data.entity;

import java.util.concurrent.ConcurrentHashMap;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import java.util.Map;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;

public final class StoredEntityDataImpl implements StoredEntityData
{
    private final Map<Class<?>, Object> storedObjects;
    private final EntityType type;
    
    public StoredEntityDataImpl(final EntityType type) {
        this.storedObjects = new ConcurrentHashMap<Class<?>, Object>();
        this.type = type;
    }
    
    @Override
    public EntityType type() {
        return this.type;
    }
    
    @Override
    public <T> T get(final Class<T> objectClass) {
        return (T)this.storedObjects.get(objectClass);
    }
    
    @Override
    public <T> T remove(final Class<T> objectClass) {
        return (T)this.storedObjects.remove(objectClass);
    }
    
    @Override
    public boolean has(final Class<?> objectClass) {
        return this.storedObjects.containsKey(objectClass);
    }
    
    @Override
    public void put(final Object object) {
        this.storedObjects.put(object.getClass(), object);
    }
}
