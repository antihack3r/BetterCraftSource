// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import java.util.Iterator;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class RegistrySimple<K, V> implements IRegistry<K, V>
{
    private static final Logger logger;
    protected final Map<K, V> registryObjects;
    
    static {
        logger = LogManager.getLogger();
    }
    
    public RegistrySimple() {
        this.registryObjects = this.createUnderlyingMap();
    }
    
    protected Map<K, V> createUnderlyingMap() {
        return (Map<K, V>)Maps.newHashMap();
    }
    
    @Override
    public V getObject(final K name) {
        return this.registryObjects.get(name);
    }
    
    @Override
    public void putObject(final K key, final V value) {
        Validate.notNull(key);
        Validate.notNull(value);
        if (this.registryObjects.containsKey(key)) {
            RegistrySimple.logger.debug("Adding duplicate key '" + key + "' to registry");
        }
        this.registryObjects.put(key, value);
    }
    
    public Set<K> getKeys() {
        return Collections.unmodifiableSet((Set<? extends K>)this.registryObjects.keySet());
    }
    
    public boolean containsKey(final K key) {
        return this.registryObjects.containsKey(key);
    }
    
    @Override
    public Iterator<V> iterator() {
        return this.registryObjects.values().iterator();
    }
}
